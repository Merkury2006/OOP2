document.addEventListener('DOMContentLoaded', function() {
    const downloadContainers = document.querySelectorAll('.download-container');

    function showPopup(element, popupId) {
        if (!popupId) {
            console.error("Invalid popupId:", popupId);
            return;
        }
        const popup = document.getElementById(popupId);
        if (!popup) {
            console.error("Popup not found with ID:", popupId);
            return;
        }
        popup.classList.add('show');

        document.addEventListener('click', function(event) {
            if (!element.contains(event.target) && !popup.contains(event.target)) {
                popup.classList.remove('show'); 
                document.removeEventListener('click', arguments.callee);
            }
        });
    }

    downloadContainers.forEach(container => {
        const button = container.querySelector('.button-load');
        button.addEventListener('click', function(event) {
            event.preventDefault();

            const trackId = container.dataset.trackId;
            const messageId = 'download-required-message-' + trackId;

            if (button.classList.contains('login-required')) {
                showPopup(container, messageId);
                return;
            }

            fetch('/static/Scripts/download_track.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'track_id=' + encodeURIComponent(trackId)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text || 'Network error');
                    });
                }
                return response.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'track_' + trackId + '.mp3';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                
                const arrow = button.querySelector('.arrow-down');
                const line = button.querySelector('.line');
                
                arrow.classList.add('loaded');
                line.classList.add('loaded');
                
                setTimeout(() => {
                    arrow.classList.remove('loaded');
                    line.classList.remove('loaded');
                }, 1000);
            })
            .catch(error => {
                console.error('Error:', error);
                if (error.message.includes("login_required")) {
                    showPopup(container, messageId);
                } else {
                    alert('Ошибка при скачивании: ' + error.message);
                }
            });
        });
    });
});
