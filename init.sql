-- Создание таблицы пользователей
CREATE TABLE userdata (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(255) NOT NULL,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          is_email_verified BOOLEAN NOT NULL DEFAULT false,
                          email_verification_token VARCHAR(255),
                          verification_token_expiry TIMESTAMP,
                          password_reset_token VARCHAR(255),
                          password_reset_expiry TIMESTAMP,
                          last_password_reset_request TIMESTAMP,
                          last_verification_sent TIMESTAMP,
                          user_role VARCHAR(50) NOT NULL DEFAULT 'USER'
);

-- Создание таблицы треков
CREATE TABLE tracks (
                        id BIGSERIAL PRIMARY KEY,
                        track_name VARCHAR(255) NOT NULL,
                        artist VARCHAR(255),
                        track_url VARCHAR(255),
                        image_url VARCHAR(255),
                        genre VARCHAR(255),
                        user_id_add BIGINT NOT NULL DEFAULT 0 REFERENCES userdata(id) ON DELETE CASCADE
);

-- Создание таблицы лайков
CREATE TABLE likes (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL REFERENCES userdata(id) ON DELETE CASCADE,
                       track_id BIGINT NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
                       UNIQUE(user_id, track_id)
);

-- Вставка администратора С РОЛЬЮ ADMIN
INSERT INTO userdata (username, email, password, is_email_verified, user_role)
VALUES (
           'Merkury',
           'elez4@yandex.ru',
           '$2a$12$YYKyAAx1Lj/o6QDGG9RyjO57wsklFMZrMrpO1VCx3twGSGTgBS5ma',
           true,
           'ADMIN'
       ) ON CONFLICT (email) DO UPDATE
    SET user_role = 'ADMIN'
WHERE userdata.email = 'elez4@yandex.ru';

-- Вставка треков
DO $$
    DECLARE
        admin_id BIGINT;
    BEGIN
        -- Получаем ID админа
        SELECT id INTO admin_id FROM userdata WHERE email = 'elez4@yandex.ru';

        -- Если админ не найден, создаем его и получаем ID
        IF admin_id IS NULL THEN
            INSERT INTO userdata (username, email, password, is_email_verified, user_role)
            VALUES (
                       'Merkury',
                       'elez4@yandex.ru',
                       '$2a$12$YYKyAAx1Lj/o6QDGG9RyjO57wsklFMZrMrpO1VCx3twGSGTgBS5ma',
                       true,
                       'ADMIN'
                   ) RETURNING id INTO admin_id;
        END IF;

        -- Вставляем треки с новыми путями
        INSERT INTO tracks (track_name, artist, track_url, image_url, genre, user_id_add) VALUES
                                                                                              -- Поп музыка
                                                                                              ('Худи', 'Artik & Asti, NILETTO, Джиган', '/Music/hudi.mp3', '/Images/hudi.jpeg', 'Поп музыка', admin_id),
                                                                                              ('Асфальт', 'Jakone', '/Music/Asfalt.mp3', '/Images/asphalt.jpeg', 'Поп музыка', admin_id),
                                                                                              ('Пожары', 'Xolidayboy', '/Music/pozhary.mp3', '/Images/pozhary.jpeg', 'Поп музыка', admin_id),

                                                                                              -- Рок музыка (Linkin Park)
                                                                                              ('New Devide', 'Linkin Park', '/Music/New Devide.mp3', '/Images/New Devide.jpeg', 'Рок музыка', admin_id),
                                                                                              ('What I''ve done', 'Linkin park', '/Music/What I''ve done.mp3', '/Images/What I''ve done.jpeg', 'Рок музыка', admin_id),
                                                                                              ('Faint', 'Linkin park', '/Music/Faint.mp3', '/Images/Faint.webp', 'Рок музыка', admin_id),
                                                                                              ('Numb', 'Linkin park', '/Music/Numb.mp3', '/Images/Numb.webp', 'Рок музыка', admin_id),
                                                                                              ('Papercut', 'Linkin park', '/Music/Papercut.mp3', '/Images/Papercut.webp', 'Рок музыка', admin_id),

                                                                                              -- Рэп музыка
                                                                                              ('Laud', 'Boulevard Depo', '/Music/Laud.mp3', '/Images/Laud.webp', 'Рэп музыка', admin_id),
                                                                                              ('География', 'Boulevard Depo', '/Music/Geografia.mp3', '/Images/geografia.jpeg', 'Рэп музыка', admin_id),
                                                                                              ('5 минут назад', 'Pharaoh', '/Music/5minNazad.mp3', '/Images/5minNazad.webp', 'Рэп музыка', admin_id),
                                                                                              ('Пломбир', 'Pharaoh', '/Music/plombir.mp3', '/Images/plombir.webp', 'Рэп музыка', admin_id),
                                                                                              ('Это все дико, например', 'Pharaoh', '/Music/dikoNaprimer.mp3', '/Images/dikoNaprimer.jpg', 'Рэп музыка', admin_id),

                                                                                              -- Хип-хоп музыка
                                                                                              ('Umbrella', 'Rihanna', '/Music/umbrella.mp3', '/Images/umbrella.jpeg', 'Хип-хоп музыка', admin_id),
                                                                                              ('Smack That', 'Akon', '/Music/smackThat.mp3', '/Images/smackThat.webp', 'Хип-хоп музыка', admin_id),
                                                                                              ('Encore', 'Eminem, Dido', '/Music/encore.mp3', '/Images/encore.webp', 'Хип-хоп музыка', admin_id),

                                                                                              -- Электронная музыка
                                                                                              ('Who Needs Forever', 'Loung Cafe', '/Music/whoNeedsForever.mp3', '/Images/whoNeedsForever.jpeg', 'Электронная музыка', admin_id),
                                                                                              ('Boum Boum', 'Enigma', '/Music/boom.mp3', '/Images/boom.jpg', 'Электронная музыка', admin_id),
                                                                                              ('Let Me Love You', 'DJ Snake, Justin Bieber', '/Music/let me love you.mp3', '/Images/let me love you.jpg', 'Электронная музыка', admin_id),
                                                                                              ('Murder In My Mind', 'Kordnell', '/Music/murder in my mind.mp3', '/Images/murder in my mind.webp', 'Электронная музыка', admin_id),

                                                                                              -- Классическая музыка
                                                                                              ('Classical Gas', 'Vanessa-Mae', '/Music/classicalGas.mp3', '/Images/classicalGas.webp', 'Классическая музыка', admin_id),
                                                                                              ('Hello', 'Adele', '/Music/hello.mp3', '/Images/hello.webp', 'Классическая музыка', admin_id),
                                                                                              ('Лунная соната', 'Бетховен', '/Music/sonata.mp3', '/Images/sonata.jpeg', 'Классическая музыка', admin_id),
                                                                                              ('Now We Are Free', 'Hans Zimmer, Gavin Greenaway', '/Music/nowWeAreFree.mp3', '/Images/nowWeAreFree.webp', 'Классическая музыка', admin_id),

                                                                                              -- Фонк музыка
                                                                                              ('BASSED FUNK', 'ФОНК, pHonk', '/Music/basedFunk.mp3', '/Images/basedFunk.webp', 'Фонк музыка', admin_id),
                                                                                              ('Welcome To Moscow', 'EVEN CUTE, derzko69', '/Music/welcomeToMoscow.mp3', '/Images/welcomeToMoscow.webp', 'Фонк музыка', admin_id),
                                                                                              ('EMPTY FUNK', 'DJ Anemia, Crier, sixnite', '/Music/emptyFunk.mp3', '/Images/emptyFonk.webp', 'Фонк музыка', admin_id),
                                                                                              ('Закричу на весь мир', 'ТРАВМА', '/Music/mir.mp3', '/Images/mir.jpg', 'Фонк музыка', admin_id),

                                                                                              -- Джазз музыка
                                                                                              ('So Nice', 'Loung Cafe', '/Music/nice.mp3', '/Images/nice.webp', 'Джазз музыка', admin_id),
                                                                                              ('Jingle Bell Rock', 'Bobby Helms', '/Music/jingleBellRock.mp3', '/Images/jingleBellRock.webp', 'Джазз музыка', admin_id),
                                                                                              ('All of Me', 'Frank Sinatra, Charles Aznavour', '/Music/allOfMe.mp3', '/Images/allOfMe.webp', 'Джазз музыка', admin_id),

                                                                                              -- Металл музыка
                                                                                              ('Петропавловск', 'Radio Tapok', '/Music/petropavlovsk.mp3', '/Images/petropavlovsk.jpeg', 'Металл музыка', admin_id),
                                                                                              ('Feuer frei!', 'Rammstein', '/Music/feuer.mp3', '/Images/feuer.jpg', 'Металл музыка', admin_id)
        ON CONFLICT DO NOTHING;
    END $$;