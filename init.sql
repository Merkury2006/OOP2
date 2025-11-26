CREATE TABLE IF NOT EXISTS userdata (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS tracks (
    id BIGSERIAL PRIMARY KEY,
    track_name VARCHAR(255) NOT NULL,
    artist VARCHAR(255),
    track_url VARCHAR(255),
    image_url VARCHAR(255),
    genre VARCHAR(255),
    user_id_add INTEGER NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS likes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    track_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES userdata(id) ON DELETE CASCADE,
    FOREIGN KEY (track_id) REFERENCES tracks(id) ON DELETE CASCADE,
    UNIQUE(user_id, track_id)
    );

INSERT INTO tracks (track_name, artist, track_url, image_url, genre, user_id_add) VALUES
    ('Худи', 'Artik & Asti, NILETTO, Джиган', '/static/Music/hudi.mp3', '/static/Images/hudi.jpeg', 'Поп музыка', 0),
    ('Асфальт', 'Jakone', '/static/Music/Asfalt.mp3', '/static/Images/asphalt.jpeg', 'Поп музыка', 0),
    ('Пожары', 'Xolidayboy', '/static/Music/pozhary.mp3', '/static/Images/pozhary.jpeg', 'Поп музыка', 0),
    ('New Devide', 'Linkin Park', '/static/Music/New Devide.mp3', '/static/Images/New Devide.jpeg', 'Рок музыка', 0),
    ('What I''ve done', 'Linkin park', '/static/Music/What I''ve done.mp3', '/static/Images/What I''ve done.jpeg', 'Рок музыка', 0),
    ('Faint', 'Linkin park', '/static/Music/Faint.mp3', '/static/Images/Faint.webp', 'Рок музыка', 0),
    ('Numb', 'Linkin park', '/static/Music/Numb.mp3', '/static/Images/Numb.webp', 'Рок музыка', 0),
    ('Papercut', 'Linkin park', '/static/Music/Papercut.mp3', '/static/Images/Papercut.webp', 'Рок музыка', 0),
    ('Laud', 'Boulevard Depo', '/static/Music/Laud.mp3', '/static/Images/Laud.webp', 'Рэп музыка', 0),
    ('География', 'Boulevard Depo', '/static/Music/Geografia.mp3', '/static/Images/geografia.jpeg', 'Рэп музыка', 0),
    ('5 минут назад', 'Pharaoh', '/static/Music/5minNazad.mp3', '/static/Images/5minNazad.webp', 'Рэп музыка', 0),
    ('Пломбир', 'Pharaoh', '/static/Music/plombir.mp3', '/static/Images/plombir.webp', 'Рэп музыка', 0),
    ('Это все дико, например', 'Pharaoh', '/static/Music/dikoNaprimer.mp3', '/static/Images/dikoNaprimer.jpg', 'Рэп музыка', 0),
    ('Umbrella', 'Rihanna', '/static/Music/umbrella.mp3', '/static/Images/umbrella.jpeg', 'Хип-хоп музыка', 0),
    ('Smack That', 'Akon', '/static/Music/smackThat.mp3', '/static/Images/smackThat.webp', 'Хип-хоп музыка', 0),
    ('Encore', 'Eminem, Dido', '/static/Music/encore.mp3', '/static/Images/encore.webp', 'Хип-хоп музыка', 0),
    ('Who Needs Forever', 'Loung Cafe', '/static/Music/whoNeedsForever.mp3', '/static/Images/whoNeedsForever.jpeg', 'Электронная музыка', 0),
    ('Boum Boum', 'Enigma', '/static/Music/boom.mp3', '/static/Images/boom.jpg', 'Электронная музыка', 0),
    ('Let Me Love You', 'DJ Snake, Justin Bieber', '/static/Music/let me love you.mp3', '/static/Images/let me love you.jpg', 'Электронная музыка', 0),
    ('Murder In My Mind', 'Kordnell', '/static/Music/murder in my mind.mp3', '/static/Images/murder in my mind.webp', 'Электронная музыка', 0),
    ('Classical Gas', 'Vanessa-Mae', '/static/Music/classicalGas.mp3', '/static/Images/classicalGas.webp', 'Классическая музыка', 0),
    ('Hello', 'Adele', '/static/Music/hello.mp3', '/static/Images/hello.webp', 'Классическая музыка', 0),
    ('Лунная соната', 'Бетховен', '/static/Music/sonata.mp3', '/static/Images/sonata.jpeg', 'Классическая музыка', 0),
    ('Now We Are Free', 'Hans Zimmer, Gavin Greenaway', '/static/Music/nowWeAreFree.mp3', '/static/Images/nowWeAreFree.webp', 'Классическая музыка', 0),
    ('BASSED FUNK', 'ФОНК, pHonk', '/static/Music/basedFunk.mp3', '/static/Images/basedFunk.webp', 'Фонк музыка', 0),
    ('Welcome To Moscow', 'EVEN CUTE, derzko69', '/static/Music/welcomeToMoscow.mp3', '/static/Images/welcomeToMoscow.webp', 'Фонк музыка', 0),
    ('EMPTY FUNK', 'DJ Anemia, Crier, sixnite', '/static/Music/emptyFunk.mp3', '/static/Images/emptyFonk.webp', 'Фонк музыка', 0),
    ('Закричу на весь мир', 'ТРАВМА', '/static/Music/mir.mp3', '/static/Images/mir.jpg', 'Фонк музыка', 0),
    ('So Nice', 'Loung Cafe', '/static/Music/nice.mp3', '/static/Images/nice.webp', 'Джазз музыка', 0),
    ('Jingle Bell Rock', 'Bobby Helms', '/static/Music/jingleBellRock.mp3', '/static/Images/jingleBellRock.webp', 'Джазз музыка', 0),
    ('All of Me', 'Frank Sinatra, Charles Aznavour', '/static/Music/allOfMe.mp3', '/static/Images/allOfMe.webp', 'Джазз музыка', 0),
    ('Петропавловск', 'Radio Tapok', '/static/Music/petropavlovsk.mp3', '/static/Images/petropavlovsk.jpeg', 'Металл музыка', 0),
    ('Feuer frei!', 'Rammstein', '/static/Music/feuer.mp3', '/static/Images/feuer.jpg', 'Металл музыка', 0)
    ON CONFLICT DO NOTHING;