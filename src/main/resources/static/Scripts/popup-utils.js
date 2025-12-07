// popupUtils.js - исправленная версия
class PopupUtils {

    /**
     * Показывает уведомление
     * @param {string} message - Текст сообщения
     * @param {string} type - Тип уведомления: 'success', 'error', 'warning', 'info'
     * @param {number} duration - Длительность показа в миллисекундах (по умолчанию 4000)
     */
    static showNotification(message, type = 'success', duration = 4000) {
        let notificationContainer = document.getElementById('notification-container');

        if (!notificationContainer) {
            notificationContainer = document.createElement('div');
            notificationContainer.id = 'notification-container';
            document.body.appendChild(notificationContainer);
        }

        notificationContainer.innerHTML = '';

        const notificationDiv = document.createElement('div');
        notificationDiv.classList.add('notification');
        notificationDiv.classList.add(type);

        const icon = document.createElement('span');
        icon.classList.add('notification-icon');

        // Разные иконки для разных типов
        const icons = {
            'success': '✓',
            'error': '✕',
            'warning': '⚠',
            'info': 'ℹ'
        };
        icon.innerHTML = icons[type] || icons['success'];
        notificationDiv.appendChild(icon);

        const text = document.createElement('span');
        text.classList.add('notification-text');
        text.textContent = message;
        notificationDiv.appendChild(text);

        notificationContainer.appendChild(notificationDiv);

        // Анимация появления
        setTimeout(() => {
            notificationDiv.classList.add('show');
        }, 10);

        // Автоматическое скрытие
        setTimeout(() => {
            notificationDiv.classList.remove('show');
            setTimeout(() => {
                notificationDiv.remove();
                if (notificationContainer.children.length === 0) {
                    notificationContainer.remove();
                }
            }, 400);
        }, duration);
    }

    /**
     * Показывает модальное окно с кнопкой закрытия
     * @param {string} title - Заголовок окна
     * @param {string} content - HTML содержимое
     * @param {Object} options - Дополнительные опции
     */
    static showModal(title, content, options = {}) {
        const modalId = 'modal-' + Date.now();
        const modalHtml = `
            <div class="modal-overlay" id="${modalId}">
                <div class="modal">
                    <div class="modal-header">
                        <h3>${title}</h3>
                        <button class="modal-close" onclick="PopupUtils.closeModal('${modalId}')">&times;</button>
                    </div>
                    <div class="modal-content">${content}</div>
                    ${options.showFooter !== false ? `
                    <div class="modal-footer">
                        <button class="modal-button" onclick="PopupUtils.closeModal('${modalId}')">
                            ${options.buttonText || 'Закрыть'}
                        </button>
                    </div>` : ''}
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);

        // Закрытие по клику на оверлей
        const modal = document.getElementById(modalId);
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                PopupUtils.closeModal(modalId);
            }
        });

        // Закрытие по Escape
        const escapeHandler = function(e) {
            if (e.key === 'Escape') {
                PopupUtils.closeModal(modalId);
                document.removeEventListener('keydown', escapeHandler);
            }
        };
        document.addEventListener('keydown', escapeHandler);

        modal.dataset.escapeHandler = escapeHandler;
    }

    /**
     * Закрывает модальное окно
     * @param {string} modalId - ID модального окна
     */
    static closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.opacity = '0';
            setTimeout(() => {
                modal.remove();

                // Удаляем обработчик Escape если есть
                if (modal.dataset.escapeHandler) {
                    document.removeEventListener('keydown', modal.dataset.escapeHandler);
                }
            }, 300);
        }
    }

    /**
     * Показывает подтверждение (confirm)
     * @param {string} message - Сообщение
     * @param {function} onConfirm - Функция при подтверждении
     * @param {function} onCancel - Функция при отмене
     */
    static showConfirm(message, onConfirm, onCancel) {
        const confirmHtml = `
            <div class="confirm-dialog">
                <p>${message}</p>
                <div class="confirm-buttons">
                    <button class="confirm-button confirm-yes">Да</button>
                    <button class="confirm-button confirm-no">Нет</button>
                </div>
            </div>
        `;

        PopupUtils.showModal('Подтверждение', confirmHtml, { showFooter: false });

        // Добавляем обработчики кнопок
        setTimeout(() => {
            const modal = document.querySelector('.modal-overlay:last-child');
            const yesBtn = modal.querySelector('.confirm-yes');
            const noBtn = modal.querySelector('.confirm-no');

            yesBtn.addEventListener('click', function() {
                PopupUtils.closeModal(modal.id);
                if (onConfirm) onConfirm();
            });

            noBtn.addEventListener('click', function() {
                PopupUtils.closeModal(modal.id);
                if (onCancel) onCancel();
            });
        }, 10);
    }

    // Хранилище для обработчиков событий
    static _popupHandlers = new WeakMap();

    /**
     * Показывает попап для лайков/скачивания
     * @param {string} popupId - ID попапа
     * @param {string} redirectUrl - URL для редиректа
     */
    static showPopup(popupId, redirectUrl = '/login') {
        if (window.event) {
            window.event.preventDefault();
            window.event.stopPropagation();
        }

        this.closeAllPopups();

        const popup = document.getElementById(popupId);
        if (popup) {
            popup.style.display = 'block';
            popup.classList.add('show');

            // Создаем обработчик клика вне попапа
            const closeOnOutsideClick = (event) => {
                if (!popup.contains(event.target)) {
                    this.closePopup(popup);
                }
            };

            // Создаем обработчик Escape
            const escapeHandler = (event) => {
                if (event.key === 'Escape') {
                    this.closePopup(popup);
                }
            };

            // Сохраняем обработчики
            this._popupHandlers.set(popup, {
                closeOnOutsideClick,
                escapeHandler
            });

            // Добавляем обработчики с небольшой задержкой
            setTimeout(() => {
                document.addEventListener('click', closeOnOutsideClick);
                document.addEventListener('keydown', escapeHandler);
            }, 10);

        } else {
            // Если попап не найден, перенаправляем на страницу входа
            window.location.href = `${redirectUrl}?redirect=${encodeURIComponent(window.location.pathname)}`;
        }

        return false;
    }

    /**
     * Закрывает попап
     * @param {HTMLElement} popup - Элемент попапа
     */
    static closePopup(popup) {
        if (!popup) return;

        popup.style.display = 'none';
        popup.classList.remove('show');

        // Получаем и удаляем обработчики
        const handlers = this._popupHandlers.get(popup);
        if (handlers) {
            document.removeEventListener('click', handlers.closeOnOutsideClick);
            document.removeEventListener('keydown', handlers.escapeHandler);
            this._popupHandlers.delete(popup);
        }
    }

    /**
     * Закрывает все попапы
     */
    static closeAllPopups() {
        const openPopups = document.querySelectorAll('.login-popup.show, .like-popup.show, .download-popup.show');
        openPopups.forEach(popup => {
            this.closePopup(popup);
        });
    }
}

// Экспортируем для глобального использования
window.PopupUtils = PopupUtils;