// /js/admin-users.js

document.addEventListener('DOMContentLoaded', function() {
    const admin = window.AdminCommon;
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
        loadUsers('');

        if (searchInput) {
            searchInput.addEventListener('input', handleSearchInput);
            searchInput.addEventListener('keypress', handleKeyPress);
        }

        if (searchBtn) {
            searchBtn.addEventListener('click', () => performSearch(searchInput.value.trim()));
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
        updateUrl(searchTerm);
    }

    function clearSearch() {
        if (searchInput) searchInput.value = '';
        performSearch('');
    }

    async function loadUsers(searchTerm) {
        if (isLoading) return;
        isLoading = true;
        showLoading();

        try {
            const params = new URLSearchParams();
            if (searchTerm) params.append('search', searchTerm);

            const response = await fetch(`/admin/users/search?${params.toString()}`, {
                method: 'GET',
                headers: { 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const result = await response.json();
            const handled = admin.handleApiResponse(result);

            if (handled.success) {
                renderUsers(handled.data);
                if (totalUsersElement && handled.data.totalUsers !== undefined) {
                    totalUsersElement.textContent = handled.data.totalUsers;
                }
            } else {
                showError(handled.message);
            }

        } catch (error) {
            console.error('Ошибка загрузки пользователей:', error);
            showError('Ошибка загрузки данных: ' + error.message);
        } finally {
            isLoading = false;
        }
    }

    function renderUsers(searchData) {
        if (!usersTableContent) return;

        if (!searchData || !searchData.users || searchData.users.length === 0) {
            const message = currentSearch ? 'Пользователи не найдены' : 'В системе пока нет пользователей';
            usersTableContent.innerHTML = `
                <div class="empty-data-state">
                    <div class="empty-data-icon">👥</div>
                    <div class="empty-data-message">${message}</div>
                    <div class="empty-data-submessage">
                        ${currentSearch ? 'Попробуйте изменить параметры поиска' : 'Зарегистрируйте первого пользователя'}
                    </div>
                    ${currentSearch ? '<button onclick="clearSearch()" class="btn secondary" style="margin-top: 20px;">Очистить поиск</button>' : ''}
                </div>
            `;
            return;
        }

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

        searchData.users.forEach(user => {
            const isCurrentUser = user.id === searchData.currentUserId;
            const roleStr = typeof user.role === 'object' ? user.role.name : String(user.role);
            const roleClass = roleStr === 'ADMIN' ? 'admin-badge' : 'user-badge';
            const statusIcon = user.emailVerified ? '✅' : '❌';
            const statusClass = user.emailVerified ? 'verified' : 'not-verified';

            html += `
                <tr ${isCurrentUser ? 'class="current-user"' : ''}>
                    <td>${user.id}</td>
                    <td>${admin.escapeHtml(user.username || '')}</td>
                    <td>${admin.escapeHtml(user.email || '')}</td>
                    <td><span class="role-badge ${roleClass}">${roleStr || 'USER'}</span></td>
                    <td><span class="status ${statusClass}">${statusIcon}</span></td>
                    <td>
                        <div class="action-buttons">
            `;

            if (!isCurrentUser) {
                const newRole = roleStr === 'ADMIN' ? 'USER' : 'ADMIN';
                const buttonText = roleStr === 'ADMIN' ? '↓ USER' : '↑ ADMIN';
                const buttonTitle = roleStr === 'ADMIN' ? 'Понизить до обычного пользователя' : 'Повысить до администратора';

                html += `
                    <button type="button" class="btn-small warning" 
                            onclick="AdminUsers.changeRole(${user.id}, '${admin.escapeHtml(user.username || '')}', '${newRole}')"
                            title="${buttonTitle}">${buttonText}</button>
                    
                    <button type="button" class="btn-small danger" 
                            onclick="AdminUsers.delete(${user.id}, '${admin.escapeHtml(user.username || '')}')"
                            title="Удалить пользователя">🗑️</button>
                `;
            } else {
                html += `<span class="text-muted">Это вы</span>`;
            }

            html += `</div></td></tr>`;
        });

        html += `</tbody></table></div>`;
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
                    <div class="error-data-message">${admin.escapeHtml(message)}</div>
                    <button onclick="location.reload()" class="btn secondary">Перезагрузить страницу</button>
                </div>
            `;
        }
    }

    function updateUrl(searchTerm) {
        const url = new URL(window.location);
        searchTerm ? url.searchParams.set('search', searchTerm) : url.searchParams.delete('search');
        window.history.pushState({ search: searchTerm }, '', url);
    }

    window.addEventListener('popstate', function(event) {
        if (event.state && event.state.search !== undefined && searchInput) {
            searchInput.value = event.state.search;
            performSearch(event.state.search);
        }
    });
});

// Отдельный модуль для операций с пользователями
const AdminUsers = {
    async changeRole(userId, username, newRole) {
        const action = newRole === 'ADMIN' ? 'повысить до ADMIN' : 'понизить до USER';
        const confirmed = await AdminCommon.showConfirm(`Вы уверены, что хотите ${action} пользователя "${username}"?`);
        if (!confirmed) return;

        try {
            const params = new URLSearchParams();
            params.append('newRole', newRole);

            const response = await AdminCommon.apiRequest(`/admin/users/${userId}/role`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            });

            const result = await response.json();
            const handled = AdminCommon.handleApiResponse(result);

            if (handled.success) {
                AdminCommon.showSuccess(`Роль пользователя ${username} успешно изменена на ${newRole}`);
                // Обновление таблицы через событие
                document.dispatchEvent(new CustomEvent('admin-refresh-table'));
            } else if (handled.status === 403) {
                AdminCommon.showError(handled.message || 'Доступ запрещен. Нельзя изменить свою собственную роль.');
            } else {
                AdminCommon.showError(handled.message || 'Ошибка при изменении роли');
                if (handled.shouldRefresh) {
                    document.dispatchEvent(new CustomEvent('admin-refresh-table'));
                }
            }

        } catch (error) {
            console.error('Ошибка при изменении роли:', error);
            AdminCommon.showError('Ошибка при изменении роли: ' + error.message);
        }
    },

    async delete(userId, username) {
        const confirmed = await AdminCommon.showConfirm(`Вы уверены, что хотите удалить пользователя "${username}"? Это действие необратимо!`);
        if (!confirmed) return;

        try {
            const response = await AdminCommon.apiRequest(`/admin/users/${userId}`, {
                method: 'DELETE'
            });

            const result = await response.json();
            const handled = AdminCommon.handleApiResponse(result);

            if (handled.success) {
                AdminCommon.showSuccess(`Пользователь ${username} успешно удален`);
                document.dispatchEvent(new CustomEvent('admin-refresh-table'));
            } else if (handled.status === 403) {
                let errorMessage = handled.message || 'Доступ запрещен.';
                if (handled.message && handled.message.includes('самого себя')) {
                    errorMessage = 'Вы не можете удалить свой собственный аккаунт.';
                } else if (handled.message && handled.message.includes('другого админа')) {
                    errorMessage = 'Вы не можете удалить другого администратора.';
                }
                AdminCommon.showError(errorMessage);
            } else {
                AdminCommon.showError(handled.message || `Ошибка при удалении пользователя`);
                if (handled.shouldRefresh) {
                    document.dispatchEvent(new CustomEvent('admin-refresh-table'));
                }
            }

        } catch (error) {
            console.error('Ошибка при удалении пользователя:', error);
            AdminCommon.showError('Ошибка при удалении пользователя: ' + error.message);
        }
    }
};

// Экспортируем
window.AdminUsers = AdminUsers;