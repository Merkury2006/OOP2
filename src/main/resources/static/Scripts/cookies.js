document.addEventListener('DOMContentLoaded', function() {
    const enableCookiesButton = document.getElementById('enable-cookies');
    const disableCookiesButton = document.getElementById('disable-cookies');
    const lightThemeButton = document.getElementById('light-theme-button');
    const darkThemeButton = document.getElementById('dark-theme-button');
    const themeContainer = document.body;

    function setCookie(name, value, days) {
        let expires = "";
        if (days) {
            const date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    }

    function getCookie(name) {
        const nameEQ = name + "=";
        const ca = document.cookie.split(';');
        for(let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    function eraseCookie(name) {   
        document.cookie = name +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }

    function activateThemeButton(theme) {
        lightThemeButton.classList.remove('active');
        darkThemeButton.classList.remove('active');
        if (theme === 'light-theme') {
            lightThemeButton.classList.add('active');
        } else if (theme === 'dark-theme') {
            darkThemeButton.classList.add('active');
        }
    }

    function applyTheme(theme) {
        themeContainer.className = theme;
        activateThemeButton(theme);
    }

    lightThemeButton.addEventListener('click', function() {
        if (getCookie('cookiesEnabled') === 'true') {
            applyTheme('light-theme');
            setCookie('selectedTheme', 'light-theme', 365);
        } else {
            applyTheme('light-theme');
            activateThemeButton('light-theme');
            alert('Включите Cookies, чтобы выбирать тему.');
        }
    });

    darkThemeButton.addEventListener('click', function() {
        if (getCookie('cookiesEnabled') === 'true') {
            applyTheme('dark-theme');
            setCookie('selectedTheme', 'dark-theme', 365);
        } else {
            applyTheme('light-theme');
            activateThemeButton('light-theme');
            alert('Включите Cookies, чтобы выбирать тему.');
        }
    });

    enableCookiesButton.addEventListener('click', function() {
        setCookie('cookiesEnabled', 'true', 365);

        let currentTheme = themeContainer.className || 'light-theme';
        setCookie('selectedTheme', currentTheme, 365);
    });

    disableCookiesButton.addEventListener('click', function() {
        eraseCookie('cookiesEnabled');
        eraseCookie('selectedTheme');
        applyTheme('light-theme');
        activateThemeButton('light-theme');
    });

    let cookiesEnabled = getCookie('cookiesEnabled');

    if (cookiesEnabled === 'true') {

        let selectedTheme = getCookie('selectedTheme');
        if (selectedTheme) {
            applyTheme(selectedTheme);
        } else {
            applyTheme('light-theme');
            activateThemeButton('light-theme');
        }
    } else {
        applyTheme('light-theme');
        activateThemeButton('light-theme');
    }

});