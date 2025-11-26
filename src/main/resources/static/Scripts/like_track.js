document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('click', function(event) {
        if (event.target && event.target.classList.contains('heart-icon')) {
            event.preventDefault();

            const button = event.target;
            const container = button.closest('.like-container');
            const popup = container.querySelector('.like-required-popup');
            const trackId = container.dataset.trackId;
            const userId = container.querySelector('input[name="user_id"]').value;

            // Функция для закрытия всплывающего окна
            function closePopup() {
                if (popup) {
                    popup.classList.remove('show');
                    document.removeEventListener('click', closePopupHandler);
                }
            }

            // Обработчик события для закрытия всплывающего окна
            function closePopupHandler(event) {
                if (!container.contains(event.target) && !popup.contains(event.target)) {
                    closePopup();
                }
            }

            if (button.classList.contains('login-required')) {
                // Показываем всплывающее окно
                if (popup) {
                    popup.classList.add('show');
                    // Назначаем обработчик события для закрытия
                    document.addEventListener('click', closePopupHandler);
                }
                return;
            }

            // Отправляем AJAX-запрос (если вход не требуется)
            let requestBody = 'track_id=' + encodeURIComponent(trackId);
            if (userId) {
                requestBody += '&user_id=' + encodeURIComponent(userId);
            }

            fetch('/static/Scripts/like_track.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: requestBody
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.status === 'liked') {
                    button.classList.add('liked');
                } else if (data.status === 'unliked') {
                    button.classList.remove('liked');
                } else {
                    console.error("Некорректный статус:", data.status);
                }

                // Обновляем список треков в my-music-container
                const myMusicContainer = document.querySelector('.my-music-container');
                if (myMusicContainer) {
                    const trackElement = document.getElementById('track-' + trackId);
                    if (trackElement) {
                        trackElement.remove();
                    }
                    const remainingTracks = myMusicContainer.querySelectorAll('.container-sound');
                    if (remainingTracks.length === 0) {
                        const noTracksMessage = myMusicContainer.querySelector('.not-logged-in-message.no-tracks-message');
                        if (noTracksMessage) {
                            noTracksMessage.style.display = 'block';
                        }
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        }
    });
});