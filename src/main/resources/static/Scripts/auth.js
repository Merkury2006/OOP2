// ============================================
// ОБЩИЕ ФУНКЦИИ ДЛЯ АУТЕНТИФИКАЦИИ
// ============================================

// Инициализация всех кнопок переключения пароля
function initAllPasswordToggles() {
    const toggleButtons = document.querySelectorAll('.toggle-password');

    toggleButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            let passwordField;

            if (targetId) {
                // Если есть data-target, ищем по id
                passwordField = document.getElementById(targetId);
            } else {
                // Иначе ищем в том же wrapper
                const passwordWrapper = this.closest('.password-wrapper');
                passwordField = passwordWrapper.querySelector('input[type="password"], input[type="text"]');
            }

            if (passwordField) {
                const isPassword = passwordField.type === 'password';
                passwordField.type = isPassword ? 'text' : 'password';
                this.textContent = isPassword ? '🙈' : '👁️';
            }
        });

        button.addEventListener('mousedown', function(e) {
            e.preventDefault();
        });
    });
}

// Автофокус на поле email если оно пустое
function initAutoFocus() {
    const emailField = document.querySelector('input[name="username"], input[name="email"]');
    if (emailField && !emailField.value) {
        emailField.focus();
    }
}

// Основная функция инициализации
function initAuthPage() {
    initAllPasswordToggles();
    initAutoFocus();
}

// Запуск при загрузке документа
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAuthPage);
} else {
    initAuthPage();
}