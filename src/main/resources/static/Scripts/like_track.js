window.showLoginPopup = function(trackId) {
    return PopupUtils.showPopup(`like-popup-${trackId}`, '/login');
};

async function likeTrack(trackId, button, container) {
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const response = await fetch(`/api/like/${trackId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                ...(csrfToken && csrfHeader && { [csrfHeader]: csrfToken })
            }
        });

        if (response.status === 401 || response.status === 403) {
            showLoginPopup(trackId);
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error ${response.status}: ${errorText}`);
        }

        const apiResponse = await response.json();

        if (apiResponse.success) {
            if (!button) return;

            if (apiResponse.data.liked) {
                button.classList.add('liked');
                const icon = button.querySelector('i');
                if (icon) icon.className = 'fas fa-heart';
            } else {
                button.classList.remove('liked');
                const icon = button.querySelector('i');
                if (icon) icon.className = 'far fa-heart';
            }
        } else {
            console.error('Server returned success: false', data);
        }

    } catch (error) {
        console.error('Like error:', error);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('click', function(event) {
        const heartIcon = event.target.closest('.heart-icon');
        if (!heartIcon) return;

        event.preventDefault();

        if (heartIcon.classList.contains('login-required')) {
            const container = heartIcon.closest('.like-container');
            const trackId = container?.dataset.trackId;
            if (trackId) showLoginPopup(trackId);
            return;
        }

        const container = heartIcon.closest('.like-container');
        const trackId = container?.dataset.trackId;

        if (!trackId) {
            console.error('Track ID not found');
            return;
        }

        likeTrack(trackId, heartIcon, container);
    });
});