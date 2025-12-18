CREATE TABLE userdata (
      id BIGSERIAL PRIMARY KEY,
      username VARCHAR(255) NOT NULL,
      email VARCHAR(255) UNIQUE NOT NULL,
      password VARCHAR(255) NOT NULL,
      is_email_verified  BOOLEAN NOT NULL DEFAULT false,
      email_verification_token VARCHAR(255),
      verification_token_expiry TIMESTAMP
);

CREATE TABLE tracks (
    id BIGSERIAL PRIMARY KEY,
    track_name VARCHAR(255) NOT NULL,
    artist VARCHAR(255),
    track_url VARCHAR(255),
    image_url VARCHAR(255),
    genre VARCHAR(255),
    user_id_add BIGINT NOT NULL DEFAULT 0 REFERENCES userdata(id) ON DELETE CASCADE
);

CREATE TABLE likes (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL REFERENCES userdata(id) ON DELETE CASCADE,
                       track_id BIGINT NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
                       UNIQUE(user_id, track_id)
);

INSERT INTO userdata (username, email, password, is_email_verified)
VALUES (
           'Merkury',
           'elez4@yandex.ru',
           '$2a$12$YYKyAAx1Lj/o6QDGG9RyjO57wsklFMZrMrpO1VCx3twGSGTgBS5ma',
           true
       ) ON CONFLICT (email) DO NOTHING;

DO $$
    DECLARE
        admin_id BIGINT;
    BEGIN
        -- Получаем ID админа
        SELECT id INTO admin_id FROM userdata WHERE email = 'elez4@yandex.ru';

        -- Вставляем треки
        INSERT INTO tracks (track_name, artist, track_url, image_url, genre, user_id_add) VALUES
          ('Худи', 'Artik & Asti, NILETTO, Джиган', '/static/Music/hudi.mp3', '/static/Images/hudi.jpeg', 'Поп музыка', admin_id),
          ('Асфальт', 'Jakone', '/static/Music/Asfalt.mp3', '/static/Images/asphalt.jpeg', 'Поп музыка', admin_id),
          ('Пожары', 'Xolidayboy', '/static/Music/pozhary.mp3', '/static/Images/pozhary.jpeg', 'Поп музыка', admin_id),
          ('New Devide', 'Linkin Park', '/static/Music/New Devide.mp3', '/static/Images/New Devide.jpeg', 'Рок музыка', admin_id),
          ('What I''ve done', 'Linkin park', '/static/Music/What I''ve done.mp3', '/static/Images/What I''ve done.jpeg', 'Рок музыка', admin_id),
          ('Faint', 'Linkin park', '/static/Music/Faint.mp3', '/static/Images/Faint.webp', 'Рок музыка', admin_id),
          ('Numb', 'Linkin park', '/static/Music/Numb.mp3', '/static/Images/Numb.webp', 'Рок музыка', admin_id),
          ('Papercut', 'Linkin park', '/static/Music/Papercut.mp3', '/static/Images/Papercut.webp', 'Рок музыка', admin_id),
          ('Laud', 'Boulevard Depo', '/static/Music/Laud.mp3', '/static/Images/Laud.webp', 'Рэп музыка', admin_id),
          ('География', 'Boulevard Depo', '/static/Music/Geografia.mp3', '/static/Images/geografia.jpeg', 'Рэп музыка', admin_id),
          ('5 минут назад', 'Pharaoh', '/static/Music/5minNazad.mp3', '/static/Images/5minNazad.webp', 'Рэп музыка', admin_id),
          ('Пломбир', 'Pharaoh', '/static/Music/plombir.mp3', '/static/Images/plombir.webp', 'Рэп музыка', admin_id),
          ('Это все дико, например', 'Pharaoh', '/static/Music/dikoNaprimer.mp3', '/static/Images/dikoNaprimer.jpg', 'Рэп музыка', admin_id),
          ('Umbrella', 'Rihanna', '/static/Music/umbrella.mp3', '/static/Images/umbrella.jpeg', 'Хип-хоп музыка', admin_id),
          ('Smack That', 'Akon', '/static/Music/smackThat.mp3', '/static/Images/smackThat.webp', 'Хип-хоп музыка', admin_id),
          ('Encore', 'Eminem, Dido', '/static/Music/encore.mp3', '/static/Images/encore.webp', 'Хип-хоп музыка', admin_id),
          ('Who Needs Forever', 'Loung Cafe', '/static/Music/whoNeedsForever.mp3', '/static/Images/whoNeedsForever.jpeg', 'Электронная музыка', admin_id),
          ('Boum Boum', 'Enigma', '/static/Music/boom.mp3', '/static/Images/boom.jpg', 'Электронная музыка', admin_id),
          ('Let Me Love You', 'DJ Snake, Justin Bieber', '/static/Music/let me love you.mp3', '/static/Images/let me love you.jpg', 'Электронная музыка', admin_id),
          ('Murder In My Mind', 'Kordnell', '/static/Music/murder in my mind.mp3', '/static/Images/murder in my mind.webp', 'Электронная музыка', admin_id),
          ('Classical Gas', 'Vanessa-Mae', '/static/Music/classicalGas.mp3', '/static/Images/classicalGas.webp', 'Классическая музыка', admin_id),
          ('Hello', 'Adele', '/static/Music/hello.mp3', '/static/Images/hello.webp', 'Классическая музыка', admin_id),
          ('Лунная соната', 'Бетховен', '/static/Music/sonata.mp3', '/static/Images/sonata.jpeg', 'Классическая музыка', admin_id),
          ('Now We Are Free', 'Hans Zimmer, Gavin Greenaway', '/static/Music/nowWeAreFree.mp3', '/static/Images/nowWeAreFree.webp', 'Классическая музыка', admin_id),
          ('BASSED FUNK', 'ФОНК, pHonk', '/static/Music/basedFunk.mp3', '/static/Images/basedFunk.webp', 'Фонк музыка', admin_id),
          ('Welcome To Moscow', 'EVEN CUTE, derzko69', '/static/Music/welcomeToMoscow.mp3', '/static/Images/welcomeToMoscow.webp', 'Фонк музыка', admin_id),
          ('EMPTY FUNK', 'DJ Anemia, Crier, sixnite', '/static/Music/emptyFunk.mp3', '/static/Images/emptyFonk.webp', 'Фонк музыка', admin_id),
          ('Закричу на весь мир', 'ТРАВМА', '/static/Music/mir.mp3', '/static/Images/mir.jpg', 'Фонк музыка', admin_id),
          ('So Nice', 'Loung Cafe', '/static/Music/nice.mp3', '/static/Images/nice.webp', 'Джазз музыка', admin_id),
          ('Jingle Bell Rock', 'Bobby Helms', '/static/Music/jingleBellRock.mp3', '/static/Images/jingleBellRock.webp', 'Джазз музыка', admin_id),
          ('All of Me', 'Frank Sinatra, Charles Aznavour', '/static/Music/allOfMe.mp3', '/static/Images/allOfMe.webp', 'Джазз музыка', admin_id),
          ('Петропавловск', 'Radio Tapok', '/static/Music/petropavlovsk.mp3', '/static/Images/petropavlovsk.jpeg', 'Металл музыка', admin_id),
          ('Feuer frei!', 'Rammstein', '/static/Music/feuer.mp3', '/static/Images/feuer.jpg', 'Металл музыка', admin_id)
        ON CONFLICT DO NOTHING;

    END $$;