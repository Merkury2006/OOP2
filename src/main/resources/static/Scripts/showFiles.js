document.addEventListener('DOMContentLoaded', function() {
    
    function updateFileName(input, fileNameElementId) {
        const fileNameElement = document.getElementById(fileNameElementId);
        if (fileNameElement) {
            if (input.files.length > 0) {
                fileNameElement.textContent = input.files[0].name;
            } else {
                fileNameElement.textContent = 'Файл не выбран';
            }
        }
    }

    const trackFile = document.getElementById('track_file');
    if (trackFile) {
        trackFile.addEventListener('change', function() {
            updateFileName(trackFile, 'file-name');
        });
    }

    const imageFile = document.getElementById('image_file');
    if (imageFile) {
        imageFile.addEventListener('change', function() {
            updateFileName(imageFile, 'image-name');
        });
    }
});