<?php
session_start();
include '../database/db_connect.php';

$userTracks = [];
$noTracksMessage = "";
$showLoginForm = false;

if (isset($_SESSION['id'])) {
    $user_id = $_SESSION['id'];

    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        if ($conn) {
            $conn->close();
        }
    }
    if (isset($_SESSION['loggedin']) && $_SESSION['loggedin'] === true) {
        $user_id = $_SESSION['id'];
        $sql = "SELECT tracks.id
                FROM tracks
                INNER JOIN likes ON tracks.id = likes.track_id
                WHERE likes.user_id = ?";

        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();

        $liked_track_ids = array();
        if ($result->num_rows > 0) {
            while ($row = $result->fetch_assoc()) {
                $liked_track_ids[] = $row['id'];
            }
        }
        $stmt->close();
    } else {
        $liked_track_ids = array();
    }

    $sql_user_tracks = "SELECT id, track_name, artist, image_url, track_url FROM tracks WHERE user_id_add = ?";
    $stmt_user_tracks = $conn->prepare($sql_user_tracks);
    $stmt_user_tracks->bind_param("i", $user_id);
    $stmt_user_tracks->execute();
    $result_user_tracks = $stmt_user_tracks->get_result();

    if ($result_user_tracks->num_rows > 0) {
        while ($row = $result_user_tracks->fetch_assoc()) {
            $userTracks[] = $row;
        }
    } else {
        $noTracksMessage = "Вы пока не добавили ни одного трека.";
    }
    $userTracks = array_reverse($userTracks);
    $stmt_user_tracks->close();

} else {
    $showLoginForm = true;
}

if ($conn) {
    $conn->close();
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Добавить свою музыку</title>
    <link rel="icon" href="/static/favicon.ico" type="image/x-icon">
    <link rel="icon" href="/static/favicon-32x32.png" type="image/png" sizes="32x32">
    <link rel="icon" href="/static/favicon-16x16.png" type="image/png" sizes="16x16">
    <link rel="stylesheet" href="./Styles/stylesPage.css">
    <link rel="stylesheet" href="./Styles/styleIcons.css">
    <link rel="stylesheet" href="./Styles/styleGenreMusic.css">
    <link rel="stylesheet" href="./Styles/fonts.css">
    <link rel="stylesheet" href="./Styles/styleAddTrack.css">
    
</head>
<body>

<header>
    <div class="head-items">
        <img class="logo-site" src="Images/logo-site.png">
        <a href="../index.php" class="head-item">Главная</a>
        <a href="My-music.php" class="head-item">Моя музыка</a>
        <a href = "AboutUs.php" class="head-item">О нас</a>
        <a href = "upload_track.php" class="head-item-active">Добавить музыку</a>
    </div>
    <div class="theme-switch">
        <img src="Images/social/lightTheme.png" id="light-theme-button" class="active">
        <img src="Images/social/darkTheme.png" id="dark-theme-button">
    </div>
    <div class="user-info <?php echo isset($_SESSION['loggedin']) && $_SESSION['loggedin'] === true ? 'logged-in' : ''; ?>" id="user-info">
        <?php if (isset($_SESSION['loggedin']) && $_SESSION['loggedin'] === true): ?>
            <div class="welcome-message" id="welcome-message">
                <span id="user-nickname"><?php echo htmlspecialchars($_SESSION['username']); ?></span>
                <a href="../registration/logout.php" class="link" id="logout-link">Выход</a>
            </div>
        <?php else: ?>
            <div class="registration-links" id="registration-links">
                <a href="../registration/register.php" class="registration-item">Регистрация</a>
                <a href="../registration/login.php" class="login-item">Вход</a>
            </div>
        <?php endif; ?>
    </div>
</header>

<div class="container">
    <div class="sidebar-left">
        <a class="sidebar-item" href="./PagesMusic/Pop-music.php">Поп-музыка</a>
        <a class="sidebar-item" href="./PagesMusic/Rock-music.php">Рок-музыка</a>
        <a class="sidebar-item" href="./PagesMusic/Rep-music.php">Рэп-музыка</a>
        <a class="sidebar-item" href="./PagesMusic/Hip-hop-music.php">Хип-хоп</a>
        <a class="sidebar-item" href="./PagesMusic/Electronic-music.php">Электронная музыка</a>
        <a class="sidebar-item" href="./PagesMusic/Classic-music.php">Классика</a>
        <a class="sidebar-item" href="./PagesMusic/Fonk-music.php">Фонк</a>
        <a class="sidebar-item" href="./PagesMusic/Jazz-music.php">Джаз</a>
        <a class="sidebar-item" href="./PagesMusic/Metallic-music.php">Металл</a>
    </div>

    <div class="main-content">
        <?php
        if ($showLoginForm) {
            ?>
            <div class='not-logged-in-message-upload-track'>
                <p>Чтобы добавлять свои треки на сайт, вам нужно <a href='../registration/login.php'>войти</a> или <a href='../registration/register.php'>зарегистрироваться</a></p>
            </div>
            <?php
        } else {
            ?>
             <div id="notification-container"></div>
            <form id="uploadForm" enctype="multipart/form-data" class="track-form">
                <div class="label">Добавить свой трек</div>
                <div class="form-group">
                    <label for="track_name">Название трека</label><br>
                    <input type="text" id="track_name" name="track_name" required><br>
                    <span id="track_name_error" class="error-message"></span>
                </div>
                <div class="form-group">
                    <label for="artist">Исполнитель</label><br>
                    <input type="text" id="artist" name="artist"><br>
                    <span id="artist_error" class="error-message"></span>
                </div>
                <div class="form-group">
                    <div class="genre-label">
                        <label for="genre">Жанр</label>
                    </div>
                    <div class="genre-select-container">
                        <select name="genre" id="genre" required>
                            <option value="" disabled selected>Выберите жанр</option>
                            <option value="Поп музыка">Поп-музыка</option>
                            <option value="Рок музыка">Рок-музыка</option>
                            <option value="Рэп музыка">Рэп-музыка</option>
                            <option value="Хип-хоп музыка">Хип-хоп музыка</option>
                            <option value="Электронная музыка">Электронная музыка</option>
                            <option value="Классическая музыка">Классика</option>
                            <option value="Фонк музыка">Фонк</option>
                            <option value="Джаз музыка">Джаз</option>
                            <option value="Металл музыка">Металл</option>
                        </select>
                        <span id="genre_error" class="error-message"></span>
                    </div>
                </div>
                <div class="form-group">
                    <div class="track-label">
                        <label for="track_file">Трек</label>
                    </div>
                    <div class="file-input-container">
                        <label for="track_file" class="file-upload-btn">Выберите файл</label>
                        <input type="file" name="track_file" id="track_file" accept="audio/*" required>
                        <span id="file-name" class="file-name">Файл не выбран</span>
                    </div>
                    <span id="track_file_error" class="error-message"></span>
                </div>
                <div class="form-group">
                    <div class="image-label">
                        <label for="image_file">Логотип трека</label>
                    </div>
                    <div class="image-input-container">
                        <label for="image_file" class="image-upload-btn">Выберите файл</label>
                        <input type="file" name="image_file" id="image_file" accept="image/*" required>
                        <span id="image-name" class="file-name">Файл не выбран</span>
                    </div>
                    <span id="image_file_error" class="error-message"></span>
                </div>

                <button type="button" class="button-add-new-track" onclick="uploadTrack()">Добавить трек</button>
            </form>
                <div class="label">Ваши добавленные треки</div>
                <div class="main-genre-music">
                <p class="noTracksMessage"id="noTracksMessage" style="<?php echo (!empty($userTracks) ? 'display: none;' : ''); ?>">
                    Вы пока не добавили ни одного трека
                </p>
                    <?php if(!empty($userTracks)): ?>
                        <?php foreach ($userTracks as $track): ?>
                            <div class="container-sound" id="track-<?php echo ($track['id']); ?>">
                                <div class="pause-button" style="display:none;">
                                    <div class="rectangle"></div>
                                    <div class="rectangle"></div>
                                </div>
                                <div class="play-button">
                                    <div class="triangle"></div>
                                </div>
                                <img src="<?php echo htmlspecialchars($track['image_url']); ?>" alt="Пример изображения" class="image-sound">
                                <div class="all-duration">
                                    <div class="container-duration">
                                        <span class="time current-time">0:00</span>
                                        <div class="main-pain">
                                            <div class="text"><?php echo htmlspecialchars($track['artist']) . ' - ' . htmlspecialchars($track['track_name']); ?></div>
                                            <audio class="audio-player" src="<?php echo htmlspecialchars($track['track_url']); ?>" preload="metadata"></audio>
                                            <div class="progress-container">
                                                <div class="progress-bar"></div>
                                                <div class="progress-thumb"></div>
                                            </div>
                                        </div>
                                        <span class="time duration">0:00</span>
                                    </div>
                                </div>
                                <div class="button-container">
                                    <div class="login-required-popup like-required-popup" id="like-required-message-<?php echo ($track['id']); ?>" style="display: none;">
                                        Для лайка необходимо <a href="../../registration/login.php">войти</a> или <a href="../../registration/register.php">зарегистрироваться</a>
                                    </div>
                                    <form id="like-form-<?php echo $track['id']; ?>" data-track-id="<?php echo $track['id']; ?>">
                                        <input type="hidden" name="user_id" value="<?php echo isset($_SESSION['id']) ? htmlspecialchars($_SESSION['id']) : ''; ?>">
                                        <input type="hidden" name="track_id" value="<?php echo htmlspecialchars($track['id']); ?>">
                                        <input type="hidden" name="current_page" value="<?php echo htmlspecialchars($_SERVER['REQUEST_URI']); ?>">
                                        <button type="button" class="heart-icon <?php echo in_array($track['id'], $liked_track_ids) ? 'liked' : ''; ?>"></button>
                                    </form>
                                    <div class="download-container" id="download-form-<?php echo $track['id']; ?>" data-track-id="<?php echo $track['id']; ?>">
                                        <button type="button" class="button-load <?php echo (!isset($_SESSION['loggedin']) || $_SESSION['loggedin'] !== true) ? 'login-required' : ''; ?>">
                                            <div class="arrow-down"></div>
                                            <div class="line"></div>
                                        </button>
                                        <div class="login-required-popup download-required-popup" id="download-required-message-<?php echo $track['id']; ?>">
                                            Для скачивания необходимо <a href="../../registration/login.php">войти</a> или <a href="../../registration/register.php">зарегистрироваться</a>
                                        </div>
                                    </div>
                                    <form id="delete-form-<?php echo $track['id']; ?>" class="delete-form" data-track-id="<?php echo $track['id']; ?>">
                                        <input type="hidden" name="user_id" value="<?php echo isset($_SESSION['id']) ? htmlspecialchars($_SESSION['id']) : ''; ?>">
                                        <input type="hidden" name="track_id" value="<?php echo htmlspecialchars($track['id']); ?>">
                                        <button type="button" class="trash-icon" title="Удалить трек"></button>
                                    </form>
                                </div>
                            </div>
                        <?php endforeach; ?>
                    <?php endif; ?>
                </div>
            <?php
            }
            ?>
    </div>
</div>

<footer>
    <div class="footer-container">
        <p>&copy; 2025 Merkury-Music. Все права защищены.</p>
        <div class="social-icons">
            <a href="https://t.me/Merkuryy111"><img src="Images/social/telegram.png"></a>
            <a href="https://vk.com/merkury_2006"><img src="Images/social/wk.png"></a>
            <a href="https://wa.me/79622371147"><img src="Images/social/whatsapp.png"></a>
            <a href="mailto:elez123456789@yandex.ru"><img src="Images/social/mail.png"></a>
        </div>
    </div>
    <div class="cookie-settings">
            <button id="enable-cookies">Включить Cookies</button>
            <button id="disable-cookies">Выключить Cookies</button>
        </div>
</footer>
<script src="Scripts/process_upload.js"></script>
<script src="Scripts/showFiles.js"></script>
<script src="Scripts/cookies.js"></script>
</body>
</html>
