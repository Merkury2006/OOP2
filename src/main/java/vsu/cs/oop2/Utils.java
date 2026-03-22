package vsu.cs.oop2;

/**
 * УТИЛИТАРНЫЙ КЛАСС С ВСПОМОГАТЕЛЬНЫМИ МЕТОДАМИ
 *
 * Содержит статические методы общего назначения, используемые в разных частях приложения.
 *
 * Особенности:
 * - Все методы статические (не требуется создание экземпляра)
 * - Нет состояния (stateless)
 *
 * @see vsu.cs.oop2.Controllers.RegistrationController
 * @see vsu.cs.oop2.Controllers.PasswordResetController
 */
public class Utils {

    /**
     * ПОЛУЧИТЬ URL ПОЧТОВОГО СЕРВИСА ПО EMAIL АДРЕСУ
     *
     * Определяет популярный почтовый сервис на основе домена email
     * и возвращает URL для доступа к веб-интерфейсу почты.
     *
     * Поддерживаемые почтовые сервисы:
     * - Gmail: @gmail.com
     * - Яндекс.Почта: @yandex.*, @ya.ru
     * - Mail.ru: @mail.ru, @inbox.ru, @list.ru, @bk.ru
     * - Outlook/Hotmail: @outlook.com, @hotmail.com, @live.com
     * - Yahoo: @yahoo.com
     *
     * @param email Email адрес пользователя
     * @return URL для доступа к веб-интерфейсу почтового сервиса
     * @see #getMailServiceName(String)
     */
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


    /**
     * ПОЛУЧИТЬ ЧЕЛОВЕКО-ЧИТАЕМОЕ ИМЯ ПОЧТОВОГО СЕРВИСА
     *
     * Определяет популярный почтовый сервис на основе домена email
     * и возвращает локализованное название сервиса на русском языке.
     *
     * Поддерживаемые почтовые сервисы (русские названия):
     * - Gmail: "Gmail"
     * - Яндекс.Почта: "Яндекс.Почту"
     * - Mail.ru: "Mail.ru"
     * - Outlook/Hotmail: "Outlook"
     * - Yahoo: "Yahoo"
     *
     * @param email Email адрес пользователя
     * @return Локализованное название почтового сервиса
     * @see #getMailServiceUrl(String)
     */
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
