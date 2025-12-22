// /js/admin-common.js
class AdminCommon {
    // Статические методы для CSRF
    static getCsrfToken() {
        return document.querySelector('meta[name="_csrf"]')?.content || '';
    }

    static getCsrfHeader() {
        return document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-Token';
    }

    static getCsrfHeaders(contentType = 'application/json') {
        const token = this.getCsrfToken();
        const headerName = this.getCsrfHeader();
        const headers = {
            'Accept': 'application/json',
            'Content-Type': contentType,
            ...(token && headerName && { [headerName]: token })
        };
        return headers;
    }

    // Статический метод для запросов
    static async apiRequest(url, options = {}) {
        const defaultOptions = {
            credentials: 'include',
            headers: this.getCsrfHeaders(options.headers?.['Content-Type'] || 'application/json')
        };

        if (options.headers) {
            defaultOptions.headers = { ...defaultOptions.headers, ...options.headers };
        }

        const response = await fetch(url, { ...defaultOptions, ...options });
        return response;
    }

    // Обработка ответов
    static handleApiResponse(result) {
        if (result.success) {
            return {
                success: true,
                data: result.data,
                message: result.message,
                status: result.status || 200
            };
        } else {
            return {
                success: false,
                message: result.message,
                status: result.status || 500,
                shouldRefresh: result.status === 404
            };
        }
    }

    // Уведомления
    static showSuccess(message) {
        if (window.PopupUtils && window.PopupUtils.showNotification) {
            PopupUtils.showNotification(message, 'success');
        } else {
            alert('✅ ' + message);
        }
    }

    static showError(message) {
        if (window.PopupUtils && window.PopupUtils.showNotification) {
            PopupUtils.showNotification(message, 'error');
        } else {
            alert('❌ ' + message);
        }
    }

    static showConfirm(message) {
        return new Promise((resolve) => {
            if (window.PopupUtils && window.PopupUtils.showConfirm) {
                PopupUtils.showConfirm(
                    message,
                    () => resolve(true),
                    () => resolve(false)
                );
            } else {
                resolve(confirm(message));
            }
        });
    }

    // Утилиты
    static escapeHtml(text) {
        if (text === null || text === undefined) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    static formatDuration(seconds) {
        if (!seconds) return '0:00';
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    }

    static formatDate(dateString) {
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('ru-RU', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (e) {
            return dateString || '';
        }
    }
}

// Экспортируем КЛАСС
window.AdminCommon = AdminCommon;