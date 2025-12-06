function closePopup(popup) {
    if (!popup) return;

    popup.style.display = 'none';
    popup.classList.remove('show');

    if (popup.dataset.closeHandler) {
        try {
            const closeHandler = eval(popup.dataset.closeHandler);
            document.removeEventListener('click', closeHandler);
        } catch (e) {
            // Игнорируем ошибки при удалении обработчика
        }
        delete popup.dataset.closeHandler;
    }

    if (popup.dataset.escapeHandler) {
        try {
            const escapeHandler = eval(popup.dataset.escapeHandler);
            document.removeEventListener('keydown', escapeHandler);
        } catch (e) {
            // Игнорируем ошибки при удалении обработчика
        }
        delete popup.dataset.escapeHandler;
    }
}

function closeAllPopups() {
    const openPopups = document.querySelectorAll('.login-popup.show, .like-popup.show, .download-popup.show');

    openPopups.forEach(popup => {
        closePopup(popup);
    });

    document.querySelectorAll('.login-popup, .like-popup, .download-popup').forEach(popup => {
        popup.style.display = 'none';
        popup.classList.remove('show');
    });
}

function showPopup(popupId, redirectUrl = '/login') {
    if (window.event) {
        window.event.preventDefault();
        window.event.stopPropagation();
    }
    closeAllPopups();

    const popup = document.getElementById(popupId);
    if (popup) {
        popup.style.display = 'block';
        popup.classList.add('show');

        const closeHandler = function(event) {
            if (!popup.contains(event.target)) {
                closePopup(popup);
                document.removeEventListener('click', closeHandler);
                document.removeEventListener('keydown', escapeHandler);
            }
        };

        setTimeout(() => {
            document.addEventListener('click', closeHandler);
        }, 10);

        const escapeHandler = function(event) {
            if (event.key === 'Escape') {
                closePopup(popup);
                document.removeEventListener('keydown', escapeHandler);
                document.removeEventListener('click', closeHandler);
            }
        };

        document.addEventListener('keydown', escapeHandler);

        popup.dataset.closeHandler = closeHandler;
        popup.dataset.escapeHandler = escapeHandler;
    } else {
        window.location.href = `${redirectUrl}?redirect=${encodeURIComponent(window.location.pathname)}`;
    }

    return false;
}

window.PopupUtils = {
    showPopup,
    closePopup,
    closeAllPopups
};