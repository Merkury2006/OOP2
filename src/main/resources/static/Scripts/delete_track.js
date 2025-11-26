document.addEventListener('DOMContentLoaded', function() {
    const deleteForms = document.querySelectorAll('.delete-form');

    deleteForms.forEach(form => {
        const button = form.querySelector('.trash-icon');
        
        button.addEventListener('click', function(event) {
            event.preventDefault();
            
            const userId = form.querySelector('input[name="user_id"]').value;
            const trackId = form.dataset.trackId;
            
            const originalBg = button.style.backgroundImage;
            button.style.backgroundImage = "url('data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\"%3E%3Cpath fill=\"%23e53935\" d=\"M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 18a8 8 0 1 1 8-8 8 8 0 0 1-8 8z\" opacity=\".5\"/%3E%3Cpath fill=\"%23e53935\" d=\"M12 6a1 1 0 0 0-1 1v6a1 1 0 0 0 2 0V7a1 1 0 0 0-1-1zm0 10a1 1 0 1 0 1 1 1 1 0 0 0-1-1z\"/%3E%3C/svg%3E')";
            
            fetch('/static/Scripts/delete_track.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `user_id=${encodeURIComponent(userId)}&track_id=${encodeURIComponent(trackId)}`
            })
            .then(response => {
                if (!response.ok) throw new Error('Ошибка сети');
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    const trackItem = form.closest('.track-item');
                    if (trackItem) {
                        trackItem.style.transition = 'opacity 0.3s';
                        trackItem.style.opacity = '0';
                        setTimeout(() => trackItem.remove(), 300);
                    }
                } else {
                    throw new Error(data.message || 'Ошибка удаления');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                button.style.backgroundImage = originalBg;
                alert(error.message);
            });
        });
    });
});