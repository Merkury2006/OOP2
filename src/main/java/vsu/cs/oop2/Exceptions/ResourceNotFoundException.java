package vsu.cs.oop2.Exceptions;

import lombok.Getter;

/**
 * ИСКЛЮЧЕНИЕ ДЛЯ СИТУАЦИЙ "РЕСУРС НЕ НАЙДЕН" (404)
 *
 * Используется когда запрашиваемый ресурс (трек, изображение и т.д.)
 * не существует в системе. Соответствует HTTP статусу 404.
 *
 * Содержит дополнительную информацию о типе ресурса и идентификаторе
 * для более детализированного логирования и отладки.
 *
 * Примеры использования:
 * - Трек с ID 123 не найден
 * - Плейлист с ID 456 не найден
 *
 * @apiNote Соответствует HTTP 404 Not Found
 * @see vsu.cs.oop2.Services.TrackService#getTrackById(Long)
 */
@Getter
public class ResourceNotFoundException extends AppException{
    /**ТИП РЕСУРСА КОТОРЫЙ НЕ НАЙДЕН*/
    private final String resourceType;

    /** ИДЕНТИФИКАТОР РЕСУРСА КОТОРЫЙ НЕ НАЙДЕН*/
    private final Long identifier;


    /**
     * СОЗДАНИЕ ИСКЛЮЧЕНИЯ ДЛЯ РЕСУРСА
     *
     * @param resourceType Тип ресурса (например, "Трек")
     * @param identifier Идентификатор ресурса (например, 123L)
     */
    public ResourceNotFoundException(String resourceType, Long identifier) {
        super(resourceType + " с ID " + identifier + " не найден");
        this.resourceType = resourceType;
        this.identifier = identifier;
    }

}
