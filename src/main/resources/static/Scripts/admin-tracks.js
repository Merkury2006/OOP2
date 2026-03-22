// /js/admin-tracks.js

document.addEventListener('DOMContentLoaded', function() {
    const admin = window.AdminCommon;
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const clearBtn = document.getElementById('clearBtn');
    const tracksTableContent = document.getElementById('tracksTableContent');
    const totalTracksElement = document.getElementById('totalTracks');

    let currentSearch = '';
    let isLoading = false;
    let searchTimeout;

    // Инициализация
    if (searchInput && tracksTableContent) {
        initSearch();
    }

    function initSearch() {
        // Загружаем треки при загрузке страницы
        loadTracks('');

        // Обработчики событий
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

        document.addEventListener('admin-refresh-table', () => loadTracks(currentSearch));
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
        loadTracks(searchTerm);

        // Обновляем URL без перезагрузки страницы
        updateUrl(searchTerm);
    }

    function clearSearch() {
        if (searchInput) {
            searchInput.value = '';
        }
        performSearch('');
    }

    async function loadTracks(searchTerm) {
        if (isLoading) return;

        isLoading = true;
        showLoading();

        try {
            const params = new URLSearchParams();
            if (searchTerm) {
                params.append('search', searchTerm);
            }

            const response = await fetch(`/admin/tracks/search?${params.toString()}`, {
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
            const handled = admin.handleApiResponse(result);

            if (handled.success) {
                renderTracks(handled.data);

                // Обновляем счетчик
                if (totalTracksElement && handled.data.totalTracks !== undefined) {
                    totalTracksElement.textContent = handled.data.totalTracks;
                }
            } else {
                showError(handled.message);
            }

        } catch (error) {
            console.error('Ошибка загрузки треков:', error);
            showError('Ошибка загрузки данных: ' + error.message);
        } finally {
            isLoading = false;
        }
    }

    function renderTracks(trackData) {
        if (!tracksTableContent) return;

        // Если нет треков
        if (!trackData || !trackData.tracks || trackData.tracks.length === 0) {
            const message = currentSearch ? 'Треки не найдены' : 'В системе пока нет треков';
            const submessage = currentSearch
                ? 'Попробуйте изменить параметры поиска'
                : 'Пользователи еще не загрузили треки';

            tracksTableContent.innerHTML = `
                <div class="empty-data-state">
                    <div class="empty-data-icon">🎵</div>
                    <div class="empty-data-message">${message}</div>
                    <div class="empty-data-submessage">${submessage}</div>
                    ${currentSearch ? '<button onclick="clearSearch()" class="btn secondary" style="margin-top: 20px;">Очистить поиск</button>' : ''}
                </div>
            `;
            return;
        }

        // Рендерим таблицу с треками
        let html = `
            <div class="tracks-table-container">
                <table class="tracks-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Название</th>
                            <th>Артист</th>
                            <th>Жанр</th>
                            <th>Лайки</th>
                            <th>Пользователь</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        // Проходим по всем трекам
        trackData.tracks.forEach(track => {
            html += `
            <tr id="track-${track.id}">
                <td>${track.id}</td>
                <td>
                    <div class="track-title" title="${admin.escapeHtml(track.title || '')}">
                        ${admin.escapeHtml(track.title || '')}
                    </div>
                </td>
                <td>
                    <div class="track-artist">
                        ${admin.escapeHtml(track.artist || 'Не указан')}
                    </div>
                </td>
                <!-- Добавляем жанр если есть -->
                ${track.genre ? `
                    <td>
                        <div class="track-genre">
                            ${admin.escapeHtml(track.genre)}
                        </div>
                    </td>
                ` : ''}
                
                <!-- Показываем лайки если есть -->
               <td>${track.likeCount || 0}</td>
                <td>
                    <div class="user-id" title="ID пользователя, который добавил трек">
                        ${track.userId || 'N/A'}
                    </div>
                </td>
               
                <td>
                    <div class="action-buttons">
                        <button type="button" class="btn-small danger" 
                                onclick="AdminTracks.delete(${track.id}, '${admin.escapeHtml(track.title || '')}')"
                                title="Удалить трек">
                            🗑️ Удалить
                        </button>
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

        tracksTableContent.innerHTML = html;
    }

    function showLoading() {
        if (tracksTableContent) {
            tracksTableContent.innerHTML = `
                <div class="loading-state">
                    <div class="spinner"></div>
                    <p>Загрузка треков...</p>
                </div>
            `;
        }
    }

    function showError(message) {
        if (tracksTableContent) {
            tracksTableContent.innerHTML = `
                <div class="error-data-state">
                    <div class="error-data-icon">❌</div>
                    <div class="error-data-message">${admin.escapeHtml(message)}</div>
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

    // Обработчик истории браузера
    window.addEventListener('popstate', function(event) {
        if (event.state && event.state.search !== undefined) {
            if (searchInput) {
                searchInput.value = event.state.search;
            }
            performSearch(event.state.search);
        }
    });

    // Экспортируем функцию очистки поиска
    window.clearSearch = clearSearch;
});

// Модуль для операций с треками
const AdminTracks = {
    async delete(trackId, trackTitle) {
        const confirmed = await AdminCommon.showConfirm(
            `Вы уверены, что хотите удалить трек "${trackTitle}"? Это действие необратимо!`
        );

        if (!confirmed) return;

        try {
            const response = await AdminCommon.apiRequest(`/admin/tracks/${trackId}`, {
                method: 'DELETE'
            });

            const result = await response.json();
            const handled = AdminCommon.handleApiResponse(result);

            if (handled.success) {
                AdminCommon.showSuccess(`Трек "${trackTitle}" успешно удален`);
                document.dispatchEvent(new CustomEvent('admin-refresh-table'));
            } else if (handled.status === 403) {
                AdminCommon.showError(handled.message || 'Доступ запрещен.');
            } else if (handled.status === 404) {
                AdminCommon.showError(handled.message || 'Трек не найден.');
                document.dispatchEvent(new CustomEvent('admin-refresh-table'));
            } else {
                AdminCommon.showError(handled.message || `Ошибка при удалении трека`);
                if (handled.shouldRefresh) {
                    document.dispatchEvent(new CustomEvent('admin-refresh-table'));
                }
            }

        } catch (error) {
            console.error('Ошибка при удалении трека:', error);
            AdminCommon.showError('Ошибка при удалении трека: ' + error.message);
        }
    }
};

// Экспортируем глобально
window.AdminTracks = AdminTracks;