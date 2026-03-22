window.showDownloadPopup = function(trackId) {
    return PopupUtils.showPopup(`download-popup-${trackId}`, '/login');
};

async function downloadTrack(trackId, button) {
    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
        const response = await fetch(`/api/download/${trackId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                ...(csrfToken && csrfHeader && { [csrfHeader]: csrfToken })
            },
            credentials: 'include'
        });

        if (response.status === 401 || response.status === 403) {
            showDownloadPopup(trackId);
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error ${response.status}: ${errorText}`);
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `track_${trackId}.mp3`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        if (button) {
            const arrow = button.querySelector('.arrow-down');
            const line = button.querySelector('.line');

            arrow.classList.add('loaded');
            line.classList.add('loaded');

            setTimeout(() => {
                arrow.classList.remove('loaded');
                line.classList.remove('loaded');
            }, 1000);
        }

    } catch (error) {
        console.error('Download error:', error);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('click', function(event) {
        const downloadButton = event.target.closest('.button-load');
        if (!downloadButton) return;

        event.preventDefault();

        if (downloadButton.classList.contains('login-required')) {
            const container = downloadButton.closest('.download-container');
            const trackId = container?.dataset.trackId;
            if (trackId) showDownloadPopup(trackId);
            return;
        }

        const container = downloadButton.closest('.download-container');
        const trackId = container?.dataset.trackId;

        if (!trackId) {
            console.error('Track ID not found');
            return;
        }

        downloadTrack(trackId, downloadButton);
    });
});