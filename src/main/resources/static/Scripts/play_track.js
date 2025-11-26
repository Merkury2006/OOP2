document.addEventListener('DOMContentLoaded', function() {
    const containerSounds = document.querySelectorAll('.container-sound');

    let currentlyPlayingAudio = null;

    containerSounds.forEach(containerSound => {
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

    });
});