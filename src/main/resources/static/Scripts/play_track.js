// player.js - с поддержкой прогресс-бара для динамических элементов
(function() {
    if (window.audioPlayerInitialized) return;
    window.audioPlayerInitialized = true;

    let currentlyPlayingAudio = null;

    // Делегирование событий для кликов
    document.addEventListener('click', function(e) {
        const playButton = e.target.closest('.play-button');
        const pauseButton = e.target.closest('.pause-button');
        const progressContainer = e.target.closest('.progress-container');

        if (playButton) {
            handlePlayButton(playButton);
            e.preventDefault();
            e.stopPropagation();
        }

        if (pauseButton) {
            handlePauseButton(pauseButton);
            e.preventDefault();
            e.stopPropagation();
        }

        if (progressContainer) {
            handleProgressClick(progressContainer, e);
            e.preventDefault();
            e.stopPropagation();
        }
    });

    // Воспроизведение трека
    function handlePlayButton(playButton) {
        const containerSound = playButton.closest('.container-sound');
        const audioPlayer = containerSound.querySelector('.audio-player');
        const pauseButton = containerSound.querySelector('.pause-button');
        const durationDisplay = containerSound.querySelector('.duration');

        if (!audioPlayer) return;

        // Останавливаем текущий трек если он есть
        if (currentlyPlayingAudio && currentlyPlayingAudio !== audioPlayer) {
            currentlyPlayingAudio.pause();
            const prevContainer = currentlyPlayingAudio.closest('.container-sound');
            const prevPlay = prevContainer.querySelector('.play-button');
            const prevPause = prevContainer.querySelector('.pause-button');
            if (prevPlay) prevPlay.style.display = 'flex';
            if (prevPause) prevPause.style.display = 'none';
        }

        // Воспроизводим новый трек
        audioPlayer.play()
            .then(() => {
                playButton.style.display = 'none';
                if (pauseButton) pauseButton.style.display = 'flex';
                currentlyPlayingAudio = audioPlayer;

                // Обновляем длительность
                if (durationDisplay && (!durationDisplay.textContent || durationDisplay.textContent === '0:00')) {
                    updateDurationForPlayer(audioPlayer, durationDisplay);
                }
            })
            .catch(error => {
                console.error('Ошибка воспроизведения:', error);
            });
    }

    // Пауза трека
    function handlePauseButton(pauseButton) {
        const containerSound = pauseButton.closest('.container-sound');
        const audioPlayer = containerSound.querySelector('.audio-player');
        const playButton = containerSound.querySelector('.play-button');

        if (!audioPlayer) return;

        audioPlayer.pause();
        pauseButton.style.display = 'none';
        if (playButton) playButton.style.display = 'flex';

        if (currentlyPlayingAudio === audioPlayer) {
            currentlyPlayingAudio = null;
        }
    }

    // Клик по прогресс-бару
    function handleProgressClick(progressContainer, event) {
        const containerSound = progressContainer.closest('.container-sound');
        const audioPlayer = containerSound.querySelector('.audio-player');
        const progressBar = containerSound.querySelector('.progress-bar');
        const progressThumb = containerSound.querySelector('.progress-thumb');

        if (!audioPlayer || isNaN(audioPlayer.duration)) return;

        const rect = progressContainer.getBoundingClientRect();
        const pos = (event.clientX - rect.left) / rect.width;
        audioPlayer.currentTime = pos * audioPlayer.duration;

        // Обновляем прогресс-бар
        if (progressBar) progressBar.style.width = `${pos * 100}%`;
        if (progressThumb) progressThumb.style.left = `calc(${pos * 100}% - 10px)`;
    }

    // Обновление длительности
    function updateDurationForPlayer(audioPlayer, durationDisplay) {
        if (audioPlayer.duration) {
            const minutes = Math.floor(audioPlayer.duration / 60);
            const seconds = Math.floor(audioPlayer.duration % 60);
            durationDisplay.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
        }
    }

    // Инициализация timeupdate для аудиоплеера
    function initAudioPlayerTimeUpdate(audioPlayer) {
        if (audioPlayer.dataset.timeupdateInitialized === 'true') {
            return; // Уже инициализирован
        }

        const containerSound = audioPlayer.closest('.container-sound');
        const currentTimeDisplay = containerSound.querySelector('.current-time');
        const progressBar = containerSound.querySelector('.progress-bar');
        const progressThumb = containerSound.querySelector('.progress-thumb');
        const durationDisplay = containerSound.querySelector('.duration');

        audioPlayer.dataset.timeupdateInitialized = 'true';

        // Обновление времени и прогресс-бара
        audioPlayer.addEventListener('timeupdate', function() {
            const currentTime = audioPlayer.currentTime;
            const duration = audioPlayer.duration;

            // Обновляем текущее время
            if (currentTimeDisplay) {
                const minutes = Math.floor(currentTime / 60);
                const seconds = Math.floor(currentTime % 60);
                currentTimeDisplay.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
            }

            // Обновляем прогресс-бар
            if (duration && progressBar && progressThumb) {
                const progress = (currentTime / duration) * 100;
                progressBar.style.width = `${progress}%`;
                progressThumb.style.left = `calc(${progress}% - 10px)`;
            }
        });

        // Загрузка метаданных (длительность)
        audioPlayer.addEventListener('loadedmetadata', function() {
            if (durationDisplay && audioPlayer.duration) {
                const minutes = Math.floor(audioPlayer.duration / 60);
                const seconds = Math.floor(audioPlayer.duration % 60);
                durationDisplay.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
            }
        });

        // Обработка окончания трека
        audioPlayer.addEventListener('ended', function() {
            const playButton = containerSound.querySelector('.play-button');
            const pauseButton = containerSound.querySelector('.pause-button');

            if (pauseButton) pauseButton.style.display = 'none';
            if (playButton) playButton.style.display = 'flex';
            if (progressBar) progressBar.style.width = '0%';
            if (progressThumb) progressThumb.style.left = '0';
            if (currentTimeDisplay) currentTimeDisplay.textContent = '0:00';

            if (currentlyPlayingAudio === audioPlayer) {
                currentlyPlayingAudio = null;
            }
        });

        console.log('✅ Инициализирован прогресс-бар для аудиоплеера');
    }

    // Инициализация всех аудиоплееров при загрузке
    function initAllAudioPlayers() {
        console.log('🎵 Инициализация всех аудиоплееров и прогресс-баров');

        document.querySelectorAll('.audio-player').forEach(audioPlayer => {
            initAudioPlayerTimeUpdate(audioPlayer);
        });
    }

    // Инициализация при загрузке DOM
    document.addEventListener('DOMContentLoaded', function() {
        console.log('🎵 Инициализация аудиоплееров...');
        initAllAudioPlayers();
    });

    // Функция для инициализации конкретного контейнера (для новых треков)
    window.initSingleAudioPlayer = function(containerSound) {
        const audioPlayer = containerSound.querySelector('.audio-player');
        if (audioPlayer) {
            initAudioPlayerTimeUpdate(audioPlayer);
            console.log('✅ Инициализирован прогресс-бар для нового трека');
        }

        // Также инициализируем клик по прогресс-бару если он еще не работает
        const progressContainer = containerSound.querySelector('.progress-container');
        if (progressContainer && !progressContainer.dataset.clickInitialized) {
            progressContainer.dataset.clickInitialized = 'true';

            progressContainer.addEventListener('click', function(e) {
                const container = this.closest('.container-sound');
                const audio = container.querySelector('.audio-player');
                const progressBar = container.querySelector('.progress-bar');
                const progressThumb = container.querySelector('.progress-thumb');

                if (!audio || isNaN(audio.duration)) return;

                const rect = this.getBoundingClientRect();
                const pos = (e.clientX - rect.left) / rect.width;
                audio.currentTime = pos * audio.duration;

                if (progressBar) progressBar.style.width = `${pos * 100}%`;
                if (progressThumb) progressThumb.style.left = `calc(${pos * 100}% - 10px)`;
            });
        }
    };

    window.initAudioPlayers = initAllAudioPlayers;

})();