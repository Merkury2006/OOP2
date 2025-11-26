<?php
session_start();
include '../../database/db_connect.php';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Рок-музыка</title>
    <link rel="icon" href="/static/favicon.ico" type="image/x-icon">
    <link rel="icon" href="/static/favicon-32x32.png" type="image/png" sizes="32x32">
    <link rel="icon" href="/static/favicon-16x16.png" type="image/png" sizes="16x16">
    <link rel="stylesheet" href="../Styles/stylesPage.css">
    <link rel="stylesheet" href="../Styles/styleIcons.css">
    <link rel="stylesheet" href="../Styles/styleGenreMusic.css">
    <link rel="stylesheet" href="../Styles/fonts.css">
</head>
<body>

<header>
    <div class="head-items">
        <img class="logo-site" src="../Images/logo-site.png">
        <a href="../../index.php" class="head-item">Главная</a>
        <a href="../My-music.php" class="head-item">Моя музыка</a>
        <a href = "../AboutUs.php" class="head-item">О нас</a>
        <a href = "../upload_track.php" class="head-item">Добавить музыку</a>
    </div>
    <div class="theme-switch">
        <img src="../Images/social/lightTheme.png" id="light-theme-button" class="active">
        <img src="../Images/social/darkTheme.png" id="dark-theme-button">
    </div>
    <div class="user-info <?php echo isset($_SESSION['loggedin']) && $_SESSION['loggedin'] === true ? 'logged-in' : ''; ?>" id="user-info">
        <?php if (isset($_SESSION['loggedin']) && $_SESSION['loggedin'] === true): ?>
            <div class="welcome-message" id="welcome-message">
                <span id="user-nickname"><?php echo htmlspecialchars($_SESSION['username']); ?></span>
                <a href="../../registration/logout.php" class="link" id="logout-link">Выход</a>
            </div>
        <?php else: ?>
            <div class="registration-links" id="registration-links">
                <a href="../../registration/register.php" class="registration-item">Регистрация</a>
                <a href="../../registration/login.php" class="login-item">Вход</a>
            </div>
        <?php endif; ?>
    </div>
</header>
<div class="container">
    <div class="sidebar-left">
        <a class="sidebar-item" href="Pop-music.php">Поп-музыка</a>
        <a class="sidebar-item-active" href="Rock-music.php">Рок-музыка</a>
        <a class="sidebar-item" href="Rep-music.php">Рэп-музыка</a>
        <a class="sidebar-item" href="Hip-hop-music.php">Хип-хоп</a>
        <a class="sidebar-item" href="Electronic-music.php">Электронная музыка</a>
        <a class="sidebar-item" href="Classic-music.php">Классика</a>
        <a class="sidebar-item" href="Fonk-music.php">Фонк</a>
        <a class="sidebar-item" href="Jazz-music.php">Джаз</a>
        <a class="sidebar-item" href="Metallic-music.php">Металл</a>
    </div>
    <div class="main-genre-music">
        <?php
            if (isset($_SESSION['loggedin']) && $_SESSION['loggedin'] === true) {
                $user_id = $_SESSION['id'];
            
                $sql_liked = "SELECT track_id FROM likes WHERE user_id = ?";
                $stmt_liked = $conn->prepare($sql_liked);
                $stmt_liked->bind_param("i", $user_id);
                $stmt_liked->execute();
                $result_liked = $stmt_liked->get_result();
            
                $liked_track_ids = array();
                while ($row_liked = $result_liked->fetch_assoc()) {
                    $liked_track_ids[] = $row_liked['track_id'];
                }
            
                $stmt_liked->close();
            } else {
                $liked_track_ids = array();
                $user_id = 0;
            }
            $sql = "SELECT id, track_name, artist, track_url, image_url FROM tracks WHERE genre = ?";
            $stmt = $conn->prepare($sql);
            $genre = 'Рок музыка';
            $stmt->bind_param("s", $genre);
            $stmt->execute();
            $result = $stmt->get_result();
            if ($result->num_rows > 0) {
                while ($track = $result->fetch_assoc()) {
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
                            <div class="like-container" id="like-form-<?php echo $track['id']; ?>" data-track-id="<?php echo $track['id']; ?>">
                                <button type="button" class="heart-icon <?php echo in_array($track['id'], $liked_track_ids) ? 'liked' : ''; ?> <?php echo ($user_id == 0) ? 'login-required' : ''; ?>"></button>
                                <div class="login-required-popup like-required-popup" id="like-required-message-<?php echo $track['id']; ?>">
                                    Для лайка необходимо <a href="../../registration/login.php">войти</a> или <a href="../../registration/register.php">зарегистрироваться</a>
                                </div>
                                <input type="hidden" name="user_id" value="<?php echo isset($_SESSION['id']) ? htmlspecialchars($_SESSION['id']) : ''; ?>">
                                <input type="hidden" name="track_id" value="<?php echo htmlspecialchars($track['id']); ?>">
                                <input type="hidden" name="current_page" value="<?php echo htmlspecialchars($_SERVER['REQUEST_URI']); ?>">
                            </div>
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
            } else {
                echo "<p>No tracks found.</p>";
            }

            $stmt->close();
            $conn->close();
        ?>
    </div>
</div>
<footer>
    <div class="footer-container">
        <p>&copy; 2025 Merkury-Music. Все права защищены.</p>
        <div class="social-icons">
            <a href="https://t.me/Merkuryy111"><img src="../Images/social/telegram.png"></a>
            <a href="https://vk.com/merkury_2006"><img src="../Images/social/wk.png"></a>
            <a href="https://wa.me/79622371147"><img src="../Images/social/whatsapp.png"></a>
            <a href="mailto:elez123456789@yandex.ru"><img src="../Images/social/mail.png"></a>
        </div>
    </div>
    <div class="cookie-settings">
            <button id="enable-cookies">Включить Cookies</button>
            <button id="disable-cookies">Выключить Cookies</button>
    </div>
</footer>

<script src="../Scripts/like_track.js"></script>
<script src="../Scripts/play_track.js"></script>
<script src="../Scripts/download_track.js"></script>
<script src="../Scripts/cookies.js"></script>
</body>
</html>
