<?php
session_start();
include '../../database/db_connect.php';

$response = [];
$errors = [];

try {
    $user_id = $_SESSION['id'];

    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        $track_name = trim($_POST["track_name"]);
        $artist = trim($_POST["artist"]);
        $genre = isset($_POST['genre']) ? trim($_POST['genre']) : '';

        if (empty($track_name)) {
            $errors['track_name'] = "Название трека обязательно";
        }
        if (empty($artist)) {
            $errors['artist'] = "Указание исполнителя обязательно";
        }
        if (empty($genre)) {
            $errors['genre'] = "Укажите жанр";
        }

        $target_dir_tracks = $_SERVER["DOCUMENT_ROOT"] . "/static/Music/";
        $target_dir_images = $_SERVER["DOCUMENT_ROOT"] . "/static/Images/";
        $uploadOkTrack = 1;
        $uploadOkImage = 1; 
        $track_file_name = '';
        $image_file_name = '';
        $trackFileType = '';
        $imageFileType = '';
        // ====================================================================
        // Проверки и обработка файла трека
        // ====================================================================
        if (isset($_FILES["track_file"]) && $_FILES["track_file"]["error"] === UPLOAD_ERR_OK) {
            $track_file_name = basename($_FILES["track_file"]["name"]);
            $unique_track_name = uniqid('track_') . '_' . $track_file_name;
            $target_file_track = $target_dir_tracks . $unique_track_name;
            $trackFileType = strtolower(pathinfo($target_file_track, PATHINFO_EXTENSION));

            // Проверяем размер файла трека
            if ($_FILES["track_file"]["size"] > 50000000) {
                $errors['track_file'] = "Извините, размер файла трека слишком велик";
                $uploadOkTrack = 0;
            }

            // Разрешаем определенные форматы файлов треков
            $allowedTrackTypes = ["mp3", "wav", "flac"];
            if (!in_array($trackFileType, $allowedTrackTypes)) {
                $errors['track_file'] = "Извините, разрешены только MP3, WAV и FLAC файлы.";
                $uploadOkTrack = 0;
            }

            if ($uploadOkTrack === 1) {    
                if (!move_uploaded_file($_FILES["track_file"]["tmp_name"], $target_file_track)) {
                    $error = error_get_last();
                    $errors['track_file'] = "Ошибка при перемещении трека: ".$error['message'];
                    error_log("Track upload error: ".print_r($error, true));
                }
            }

        } else {
            $errors['track_file'] = "Необходимо загрузить файл трека";
            $uploadOkTrack = 0;
        }

        // ====================================================================
        // Проверки и обработка файла изображения
        // ====================================================================
        if (isset($_FILES["image_file"]) && $_FILES["image_file"]["error"] === UPLOAD_ERR_OK) {
            $image_file_name = basename($_FILES["image_file"]["name"]);
            $unique_image_name = uniqid('img_') . '_' . $image_file_name;
            $target_file_image = $target_dir_images . $unique_image_name;
            $imageFileType = strtolower(pathinfo($target_file_image, PATHINFO_EXTENSION));

            // Проверяем размер файла изображения
            if ($_FILES["image_file"]["size"] > 5000000) {
                $errors['image_file'] = "Извините, размер файла изображения слишком велик.";
                $uploadOkImage = 0;
            }

            // Разрешаем определенные форматы файлов изображений
            $allowedImageTypes = ["jpg", "png", "jpeg", "gif"];
            if (!in_array($imageFileType, $allowedImageTypes)) {
                $errors['image_file'] = "Извините, разрешены только JPG, JPEG, PNG и GIF файлы.";
                $uploadOkImage = 0;
            }

            if ($uploadOkImage === 1) {
                if (!move_uploaded_file($_FILES["image_file"]["tmp_name"], $target_file_image)) {
                    $errors['image_file'] = "Ошибка при перемещении файла изображения.";
                } else {
                    $image_file_name = $unique_image_name; // Используем уже сгенерированное имя
                }
            }
        } else {
            $errors['image_file'] = "Необходимо загрузить файл изображения";
            $uploadOkImage = 0;
        }
        // ====================================================================
        //  Перемещение файлов
        // ====================================================================

        // Перемещаем загруженный файл трека
        if (!empty($errors)) {
            // Если есть ошибки, возвращаем JSON с ошибками
            $response["status"] = "error";
            $response["message"] = "Пожалуйста, исправьте ошибки в форме.";
            $response["errors"] = $errors;  // Передаём массив ошибок 
        } else {
        // Получаем относительный путь
        $track_url_to_db = str_replace($_SERVER["DOCUMENT_ROOT"], '', $target_file_track);
        $image_url_to_db = str_replace($_SERVER["DOCUMENT_ROOT"], '', $target_file_image);
        $sql = "INSERT INTO tracks (user_id_add, track_name, artist, track_url, image_url, genre)
                VALUES ('$user_id', '$track_name', '$artist', '$track_url_to_db', '$image_url_to_db', '$genre')";

        if ($conn->query($sql) === TRUE) {
            $track_id = $conn->insert_id;
            $track = [
                "id" => $track_id,
                "track_name" => $track_name,
                "artist" => $artist,
                "genre" => $genre,
                "image_url" => $image_url_to_db,
                "track_url" => $track_url_to_db,
                "user_id" => $user_id
            ];
            $html = '<div class="main-genre-music">';
            $html = '<div class="container-sound" id="track-' . htmlspecialchars($track['id']) . '">';
            $html .= '    <div class="pause-button" style="display:none;">';
            $html .= '        <div class="rectangle"></div>';
            $html .= '        <div class="rectangle"></div>';
            $html .= '    </div>';
            $html .= '    <div class="play-button">';
            $html .= '        <div class="triangle"></div>';
            $html .= '    </div>';
            $html .= '    <img src="' . htmlspecialchars($track['image_url']) . '" alt="Пример изображения" class="image-sound">';
            $html .= '    <div class="all-duration">';
            $html .= '        <div class="container-duration">';
            $html .= '            <span class="time current-time">0:00</span>';
            $html .= '            <div class="main-pain">';
            $html .= '                <div class="text">' . htmlspecialchars($track['artist']) . ' - ' . htmlspecialchars($track['track_name']) . '</div>';
            $html .= '                <audio class="audio-player" src="' . htmlspecialchars($track['track_url']) . '" preload="metadata"></audio>';
            $html .= '                <div class="progress-container">';
            $html .= '                    <div class="progress-bar"></div>';
            $html .= '                    <div class="progress-thumb"></div>';
            $html .= '                </div>';
            $html .= '            </div>';
            $html .= '            <span class="time duration">0:00</span>';
            $html .= '        </div>';
            $html .= '    </div>';
            $html .= '    <div class="button-container">';
            $html .= '        <div class="login-required-popup like-required-popup" id="like-required-message-' . htmlspecialchars($track['id']) . '" style="display: none;">';
            $html .= '            Для лайка необходимо <a href="../../registration/login.php">войти</a> или <a href="../../registration/register.php">зарегистрироваться</a>';
            $html .= '        </div>';
            $html .= '        <form id="like-form-' . htmlspecialchars($track['id']) . '" data-track-id="' . htmlspecialchars($track['id']) . '">';
            $html .= '            <input type="hidden" name="user_id" value="' . (isset($_SESSION['id']) ? htmlspecialchars($_SESSION['id']) : '') . '">';
            $html .= '            <input type="hidden" name="track_id" value="' . htmlspecialchars($track['id']) . '">';
            $html .= '            <input type="hidden" name="current_page" value="' . htmlspecialchars($_SERVER['REQUEST_URI']) . '">';
            $html .= '            <button type="button" class="heart-icon"></button>';
            $html .= '        </form>';
            $html .= '        <div class="download-container" id="download-form-' . htmlspecialchars($track['id']) . '" data-track-id="' . htmlspecialchars($track['id']) . '">';
            $html .= '            <button type="button" class="button-load">';
            $html .= '                <div class="arrow-down"></div>';
            $html .= '                <div class="line"></div>';
            $html .= '            </button>';
            $html .= '            <div class="login-required-popup download-required-popup" id="download-required-message-' . htmlspecialchars($track['id']) . '">';
            $html .= '                Для скачивания необходимо <a href="../../registration/login.php">войти</a> или <a href="../../registration/register.php">зарегистрироваться</a>';
            $html .= '            </div>';
            $html .= '        </div>';
            $html .= '        <form id="delete-form-' . htmlspecialchars($track['id']) . '" class="delete-form" data-track-id="' . htmlspecialchars($track['id']) . '">';
            $html .= '            <input type="hidden" name="user_id" value="' . (isset($_SESSION['id']) ? htmlspecialchars($_SESSION['id']) : '') . '">';
            $html .= '            <input type="hidden" name="track_id" value="' . htmlspecialchars($track['id']) . '">';
            $html .= '            <button type="button" class="trash-icon" title="Удалить трек"></button>';
            $html .= '        </form>';
            $html .= '    </div>';
            $html .= '</div>';
            $html .= '</div>';
            $track["html"] =  $html;
            $response["status"] = "success";
            $response["message"] = "Трек успешно загружен и добавлен в базу данных!";
            $response["track"] = $track;
        } else {
            throw new Exception("Ошибка при записи в базу данных: " . $conn->error);
        }
    }
    } else {
        throw new Exception("Недопустимый метод запроса.");
    }

} catch (Exception $e) {
    $response["status"] = "error";
    $response["message"] = $e->getMessage();
} finally {
    if ($conn) {
        $conn->close();
    }
}
header('Content-Type: application/json');
echo json_encode($response);
exit;
?>