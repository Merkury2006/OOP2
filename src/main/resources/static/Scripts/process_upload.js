function showNotification(message, type = 'success') {
    let notificationContainer = document.getElementById('notification-container');
    
    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'notification-container';
        document.body.appendChild(notificationContainer);
    }

    notificationContainer.innerHTML = '';

    const notificationDiv = document.createElement('div');
    notificationDiv.classList.add('notification');
    
    const icon = document.createElement('span');
    icon.classList.add('notification-icon');
    icon.innerHTML = type === 'success' ? '✓' : '✕';
    notificationDiv.appendChild(icon);
    
    const text = document.createElement('span');
    text.classList.add('notification-text');
    text.textContent = message;
    notificationDiv.appendChild(text);
    
    notificationContainer.appendChild(notificationDiv);

    setTimeout(() => {
        notificationDiv.classList.add('show');
    }, 10);

    setTimeout(() => {
        notificationDiv.classList.remove('show');
        setTimeout(() => {
            notificationDiv.remove();
            if (notificationContainer.children.length === 0) {
                notificationContainer.remove();
            }
        }, 400);
    }, 4000);
}

function addTrackToTrackList(track) {
    const mainGenreMusic = document.querySelector('.main-genre-music');

    const newTrackElement = document.createElement('div');
    newTrackElement.innerHTML = track.html;

    if (newTrackElement.firstChild) {
        newTrackElement.firstChild.classList.add('track-fade-in');
    }

    mainGenreMusic.insertBefore(newTrackElement.firstChild, mainGenreMusic.firstChild);

    const noTracksMessage = document.getElementById('noTracksMessage');
    if (noTracksMessage) {
        noTracksMessage.style.display = 'none';
    }

    const newTrack = mainGenreMusic.firstChild;
    initializeTrack(newTrack);
    initializeTrackEvents(track.id);
}

function initializeTrackEvents(trackId) {
    const trackElement = document.getElementById(`track-${trackId}`);
    if (trackElement) {
        const likeButton = trackElement.querySelector('.heart-icon');
        if (likeButton) {
            likeButton.addEventListener('click', function(event) {
                event.preventDefault();
                likeTrack(trackId, this);
            });
        }

        const downloadButton = trackElement.querySelector('.button-load');
        if (downloadButton) {
            downloadButton.addEventListener('click', function(event) {
                event.preventDefault();
                downloadTrack(trackId, this);
            });
        }

        const deleteButton = trackElement.querySelector('.trash-icon');
        if (deleteButton) {
            deleteButton.addEventListener('click', function() {
                deleteTrack(trackId, trackElement);
            });
        }
        
    } else {
        console.warn(`Track element with ID ${trackId} not found!`);
    }
}
let currentlyPlayingAudio = null;
function initializeTrack(containerSound) {
    const playButton = containerSound.querySelector('.play-button');
    const pauseButton = containerSound.querySelector('.pause-button');
    const audioPlayer = containerSound.querySelector('.audio-player');
    const trackUrl = audioPlayer.src;
    audioPlayer.src = trackUrl + '?v=' + Math.random();
    const currentTimeDisplay = containerSound.querySelector('.current-time');
    const durationDisplay = containerSound.querySelector('.duration');
    const progressBar = containerSound.querySelector('.progress-bar');
    const progressThumb = containerSound.querySelector('.progress-thumb');
    const progressContainer = containerSound.querySelector('.progress-container');

    let isPlaying = false;

    function formatTime(seconds) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = Math.floor(seconds % 60);
        const formattedSeconds = remainingSeconds < 10 ? '0' + remainingSeconds : remainingSeconds;
        return `${minutes}:${formattedSeconds}`;
    }

    function playTrack() {
        if (currentlyPlayingAudio && currentlyPlayingAudio !== audioPlayer) {
            currentlyPlayingAudio.pause();
            const containerOfCurrentlyPlaying = currentlyPlayingAudio.closest('.container-sound');
            if (containerOfCurrentlyPlaying) {
                const playButtonOfCurrentlyPlaying = containerOfCurrentlyPlaying.querySelector('.play-button');
                const pauseButtonOfCurrentlyPlaying = containerOfCurrentlyPlaying.querySelector('.pause-button');
                if (playButtonOfCurrentlyPlaying) {
                    playButtonOfCurrentlyPlaying.style.display = 'flex';
                }
                if (pauseButtonOfCurrentlyPlaying) {
                    pauseButtonOfCurrentlyPlaying.style.display = 'none';
                }
            }
        }

        audioPlayer.play();
        playButton.style.display = 'none';
        pauseButton.style.display = 'flex';
        isPlaying = true;
        currentlyPlayingAudio = audioPlayer; 

        updateDuration();
    }

    function pauseTrack() {
        audioPlayer.pause();
        pauseButton.style.display = 'none';
        playButton.style.display = 'flex';
        isPlaying = false;
        currentlyPlayingAudio = null;
    }

    function updateDuration() {
        if (audioPlayer.duration) {
            durationDisplay.textContent = formatTime(audioPlayer.duration);
        }
    }

    playButton.addEventListener('click', function() {
        playTrack();
    });

    pauseButton.addEventListener('click', function() {
        pauseTrack();
    });

    audioPlayer.addEventListener('timeupdate', function() {
        const currentTime = audioPlayer.currentTime;
        const duration = audioPlayer.duration;


        currentTimeDisplay.textContent = formatTime(currentTime);

        if (duration) {
            const progress = (currentTime / duration) * 100;
            progressBar.style.width = `${progress}%`;
            progressThumb.style.left = `calc(${progress}% - 10px)`;
        }
    });

    audioPlayer.addEventListener('loadedmetadata', function() {
            updateDuration();
    });

    progressContainer.addEventListener('click', function(e) {
        const duration = audioPlayer.duration;
        if (!duration) return; 

        const clickPosition = e.offsetX;
        const progressBarWidth = progressContainer.offsetWidth;
        const seekTime = (clickPosition / progressBarWidth) * duration;

        audioPlayer.currentTime = seekTime;
    });

    audioPlayer.addEventListener('timeupdate', function() {
        const currentTime = audioPlayer.currentTime;
        const duration = audioPlayer.duration;
        if (duration) {
            const progress = (currentTime / duration) * 100;
            progressBar.style.width = `${progress}%`;
            progressThumb.style.left = `calc(${progress}% - 10px)`;
        }
    });

        audioPlayer.addEventListener('ended', function() {
            pauseTrack();
            audioPlayer.currentTime = 0;
            progressBar.style.width = '0%';
            progressThumb.style.left = '0';
        });
}


function escapeHtml(unsafe) {
    if (typeof unsafe !== 'string') {
        return '';
    }
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// Функция загрузки трека с обработкой ошибок
function uploadTrack() {
    const form = document.getElementById('uploadForm');
    if (!form) {
        showNotification('Форма загрузки не найдена', 'error');
        return;
    }

    clearMessages();

    const formData = new FormData(form);
    const submitButton = form.querySelector('button[type="button"]');
    const originalButtonText = submitButton.textContent;

    submitButton.disabled = true;
    submitButton.textContent = 'Загрузка...';

    fetch('/static/Scripts/process_upload.php', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(`Network response was not ok: ${text}`);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('Response from server:', data);
        if (data.status === 'success') {
            showNotification(data.message, 'success');
            addTrackToTrackList(data.track);

            const noTracksMessage = document.getElementById('noTracksMessage');
            if (noTracksMessage) {
                noTracksMessage.style.display = 'none';
            }

            form.reset();
        } else if (data.status === 'error') {
            if (data.errors && typeof data.errors === 'object') {
                for (const field in data.errors) {
                    if (data.errors.hasOwnProperty(field)) {
                        const errorElementId = `${field}_error`;
                        const errorElement = document.getElementById(errorElementId);
                        if (errorElement) {
                            errorElement.textContent = data.errors[field];
                        } else {
                            console.warn(`Элемент с id "${errorElementId}" не найден.`);
                        }
                    }
                }
            } else {
                showNotification(data.message, 'error');
            }
        }
    })
    .catch(error => {
        console.error('Upload error:', error);
        showNotification('Произошла ошибка при отправке запроса: ' + error.message, 'error');
    })
    .finally(() => {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
    });
}

function clearMessages() {
    document.querySelectorAll('.error-message').forEach(el => {
        el.textContent = '';
    });
    const successElement = document.getElementById('success_message');
    if (successElement) successElement.textContent = '';
}

function likeTrack(trackId, element) {
    console.log("likeTrack called with trackId:", trackId, "and element:", element);
    const form = element.closest('form');
    console.log("form",form)
    const userId = form.querySelector('input[name="user_id"]').value;
    const messageId = 'like-required-message-' + trackId;
     console.log("messageId",messageId)
    function showPopup(element, popupId) {
        if (!popupId) {
            console.error("Invalid popupId:", popupId);
            return;
        }

    
        const popup = element.closest('.button-container').querySelector('#' + popupId);

        if (!popup) {
            console.error("Popup not found with ID:", popupId);
            return;
        }
        popup.style.display = 'block';

        document.addEventListener('click', function(event) {
            if (!element.closest('.button-container').contains(event.target) && !popup.contains(event.target)) {
                popup.style.display = 'none';
                document.removeEventListener('click', arguments.callee);
            }
        });
    }

    if (!userId) {
        showPopup(form, messageId);
        return;
    }

    fetch('/static/Scripts/like_track.php', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: 'user_id=' + encodeURIComponent(userId) + '&track_id=' + encodeURIComponent(trackId)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        if (data.redirect) {
            window.location.href = '../My-music.php';
            return; 
        }

        if (data.status === 'liked') {
            element.classList.add('liked');
        } else if (data.status === 'unliked') {
            element.classList.remove('liked');
        }
    })
    .catch(error => {
        console.error('Ошибка при лайке:', error);
    });
}

function downloadTrack(trackId, button) {
    const container = button.closest('.download-container');
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
                throw new Error(`Network response was not ok: ${text}`);
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
        console.error('Download error:', error);
        showNotification('Произошла ошибка при скачивании трека: ' + error.message, 'error');
    });
}

function deleteTrack(trackId, trackElement) {
    const deleteForm = document.getElementById('delete-form-' + trackId);

    if (!deleteForm) {
        alert('Не удалось найти форму удаления.');
        return;
    }

    const formData = new FormData(deleteForm);
    const userId = formData.get('user_id');

    if (!userId) {
        alert('Необходимо авторизоваться для удаления трека.');
        return;
    }

    fetch('/static/Scripts/delete_track.php', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'track_id=' + trackId + '&user_id=' + userId,
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            trackElement.remove(); 

            const containerSounds = document.querySelectorAll('.container-sound');
            if (containerSounds.length === 0) {
                const noTracksMessage = document.getElementById('noTracksMessage');
                if (noTracksMessage) {
                    noTracksMessage.style.display = 'block';
                }
            }
        } else {
            alert('Ошибка при удалении трека: ' + data.message);
        }
    })
    .catch(error => {
        console.error('Ошибка:', error);
        alert('Произошла ошибка при удалении трека.');
    });
}

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

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.trash-icon').forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault(); 
            const trackId = this.closest('.delete-form').dataset.trackId;
            const trackElement = this.closest('.container-sound');
            deleteTrack(trackId, trackElement);
        });
    });

    document.querySelectorAll('.heart-icon').forEach(button => {
        button.addEventListener('click', function() {
          likeTrack(this.closest('.container-sound').id.split('-')[1], this);
        });
    });

    document.querySelectorAll('.button-load').forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            downloadTrack(this.closest('.container-sound').id.split('-')[1], this);
        });
    });

    document.querySelectorAll('.container-sound').forEach(containerSound => {
        initializeTrack(containerSound);
    });

    const uploadForm = document.getElementById('uploadForm');
    if (uploadForm) {
        uploadForm.addEventListener('submit', function(e) {
            e.preventDefault();
            uploadTrack();
        });
    }
});