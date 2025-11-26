<?php
session_start();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>О нас</title>
    <link rel="icon" href="/static/favicon.ico" type="image/x-icon">
    <link rel="icon" href="/static/favicon-32x32.png" type="image/png" sizes="32x32">
    <link rel="icon" href="/static/favicon-16x16.png" type="image/png" sizes="16x16">
    <link rel="stylesheet" href="./Styles/stylesPage.css">
    <link rel="stylesheet" href="./Styles/styleIcons.css">
    <link rel="stylesheet" href="./Styles/aboutUs.css">
    <link rel="stylesheet" href="./Styles/fonts.css">
</head>
<body>

<header>
    <div class="head-items">
        <img class="logo-site" src="Images/logo-site.png">
        <a href="../index.php" class="head-item">Главная</a>
        <a href="My-music.php" class="head-item">Моя музыка</a>
        <a href = "AboutUs.php" class="head-item-active">О нас</a>
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
    <div class="main">
        <div class="container-us">
            <img src="Images/social/Me.jpg" alt="Фото автора" class="author-photo">
            <div class="text-us">Здравствуйте! Меня зовут Рома Меркулов, и я автор этого сайта. Я увлекаюсь программированием и спортом, и моя цель — делиться знаниями и опытом с вами.
                На этом сайте вы можете насладиться музыкой на Ваш вкус.
                Я всегда открыт для общения, буду рад помочь!
            </div>
            <div class="contact">Связаться со мной:</div>
            <div class="contacts">
                <a class="link-us" href="https://t.me/Merkuryy111">Telegram: https://t.me/Merkuryy111</a>
                <a class="link-us"href="https://vk.com/merkury_2006">VK: https://vk.com/merkury_2006</a>
                <a class="link-us" href="https://wa.me/79622371147">WhatsApp: +7 (962) 237-11-47</a>
                <a class="link-us" href="mailto:elez123456789@yandex.ru">Почта: elez123456789@yandex.ru</a>
            </div>
        </div>        
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

</body>
<script src="Scripts/cookies.js"></script>
</html>
