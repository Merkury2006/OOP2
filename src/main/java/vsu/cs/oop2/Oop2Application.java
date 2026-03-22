package vsu.cs.oop2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ГЛАВНЫЙ КЛАСС ПРИЛОЖЕНИЯ SPRING BOOT
 *
 * Точка входа в приложение. Запускает Spring Boot приложение с автоматической
 * конфигурацией и компонентным сканированием.
 *
 * Аннотация @SpringBootApplication объединяет три основные аннотации:
 * 1. @Configuration - класс содержит конфигурацию Spring Beans
 * 2. @ComponentScan - автоматическое сканирование компонентов в текущем пакете и подпакетах
 * 3. @EnableAutoConfiguration - автоматическая настройка Spring на основе зависимостей в classpath
 *
 * Структура приложения:
 * - Контроллеры: vsu.cs.oop2.Controllers.*
 * - Сервисы: vsu.cs.oop2.Services.*
 * - Репозитории: vsu.cs.oop2.Repository.*
 * - Сущности: vsu.cs.oop2.Entity.*
 * - DTO: vsu.cs.oop2.DTO.*
 * - Конфигурация: vsu.cs.oop2.Сonfig.*
 * - Ошибки: vsu.cs.opp2.Exceptions.*
 *
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication
public class Oop2Application {


    /**
     * ОСНОВНОЙ МЕТОД ДЛЯ ЗАПУСКА ПРИЛОЖЕНИЯ
     * Точка входа в приложение. Инициализирует и запускает Spring Boot приложение.
     */
    public static void main(String[] args) {
        SpringApplication.run(Oop2Application.class, args);
    }

}
