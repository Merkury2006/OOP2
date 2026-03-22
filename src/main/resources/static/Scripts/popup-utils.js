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
            notificationContainer.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 9999;
            `;
            document.body.appendChild(notificationContainer);
        }

        notificationContainer.innerHTML = '';

        const notificationDiv = document.createElement('div');
        notificationDiv.classList.add('notification');
        notificationDiv.classList.add(type);
        notificationDiv.style.cssText = `
            background: ${type === 'success' ? '#4CAF50' :
            type === 'error' ? '#f44336' :
                type === 'warning' ? '#ff9800' : '#2196F3'};
            color: white;
            padding: 15px 20px;
            border-radius: 4px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.2);
            opacity: 0;
            transform: translateX(100%);
            transition: all 0.3s ease;
        `;

        const icon = document.createElement('span');
        icon.classList.add('notification-icon');
        icon.style.cssText = `
            font-weight: bold;
            font-size: 18px;
        `;

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
            notificationDiv.style.opacity = '1';
            notificationDiv.style.transform = 'translateX(0)';
        }, 10);

        // Автоматическое скрытие
        setTimeout(() => {
            notificationDiv.style.opacity = '0';
            notificationDiv.style.transform = 'translateX(100%)';
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

        // Создаем модальное окно
        const modalOverlay = document.createElement('div');
        modalOverlay.id = modalId;
        modalOverlay.className = 'modal-overlay';
        modalOverlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0,0,0,0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
            opacity: 0;
            transition: opacity 0.3s;
        `;

        const modal = document.createElement('div');
        modal.className = 'modal';
        modal.style.cssText = `
            background: white;
            border-radius: 8px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            opacity: 0;
            transform: translateY(-20px);
            transition: all 0.3s;
        `;

        modal.innerHTML = `
            <div class="modal-header" style="
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 20px 20px 10px;
                border-bottom: 1px solid #eee;
            ">
                <h3 style="margin: 0; font-size: 18px;">${title}</h3>
                <button class="modal-close" style="
                    background: none;
                    border: none;
                    font-size: 24px;
                    cursor: pointer;
                    line-height: 1;
                ">&times;</button>
            </div>
            <div class="modal-content" style="padding: 20px; max-height: 60vh; overflow-y: auto;">
                ${content}
            </div>
            ${options.showFooter !== false ? `
            <div class="modal-footer" style="
                padding: 10px 20px 20px;
                border-top: 1px solid #eee;
                text-align: right;
            ">
                <button class="modal-button" style="
                    background: #4CAF50;
                    color: white;
                    border: none;
                    padding: 8px 16px;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 14px;
                ">${options.buttonText || 'Закрыть'}</button>
            </div>` : ''}
        `;

        modalOverlay.appendChild(modal);
        document.body.appendChild(modalOverlay);

        // Анимация появления
        setTimeout(() => {
            modalOverlay.style.opacity = '1';
            modal.style.opacity = '1';
            modal.style.transform = 'translateY(0)';
        }, 10);

        // Обработчики событий
        const closeModal = () => this.closeModal(modalId);

        // Кнопка закрытия
        const closeBtn = modal.querySelector('.modal-close');
        closeBtn.addEventListener('click', closeModal);

        // Кнопка в футере
        const footerBtn = modal.querySelector('.modal-button');
        if (footerBtn) {
            footerBtn.addEventListener('click', closeModal);
        }

        // Закрытие по клику на оверлей
        modalOverlay.addEventListener('click', function(e) {
            if (e.target === this) {
                closeModal();
            }
        });

        // Закрытие по Escape
        const escapeHandler = (e) => {
            if (e.key === 'Escape') {
                closeModal();
            }
        };

        document.addEventListener('keydown', escapeHandler);
        modalOverlay._escapeHandler = escapeHandler;

        return modalId;
    }

    /**
     * Закрывает модальное окно
     * @param {string} modalId - ID модального окна
     */
    static closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            // Удаляем обработчик Escape
            const escapeHandler = modal._escapeHandler;
            if (escapeHandler) {
                document.removeEventListener('keydown', escapeHandler);
            }

            // Анимация закрытия
            modal.style.opacity = '0';
            const modalInner = modal.querySelector('.modal');
            if (modalInner) {
                modalInner.style.opacity = '0';
                modalInner.style.transform = 'translateY(-20px)';
            }

            setTimeout(() => {
                modal.remove();
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
            <div style="text-align: center;">
                <p style="margin-bottom: 20px; font-size: 16px;">${message}</p>
                <div style="display: flex; gap: 10px; justify-content: center;">
                    <button class="confirm-button confirm-yes" style="
                        background: #4CAF50;
                        color: white;
                        border: none;
                        padding: 8px 16px;
                        border-radius: 4px;
                        cursor: pointer;
                        font-size: 14px;
                    ">Да</button>
                    <button class="confirm-button confirm-no" style="
                        background: #f44336;
                        color: white;
                        border: none;
                        padding: 8px 16px;
                        border-radius: 4px;
                        cursor: pointer;
                        font-size: 14px;
                    ">Нет</button>
                </div>
            </div>
        `;

        const modalId = this.showModal('Подтверждение', confirmHtml, { showFooter: false });

        // Добавляем обработчики кнопок
        setTimeout(() => {
            const modal = document.getElementById(modalId);
            if (!modal) return;

            const yesBtn = modal.querySelector('.confirm-yes');
            const noBtn = modal.querySelector('.confirm-no');

            const closeAndCall = (callback) => {
                this.closeModal(modalId);
                if (callback) callback();
            };

            yesBtn.addEventListener('click', () => closeAndCall(onConfirm));
            noBtn.addEventListener('click', () => closeAndCall(onCancel));
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
            popup.style.opacity = '0';
            popup.style.transform = 'scale(0.9)';
            popup.classList.add('show');

            // Создаем оверлей
            let overlay = document.getElementById('popup-overlay');
            if (!overlay) {
                overlay = document.createElement('div');
                overlay.id = 'popup-overlay';
                overlay.style.cssText = `
                    position: fixed;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: rgba(0,0,0,0.5);
                    z-index: 9998;
                    opacity: 0;
                    transition: opacity 0.3s;
                `;
                document.body.appendChild(overlay);
            }

            overlay.style.display = 'block';
            popup.style.zIndex = '9999';

            // Анимация появления
            setTimeout(() => {
                overlay.style.opacity = '1';
                popup.style.opacity = '1';
                popup.style.transform = 'scale(1)';
            }, 10);

            // Создаем обработчик клика вне попапа
            const closeOnOutsideClick = (event) => {
                if (!popup.contains(event.target) && event.target !== popup) {
                    this.closePopup(popup);
                }
            };

            // Создаем обработчик Escape
            const escapeHandler = (event) => {
                if (event.key === 'Escape') {
                    this.closePopup(popup);
                }
            };

            // Создаем обработчик клика на оверлей
            const overlayClickHandler = (event) => {
                if (event.target === overlay) {
                    this.closePopup(popup);
                }
            };

            // Сохраняем обработчики
            this._popupHandlers.set(popup, {
                closeOnOutsideClick,
                escapeHandler,
                overlayClickHandler
            });

            // Добавляем обработчики
            document.addEventListener('click', closeOnOutsideClick);
            document.addEventListener('keydown', escapeHandler);
            overlay.addEventListener('click', overlayClickHandler);

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

        popup.style.opacity = '0';
        popup.style.transform = 'scale(0.9)';
        popup.classList.remove('show');

        // Получаем и удаляем обработчики
        const handlers = this._popupHandlers.get(popup);
        if (handlers) {
            document.removeEventListener('click', handlers.closeOnOutsideClick);
            document.removeEventListener('keydown', handlers.escapeHandler);

            const overlay = document.getElementById('popup-overlay');
            if (overlay && handlers.overlayClickHandler) {
                overlay.removeEventListener('click', handlers.overlayClickHandler);
                overlay.style.opacity = '0';
                setTimeout(() => {
                    overlay.style.display = 'none';
                }, 300);
            }

            this._popupHandlers.delete(popup);
        }

        setTimeout(() => {
            popup.style.display = 'none';
            popup.style.zIndex = '';
        }, 300);
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

    static showAdminNotification(title, message, type = 'success', duration = 5000) {
        const notification = document.createElement('div');
        notification.className = `admin-notification ${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${type === 'success' ? '#4CAF50' :
            type === 'error' ? '#f44336' :
                type === 'warning' ? '#ff9800' : '#2196F3'};
            color: white;
            padding: 15px 20px;
            border-radius: 4px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.2);
            display: flex;
            align-items: center;
            gap: 15px;
            min-width: 300px;
            max-width: 400px;
            opacity: 0;
            transform: translateX(100%);
            transition: all 0.3s ease;
            z-index: 10001;
        `;

        notification.innerHTML = `
            <div class="admin-notification-icon" style="
                font-size: 20px;
                font-weight: bold;
            ">
                ${type === 'success' ? '✓' : type === 'error' ? '✕' : type === 'warning' ? '⚠' : 'ℹ'}
            </div>
            <div class="admin-notification-content" style="flex: 1;">
                <div class="admin-notification-title" style="
                    font-weight: bold;
                    margin-bottom: 5px;
                    font-size: 14px;
                ">${title}</div>
                <div class="admin-notification-message" style="
                    font-size: 13px;
                    opacity: 0.9;
                ">${message}</div>
            </div>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.opacity = '1';
            notification.style.transform = 'translateX(0)';
        }, 10);

        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => notification.remove(), 300);
        }, duration);
    }
}

// Экспортируем для глобального использования
window.PopupUtils = PopupUtils;