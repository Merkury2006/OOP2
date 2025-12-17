(function() {
    // Проверяем, не инициализирован ли скрипт уже
    if (window.deleteTrackInitialized) {
        console.warn('delete-track.js уже был инициализирован');
        return;
    }
    window.deleteTrackInitialized = true;

    let isProcessing = false;

    async function deleteTrack(trackId) {
        if (isProcessing) {
            console.log('Уже идет удаление...');
            return;
        }

        isProcessing = true;

    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        const response = await fetch(`/api/delete/${trackId}`, {
            method: 'DELETE',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
                ...(csrfToken && csrfHeader && { [csrfHeader]: csrfToken })
            }
        });

        const apiResponse = await response.json();

        if (!response.ok || !apiResponse.success) {
            throw new Error(apiResponse.message || 'Ошибка удаления');
        }

        // Показываем уведомление об успехе
        PopupUtils.showNotification('Трек успешно удален', 'success');

        // Находим и удаляем элемент трека
        const trackElement = document.getElementById(`track-${trackId}`);
        if (trackElement) {
            // Простая анимация исчезновения
            trackElement.style.opacity = '0';
            trackElement.style.transition = 'opacity 0.3s ease';

            setTimeout(() => {
                trackElement.remove();

                // Проверяем, остались ли треки
                const tracks = document.querySelectorAll('.container-sound');
                if (tracks.length === 0) {
                    const noTracksMessage = document.querySelector('.no-tracks-message');
                    if (noTracksMessage) {
                        noTracksMessage.style.display = 'block';
                    }
                }
            }, 300);
        } else {
            // Если элемент не найден, перезагружаем страницу
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        }

    } catch (error) {
        console.error('Delete error:', error);
        PopupUtils.showNotification('Ошибка при удалении: ' + error.message, 'error');
    }
    finally {
        isProcessing = false;
    }
}

// Инициализация
    document.addEventListener('click', function(event) {
        const trashIcon = event.target.closest('.trash-icon');
        if (!trashIcon) return;

        event.preventDefault();
        event.stopPropagation();

        const deleteContainer = trashIcon.closest('.delete-container');
        const trackId = deleteContainer?.dataset.trackId;

        if (trackId) {
            deleteTrack(trackId);
        }
    });

    console.log('delete-track.js инициализирован');
})();