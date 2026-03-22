(function() {
    if (window.uploadTrackInitialized) {
        console.warn('upload-track.js уже был инициализирован');
        return;
    }
    window.uploadTrackInitialized = true;

    let isProcessing = false;

    // Функции валидации
    function clearMessages() {
        document.querySelectorAll('.error-message').forEach(el => {
            el.textContent = '';
        });
    }

    function showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
        } else {
            console.error(`Элемент с id "${elementId}" не найден`);
        }
    }

    function validateForm(form) {
        clearMessages();
        let isValid = true;

        // Проверка названия трека
        const trackName = form.querySelector('#track_name').value.trim();
        if (!trackName) {
            showError('track_name_error', 'Введите название трека');
            isValid = false;
        }

        // Проверка жанра
        const genre = form.querySelector('#genre').value;
        if (!genre) {
            showError('genre_error', 'Выберите жанр');
            isValid = false;
        }

        // Проверка файла трека
        const trackFile = form.querySelector('#track_file').files[0];
        if (!trackFile) {
            showError('track_file_error', 'Выберите аудиофайл');
            isValid = false;
        } else {
            const allowedAudioExtensions = ['.mp3', '.wav', '.m4a', '.flac'];
            const fileName = trackFile.name.toLowerCase();
            const isValidExtension = allowedAudioExtensions.some(ext => fileName.endsWith(ext));

            if (!isValidExtension) {
                showError('track_file_error', 'Недопустимый формат аудиофайла. Разрешены: MP3, WAV, M4A, FLAC');
                isValid = false;
            }

            const maxSize = 50 * 1024 * 1024;
            if (trackFile.size > maxSize) {
                showError('track_file_error', `Файл слишком большой. Максимальный размер: 50MB`);
                isValid = false;
            }
        }

        // Проверка файла изображения
        const imageFile = form.querySelector('#image_file').files[0];
        if (!imageFile) {
            showError('image_file_error', 'Выберите изображение');
            isValid = false;
        } else {
            const allowedImageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];
            const fileName = imageFile.name.toLowerCase();
            const isValidExtension = allowedImageExtensions.some(ext => fileName.endsWith(ext));

            if (!isValidExtension) {
                showError('image_file_error', 'Недопустимый формат изображения. Разрешены: JPG, PNG, GIF, WebP');
                isValid = false;
            }

            const maxSize = 10 * 1024 * 1024;
            if (imageFile.size > maxSize) {
                showError('image_file_error', `Файл слишком большой. Максимальный размер: 10MB`);
                isValid = false;
            }
        }

        return isValid;
    }

    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';

        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));

        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    async function processUpload() {
        if (isProcessing) {
            console.log('Уже идет загрузка...');
            return;
        }

        const form = document.getElementById('uploadForm');
        if (!form) {
            PopupUtils.showNotification('Форма загрузки не найдена', 'error');
            return;
        }

        if (!validateForm(form)) {
            return;
        }

        isProcessing = true;

        const formData = new FormData(form);
        const submitButton = form.querySelector('.button-add-new-track');
        const originalButtonText = submitButton.textContent;

        submitButton.disabled = true;
        submitButton.textContent = 'Загрузка...';

        try {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

            const response = await fetch('/api/upload', {
                method: 'POST',
                body: formData,
                credentials: 'include',
                headers: {
                    ...(csrfToken && csrfHeader && { [csrfHeader]: csrfToken })
                }
            });

            const apiResponse = await response.json();
            console.log('Upload response:', apiResponse);

            if (!apiResponse.success) {
                // Используем сообщение из ответа или общее
                const errorMessage = apiResponse.message ||
                    `Ошибка ${response.status}: ${getStatusMessage(response.status)}`;
                throw new Error(errorMessage);
            }


            if (apiResponse.success) {
                PopupUtils.showNotification('🎵 Трек успешно загружен!', 'success');

                // Добавляем трек в список без перезагрузки
                addTrackToUI(apiResponse.data);

                // Очищаем форму
                form.reset();
                document.getElementById('file-name').textContent = 'Файл не выбран';
                document.getElementById('image-name').textContent = 'Файл не выбран';

                // Прячем сообщение "нет треков"
                const noTracksMessage = document.querySelector('.no-tracks-message');
                if (noTracksMessage) {
                    noTracksMessage.style.display = 'none';
                }
            } else {
                PopupUtils.showNotification(apiResponse.message || 'Ошибка при загрузке', 'error');
            }
        } catch (error) {
            console.error('Upload error:', error);
            PopupUtils.showNotification('Ошибка: ' + error.message, 'error');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = originalButtonText;
            isProcessing = false;
        }
    }

    function getStatusMessage(status) {
        const messages = {
            400: 'Некорректные данные',
            401: 'Требуется авторизация',
            403: 'Доступ запрещен',
            404: 'Не найдено',
            413: 'Файл слишком большой',
            415: 'Неподдерживаемый формат',
            500: 'Ошибка сервера',
            503: 'Сервис временно недоступен'
        };
        return messages[status] || `HTTP ошибка ${status}`;
    }

    // Функция для добавления трека в UI
    function addTrackToUI(trackData) {
        const tracksContainer = document.querySelector('.main-genre-music');
        if (!tracksContainer) {
            console.error('Tracks container not found');
            return;
        }

        // Проверяем, есть ли сообщение "нет треков"
        const noTracksMessage = document.querySelector('.no-tracks-message');
        if (noTracksMessage) {
            noTracksMessage.style.display = 'none';
        }

        // Создаем HTML для нового трека
        const trackElement = createTrackElement(trackData);

        // Добавляем с анимацией
        trackElement.style.opacity = '0';
        trackElement.style.transform = 'translateY(-20px)';

        // Добавляем в начало списка
        const firstTrack = tracksContainer.querySelector('.container-sound');
        if (firstTrack) {
            tracksContainer.insertBefore(trackElement, firstTrack);
        } else {
            tracksContainer.appendChild(trackElement);
        }

        // Анимация появления
        setTimeout(() => {
            trackElement.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            trackElement.style.opacity = '1';
            trackElement.style.transform = 'translateY(0)';

            // ✅ ВАЖНО: Инициализируем плеер для нового элемента
            if (window.initSingleAudioPlayer) {
                window.initSingleAudioPlayer(trackElement);
                console.log('✅ Плеер инициализирован для нового трека');
            } else {
                console.warn('⚠️ Функция initSingleAudioPlayer не найдена, пытаемся переинициализировать все');
                if (window.initAudioPlayers) {
                    window.initAudioPlayers();
                }
            }
        }, 50);
    }

    // Создание HTML элемента трека
    function createTrackElement(trackData) {
        const trackDiv = document.createElement('div');
        trackDiv.className = 'container-sound';
        trackDiv.id = `track-${trackData.trackId}`;

        // Используем тот же HTML шаблон, что и на сервере
        trackDiv.innerHTML = `
            <div class="pause-button" style="display:none;">
                <div class="rectangle"></div>
                <div class="rectangle"></div>
            </div>
            <div class="play-button">
                <div class="triangle"></div>
            </div>

            <img src="${trackData.imageUrl}" alt="Обложка трека" class="image-sound">

            <div class="all-duration">
                <div class="container-duration">
                    <span class="time current-time">0:00</span>
                    <div class="main-pain">
                        <div class="text">${trackData.artist ? trackData.artist + ' - ' : ''}${trackData.trackName}</div>
                        <audio class="audio-player" src="${trackData.trackUrl}" preload="metadata"></audio>
                        <div class="progress-container">
                            <div class="progress-bar"></div>
                            <div class="progress-thumb"></div>
                        </div>
                    </div>
                    <span class="time duration">0:00</span>
                </div>
            </div>

            <div class="button-container">
                <div class="like-container" id="like-form-${trackData.trackId}" data-track-id="${trackData.trackId}">
                    <button type="button" class="heart-icon">
                        <i class="far fa-heart"></i>
                    </button>
                </div>

                <div class="download-container" id="download-form-${trackData.trackId}" data-track-id="${trackData.trackId}">
                    <button type="button" class="button-load" data-track-id="${trackData.trackId}">
                        <div class="arrow-down"></div>
                        <div class="line"></div>
                    </button>
                </div>

                <div class="delete-container" id="delete-form-${trackData.trackId}" data-track-id="${trackData.trackId}">
                    <button type="button" class="trash-icon" data-track-id="${trackData.trackId}" title="Удалить трек"></button>
                </div>
            </div>
        `;

        return trackDiv;
    }

    // Инициализация формы
    function initForm() {
        const uploadForm = document.getElementById('uploadForm');
        const addTrackButton = document.querySelector('.button-add-new-track');

        if (uploadForm && addTrackButton) {
            console.log('✅ Найдены форма и кнопка загрузки');

            // Обработчик для кнопки
            addTrackButton.addEventListener('click', function(e) {
                e.preventDefault();
                console.log('🖱️ Кнопка "Добавить трек" нажата');
                processUpload();
            });

            // Обработчик для формы submit
            uploadForm.addEventListener('submit', function(e) {
                e.preventDefault();
                console.log('📝 Событие submit формы');
                processUpload();
            });

            // Сброс ошибок при изменении полей
            const inputs = uploadForm.querySelectorAll('input, select');
            inputs.forEach(input => {
                input.addEventListener('change', function() {
                    const errorId = this.id + '_error';
                    const errorElement = document.getElementById(errorId);
                    if (errorElement) {
                        errorElement.textContent = '';
                    }
                });
            });
        } else {
            console.error('❌ Не найдены форма или кнопка:', {
                form: !!uploadForm,
                button: !!addTrackButton
            });
        }
    }

    // Инициализация файловых инпутов
    function setupFileInputs() {
        const trackFileInput = document.getElementById('track_file');
        const trackFileNameSpan = document.getElementById('file-name');

        if (trackFileInput && trackFileNameSpan) {
            trackFileInput.addEventListener('change', function() {
                if (this.files.length > 0) {
                    const file = this.files[0];
                    const fileSize = formatFileSize(file.size);
                    trackFileNameSpan.textContent = `${file.name} (${fileSize})`;
                    const errorElement = document.getElementById('track_file_error');
                    if (errorElement) errorElement.textContent = '';
                } else {
                    trackFileNameSpan.textContent = 'Файл не выбран';
                }
            });
        }

        const imageFileInput = document.getElementById('image_file');
        const imageFileNameSpan = document.getElementById('image-name');

        if (imageFileInput && imageFileNameSpan) {
            imageFileInput.addEventListener('change', function() {
                if (this.files.length > 0) {
                    const file = this.files[0];
                    const fileSize = formatFileSize(file.size);
                    imageFileNameSpan.textContent = `${file.name} (${fileSize})`;
                    const errorElement = document.getElementById('image_file_error');
                    if (errorElement) errorElement.textContent = '';
                } else {
                    imageFileNameSpan.textContent = 'Файл не выбран';
                }
            });
        }
    }

    // Инициализация при загрузке DOM
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📁 upload-track.js инициализирован');
        initForm();
        setupFileInputs();
    });

    // Экспортируем функцию для внешнего использования
    window.uploadTrack = processUpload;
})();