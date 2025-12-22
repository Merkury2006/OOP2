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

            const response = await fetch(`/admin/users/search?${params.toString()}`, {
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
                const buttonTitle = roleStr === 'ADMIN' ? 'Понизить до обычного пользователя' : 'Повысить до администратора';

                html += `
                    <button type="button" class="btn-small warning" 
                            onclick="changeUserRole(${user.id}, '${escapeHtml(user.username || '')}', '${newRole}')"
                            title="${buttonTitle}">
                        ${buttonText}
                    </button>
                    
                    <button type="button" class="btn-small danger" 
                            onclick="deleteUser(${user.id}, '${escapeHtml(user.username || '')}')"
                            title="Удалить пользователя">
                        🗑️
                    </button>
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

    // Функция для обновления таблицы после действия
    function refreshTable() {
        loadUsers(currentSearch);
    }

    // =============================================
    // ГЛОБАЛЬНЫЕ ФУНКЦИИ ДЛЯ УПРАВЛЕНИЯ ПОЛЬЗОВАТЕЛЯМИ
    // =============================================

    // Изменить роль пользователя
    window.changeUserRole = async function(userId, username, newRole) {
        const action = newRole === 'ADMIN' ? 'повысить до ADMIN' : 'понизить до USER';

        if (!confirm(`Вы уверены, что хотите ${action} пользователя "${username}"?`)) {
            return;
        }

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

            const params = new URLSearchParams();
            params.append('newRole', newRole);

            const headers = {
                'Accept': 'application/json',
                'Content-Type': 'application/x-www-form-urlencoded',
                ...(csrfToken && csrfHeader && { [csrfHeader]: csrfToken })
            };


            const response = await fetch(`/admin/users/${userId}/role`, {
                method: 'POST',
                credentials: 'include',
                headers: headers,
                body: params.toString()
            });

            const result = await response.json();

            if (result.success) {
                showSuccessMessage(`Роль пользователя ${username} успешно изменена на ${newRole}`);
                refreshTable(); // Обновляем таблицу
            } else if (result.status === 403) {
                showErrorMessage(result.message || 'Доступ запрещен. Нельзя изменить свою собственную роль.');
            } else if (result.status === 404) {
                showErrorMessage(result.message || 'Пользователь не найден. Возможно, он был удален.');
                refreshTable();
            } else {
                showErrorMessage(result.message || 'Ошибка при изменении роли');
            }

        } catch (error) {
            console.error('Ошибка при изменении роли:', error);
            showErrorMessage('Ошибка при изменении роли: ' + error.message);
        }
    };

    // Удалить пользователя
    window.deleteUser = async function(userId, username) {
        if (!confirm(`Вы уверены, что хотите удалить пользователя "${username}"? Это действие необратимо!`)) {
            return;
        }

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

            const headers = {
                'Accept': 'application/json',
                ...(csrfToken && csrfHeader && { [csrfHeader]: csrfToken })
            };

            const response = await fetch(`/admin/users/${userId}`, {
                method: 'DELETE',
                credentials: 'include',
                headers: headers
            });

            const result = await response.json();

            if (result.success) {
                showSuccessMessage(`Пользователь ${username} успешно удален`);
                refreshTable();
            } else if (result.status === 403) {
                let errorMessage = result.message || 'Доступ запрещен.';
                if (result.message && result.message.includes('самого себя')) {
                    errorMessage = 'Вы не можете удалить свой собственный аккаунт.';
                } else if (result.message && result.message.includes('другого админа')) {
                    errorMessage = 'Вы не можете удалить другого администратора.';
                }

                showErrorMessage(errorMessage);
            } else if (result.status === 404) {
                showErrorMessage(result.message || 'Пользователь не найден. Возможно, он уже был удален.');
                refreshTable();
            } else {
                showErrorMessage(result.message || `Ошибка при удалении пользователя (код: ${response.status})`);
            }

        } catch (error) {
            console.error('Ошибка при удалении пользователя:', error);
            showErrorMessage('Ошибка при удалении пользователя: ' + error.message);
        }
    };

    // Очистить поиск
    window.clearSearch = function() {
        if (searchInput) {
            searchInput.value = '';
        }
        performSearch('');
    };

    // Получение CSRF токена
    function getCsrfToken() {
        return document.querySelector('meta[name="_csrf"]')?.content ||
            document.querySelector('input[name="_csrf"]')?.value ||
            '';
    }

    // Показать сообщение об успехе
    function showSuccessMessage(message) {
        showMessage(message, 'success');
    }

    // Показать сообщение об ошибке
    function showErrorMessage(message) {
        showMessage(message, 'error');
    }

    // Показать сообщение
    function showMessage(message, type) {
        // Создаем элемент сообщения
        const messageDiv = document.createElement('div');
        messageDiv.className = `admin-message ${type === 'error' ? 'error' : ''}`;
        messageDiv.innerHTML = `
            <span>${type === 'success' ? '✅' : '❌'} ${escapeHtml(message)}</span>
        `;

        // Вставляем перед блоком поиска
        const contentCard = document.querySelector('.admin-content-card');
        if (contentCard) {
            const existingMessage = contentCard.querySelector('.admin-message');
            if (existingMessage) {
                existingMessage.remove();
            }
            contentCard.insertBefore(messageDiv, contentCard.firstChild);

            // Автоматически скрываем через 5 секунд
            setTimeout(() => {
                if (messageDiv.parentNode) {
                    messageDiv.style.transition = 'opacity 0.5s';
                    messageDiv.style.opacity = '0';
                    setTimeout(() => {
                        if (messageDiv.parentNode) {
                            messageDiv.remove();
                        }
                    }, 500);
                }
            }, 5000);
        } else {
            // Если не нашли карточку, показываем alert
            alert(message);
        }
    }

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
        
        /* Анимация для сообщений */
        .admin-message {
            animation: fadeInDown 0.3s ease-out;
        }
        
        @keyframes fadeInDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    `;
    document.head.appendChild(style);
})();