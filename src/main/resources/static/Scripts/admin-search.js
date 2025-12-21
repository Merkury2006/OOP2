// Файл: /static/js/admin-search.js

document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const clearBtn = document.getElementById('clearBtn');
    const usersTableContent = document.getElementById('usersTableContent');
    const totalUsersElement = document.getElementById('totalUsers');

    let currentSearch = '';
    let isLoading = false;
    let searchTimeout;

    // Инициализация
    if (searchInput && usersTableContent) {
        initSearch();
    }

    function initSearch() {
        // Загружаем пользователей при загрузке страницы
        loadUsers('');

        // Обработчики событий
        if (searchInput) {
            searchInput.addEventListener('input', handleSearchInput);
            searchInput.addEventListener('keypress', handleKeyPress);
        }

        if (searchBtn) {
            searchBtn.addEventListener('click', () => performSearch(searchInput.value));
        }

        if (clearBtn) {
            clearBtn.addEventListener('click', clearSearch);
        }
    }

    function handleSearchInput(e) {
        clearTimeout(searchTimeout);
        const value = e.target.value.trim();

        searchTimeout = setTimeout(() => {
            if (value !== currentSearch) {
                performSearch(value);
            }
        }, 500);
    }

    function handleKeyPress(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            performSearch(searchInput.value.trim());
        }
    }

    function performSearch(searchTerm) {
        if (isLoading) return;

        currentSearch = searchTerm;
        loadUsers(searchTerm);

        // Обновляем URL без перезагрузки страницы
        updateUrl(searchTerm);
    }

    function clearSearch() {
        if (searchInput) {
            searchInput.value = '';
        }
        performSearch('');
    }

    async function loadUsers(searchTerm) {
        if (isLoading) return;

        isLoading = true;
        showLoading();

        try {
            const params = new URLSearchParams();
            if (searchTerm) {
                params.append('search', searchTerm);
            }

            const response = await fetch(`/api/users/search?${params.toString()}`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();

            if (!result.success) {
                throw new Error(result.message || 'Ошибка при получении данных');
            }

            const data = result.data;

            renderUsers(data);

            // Обновляем счетчик
            if (totalUsersElement && data.totalUsers !== undefined) {
                totalUsersElement.textContent = data.totalUsers;
            }

        } catch (error) {
            console.error('Ошибка загрузки пользователей:', error);
            showError('Ошибка загрузки данных: ' + error.message);
        } finally {
            isLoading = false;
        }
    }

    function renderUsers(searchData) {
        console.log('Rendering with searchData:', searchData);

        if (!usersTableContent) return;

        // Если нет пользователей
        if (!searchData || !searchData.users || searchData.users.length === 0) {
            const message = currentSearch ? 'Пользователи не найдены' : 'В системе пока нет пользователей';
            const submessage = currentSearch
                ? 'Попробуйте изменить параметры поиска'
                : 'Зарегистрируйте первого пользователя';

            usersTableContent.innerHTML = `
                <div class="empty-data-state">
                    <div class="empty-data-icon">👥</div>
                    <div class="empty-data-message">${message}</div>
                    <div class="empty-data-submessage">${submessage}</div>
                    ${currentSearch ? '<button onclick="clearSearch()" class="btn secondary" style="margin-top: 20px;">Очистить поиск</button>' : ''}
                </div>
            `;
            return;
        }

        // Рендерим таблицу с пользователями
        let html = `
            <div class="users-table-container">
                <table class="users-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Имя пользователя</th>
                            <th>Email</th>
                            <th>Роль</th>
                            <th>Статус email</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        // Проходим по всем пользователям и создаем строки таблицы
        searchData.users.forEach(user => {
            const isCurrentUser = user.id === searchData.currentUserId;
            const roleValue = user.role;
            const roleStr = typeof roleValue === 'object' ? roleValue.name : String(roleValue);
            const roleClass = roleStr === 'ADMIN' ? 'admin-badge' : 'user-badge';
            const statusIcon = user.emailVerified ? '✅' : '❌';
            const statusClass = user.emailVerified ? 'verified' : 'not-verified';

            html += `
                <tr ${isCurrentUser ? 'class="current-user"' : ''}>
                    <td>${user.id}</td>
                    <td>${escapeHtml(user.username || '')}</td>
                    <td>${escapeHtml(user.email || '')}</td>
                    <td>
                        <span class="role-badge ${roleClass}">${roleStr || 'USER'}</span>
                    </td>
                    <td>
                        <span class="status ${statusClass}">${statusIcon}</span>
                    </td>
                    <td>
                        <div class="action-buttons">
            `;

            if (!isCurrentUser) {
                const newRole = roleStr === 'ADMIN' ? 'USER' : 'ADMIN';
                const buttonText = roleStr === 'ADMIN' ? '↓ USER' : '↑ ADMIN';

                html += `
                    <form method="post" action="/admin/users/${user.id}/role" 
                          onsubmit="return window.confirmRoleChange('${escapeHtml(user.username || '')}', '${newRole}')">
                        <input type="hidden" name="_csrf" value="${getCsrfToken()}">
                        <input type="hidden" name="search" value="${currentSearch}">
                        <button type="submit" class="btn-small warning">${buttonText}</button>
                    </form>
                    
                    <form method="post" action="/admin/users/${user.id}/delete" 
                          onsubmit="return window.confirmDelete('${escapeHtml(user.username || '')}')">
                        <input type="hidden" name="_csrf" value="${getCsrfToken()}">
                        <input type="hidden" name="search" value="${currentSearch}">
                        <button type="submit" class="btn-small danger">🗑️</button>
                    </form>
                `;
            } else {
                html += `<span class="text-muted">Это вы</span>`;
            }

            html += `
                        </div>
                    </td>
                </tr>
            `;
        });

        html += `
                    </tbody>
                </table>
            </div>
        `;

        usersTableContent.innerHTML = html;
    }

    // Получение CSRF токена
    function getCsrfToken() {
        return document.querySelector('meta[name="_csrf"]')?.content ||
            document.querySelector('input[name="_csrf"]')?.value ||
            '';
    }

    function showLoading() {
        if (usersTableContent) {
            usersTableContent.innerHTML = `
                <div class="loading-state">
                    <div class="spinner"></div>
                    <p>Загрузка пользователей...</p>
                </div>
            `;
        }
    }

    function showError(message) {
        if (usersTableContent) {
            usersTableContent.innerHTML = `
                <div class="error-data-state">
                    <div class="error-data-icon">❌</div>
                    <div class="error-data-message">${escapeHtml(message)}</div>
                    <button onclick="location.reload()" class="btn secondary" style="margin-top: 20px;">Перезагрузить страницу</button>
                </div>
            `;
        }
    }

    window.clearSearch = function() {
        if (searchInput) {
            searchInput.value = '';
        }
        performSearch('');
    };

    function updateUrl(searchTerm) {
        const url = new URL(window.location);

        if (searchTerm) {
            url.searchParams.set('search', searchTerm);
        } else {
            url.searchParams.delete('search');
        }

        window.history.pushState({ search: searchTerm }, '', url);
    }

    function escapeHtml(text) {
        if (text === null || text === undefined) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Функции подтверждения
    window.confirmRoleChange = function(username, newRole) {
        const action = newRole === 'ADMIN' ? 'повысить до ADMIN' : 'понизить до USER';
        return confirm(`Вы уверены, что хотите ${action} пользователя "${username}"?`);
    };

    window.confirmDelete = function(username) {
        return confirm(`Вы уверены, что хотите удалить пользователя "${username}"?`);
    };

    // Обработчик истории браузера
    window.addEventListener('popstate', function(event) {
        if (event.state && event.state.search !== undefined) {
            if (searchInput) {
                searchInput.value = event.state.search;
            }
            performSearch(event.state.search);
        }
    });
});

// Добавляем стили для спиннера
(function() {
    const style = document.createElement('style');
    style.textContent = `
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    `;
    document.head.appendChild(style);
})();