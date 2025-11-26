<?php
session_start();
include '../database/db_connect.php';
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Моя музыка</title>
    <link rel="icon" href="/static/favicon.ico" type="image/x-icon">
    <link rel="icon" href="/static/favicon-32x32.png" type="image/png" sizes="32x32">
    <link rel="icon" href="/static/favicon-16x16.png" type="image/png" sizes="16x16">
    <link rel="apple-touch-icon" href="/static/apple-touch-icon.png">
    <link rel="stylesheet" href="./Styles/stylesPage.css">
    <link rel="stylesheet" href="./Styles/styleIcons.css">
    <link rel="stylesheet" href="./Styles/styleGenreMusic.css">
    <link rel="stylesheet" href="./Styles/fonts.css">
</head>
<body>
<header>
    <div class="head-items">
        <img class="logo-site" src="Images/logo-site.png">
        <a href="../index.php" class="head-item">Главная</a>
        <a href="My-music.php" class="head-item-active">Моя музыка</a>
        <a href = "AboutUs.php" class="head-item">О нас</a>
        <a href = "upload_track.php" class="head-item">Добавить музыку</a>
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

    <div class="my-music-container">
        <?php
        if (isset($_SESSION['loggedin']) && $_SESSION['loggedin'] === true) {
            $user_id = $_SESSION['id'];
            $sql = "SELECT tracks.id, tracks.track_name, tracks.artist, tracks.track_url, tracks.image_url
                    FROM tracks
                    INNER JOIN likes ON tracks.id = likes.track_id
                    WHERE likes.user_id = ?";

            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $user_id);
            $stmt->execute();
            $result = $stmt->get_result();

            $liked_track_ids = array();
            $noTracksMessageClass = 'not-logged-in-message no-tracks-message';
            if ($result->num_rows > 0) {
                $noTracksMessageClass .= ' hidden';
            }
            ?>
            <div class="<?php echo $noTracksMessageClass; ?>">
                <p>У вас нет лайкнутых треков</p>
            </div>
            <?php

            if ($result->num_rows > 0) {
                while ($track = $result->fetch_assoc()) {
                    $liked_track_ids[] = $track['id'];
                    ?>
                    <div class="container-sound" id="track-<?php echo $track['id']; ?>">
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
                                <span class = "time duration">0:00</span>
                            </div>
                        </div>
                        <div class="button-container">
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
                        </div>
                    </div>
                    <?php
                }
            $stmt->close();
            }
        } else {
            ?>
            <div class='not-logged-in-message'>
                <p>Чтобы добавлять треки в свою музыку, вам нужно <a href='../registration/login.php'>войти</a> или <a href='../registration/register.php'>зарегистрироваться</a></p>
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
<script src="Scripts/like_track.js"></script>
<script src="Scripts/play_track.js"></script>
<script src="Scripts/download_track.js"></script>
<script src="Scripts/cookies.js"></script>
</body>
</html>
