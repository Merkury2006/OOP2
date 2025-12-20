package vsu.cs.oop2;

public class Utils {
    public static String getMailServiceUrl(String email) {
        if (email.contains("@gmail.com")) return "https://mail.google.com";
        if (email.contains("@yandex.") || email.contains("@ya.ru")) return "https://mail.yandex.ru";
        if (email.contains("@mail.ru") || email.contains("@inbox.ru") || email.contains("@list.ru") || email.contains("@bk.ru"))
            return "https://mail.ru";
        if (email.contains("@outlook.com") || email.contains("@hotmail.com") || email.contains("@live.com"))
            return "https://outlook.live.com";
        if (email.contains("@yahoo.com")) return "https://mail.yahoo.com";
        return "https://mail.google.com";
    }

    public static String getMailServiceName(String email) {
        if (email.contains("@gmail.com")) return "Gmail";
        if (email.contains("@yandex.") || email.contains("@ya.ru")) return "Яндекс.Почту";
        if (email.contains("@mail.ru") || email.contains("@inbox.ru") || email.contains("@list.ru") || email.contains("@bk.ru"))
            return "Mail.ru";
        if (email.contains("@outlook.com") || email.contains("@hotmail.com") || email.contains("@live.com"))
            return "Outlook";
        if (email.contains("@yahoo.com")) return "Yahoo";
        return "почту";
    }
}
