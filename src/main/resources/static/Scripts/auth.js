// Общие функции для аутентификации

// Показать/скрыть пароль
document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('togglePasswordBtn');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', function() {
            const passwordField = document.getElementById('password');
            if (!passwordField) return;

            const isPassword = passwordField.type === 'password';
            passwordField.type = isPassword ? 'text' : 'password';
            this.textContent = isPassword ? '🙈' : '👁️';
        });

        toggleBtn.addEventListener('mousedown', function(e) {
            e.preventDefault();
        });
    }

    // Автофокус на поле email если оно пустое
    const emailField = document.querySelector('input[name="username"], input[name="email"]');
    if (emailField && !emailField.value) {
        emailField.focus();
    }

    // Валидация email для регистрации
    const registerEmailInput = document.getElementById('emailInput');
    if (registerEmailInput) {
        registerEmailInput.addEventListener('blur', validateEmail);

        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', function(e) {
                if (registerEmailInput && !validateEmail()) {
                    e.preventDefault();
                    registerEmailInput.focus();
                }
            });
        }
    }

    // Функция для открытия почтового клиента
    const mailButton = document.getElementById('mail-button');
    if (mailButton) {
        const email = mailButton.dataset.email || '';
        if (email) {
            const url = getMailServiceUrl(email);
            const serviceName = getMailServiceName(email);

            mailButton.href = url;
            mailButton.innerHTML = `📨 Открыть ${serviceName}`;
        }
    }
});

// Валидация email
function validateEmail() {
    const input = document.getElementById('emailInput');
    const errorElement = document.getElementById('emailError');

    if (!input || !errorElement) return true;

    const email = input.value.trim();

    if (!email) {
        errorElement.textContent = 'Email обязателен';
        input.classList.add('field-error');
        return false;
    }

    // Проверка временных email
    const suspiciousDomains = [
        'tempmail.com', '10minutemail.com', 'mailinator.com',
        'guerrillamail.com', 'yopmail.com', 'trashmail.com',
        'temp-mail.org', 'fakeinbox.com', 'throwawaymail.com',
        'tempmail.net', 'disposablemail.com'
    ];

    const domain = email.split('@')[1]?.toLowerCase();

    if (domain && suspiciousDomains.includes(domain)) {
        errorElement.textContent = 'Использование временных email запрещено';
        input.classList.add('field-error');
        return false;
    }

    // Проверка формата
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(email)) {
        errorElement.textContent = 'Введите корректный email';
        input.classList.add('field-error');
        return false;
    }

    errorElement.textContent = '';
    input.classList.remove('field-error');
    return true;
}

// Получение URL почтового сервиса
function getMailServiceUrl(email) {
    if (!email) return "https://mail.google.com";

    const domain = email.split('@')[1].toLowerCase();

    const services = {
        'gmail.com': 'https://mail.google.com',
        'yandex.ru': 'https://mail.yandex.ru',
        'ya.ru': 'https://mail.yandex.ru',
        'yandex.com': 'https://mail.yandex.ru',
        'yandex.ua': 'https://mail.yandex.ru',
        'mail.ru': 'https://mail.ru',
        'inbox.ru': 'https://mail.ru',
        'list.ru': 'https://mail.ru',
        'bk.ru': 'https://mail.ru',
        'outlook.com': 'https://outlook.live.com',
        'hotmail.com': 'https://outlook.live.com',
        'live.com': 'https://outlook.live.com',
        'yahoo.com': 'https://mail.yahoo.com'
    };

    return services[domain] || "https://mail.google.com";
}

// Получение имени почтового сервиса
function getMailServiceName(email) {
    if (!email) return "почту";

    const domain = email.split('@')[1].toLowerCase();

    if (domain.includes('gmail')) return "Gmail";
    if (domain.includes('yandex') || domain.includes('ya.ru')) return "Яндекс.Почту";
    if (domain.includes('mail.ru') || domain.includes('inbox.ru') ||
        domain.includes('list.ru') || domain.includes('bk.ru')) return "Mail.ru";
    if (domain.includes('outlook') || domain.includes('hotmail') || domain.includes('live.com')) return "Outlook";
    if (domain.includes('yahoo')) return "Yahoo";

    return "почту";
}