<?php
session_start();
include '../../database/db_connect.php';

header('Content-Type: application/json');

if (!isset($_SESSION['id'])) {
    die(json_encode(['success' => false, 'message' => 'Требуется авторизация']));
}

if (!isset($_POST['user_id']) || !isset($_POST['track_id'])) {
    die(json_encode(['success' => false, 'message' => 'Не указаны параметры']));
}

$user_id = intval($_POST['user_id']);
$track_id = intval($_POST['track_id']);

// Получаем полную информацию о треке
$stmt = $conn->prepare("SELECT user_id_add, track_url, image_url FROM tracks WHERE id = ?");
$stmt->bind_param("i", $track_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    die(json_encode(['success' => false, 'message' => 'Трек не найден']));
}

$track = $result->fetch_assoc();

if ($track['user_id_add'] != $user_id) {
    die(json_encode(['success' => false, 'message' => 'Нет прав на удаление']));
}

// Удаляем лайки трека
$delete_likes_stmt = $conn->prepare("DELETE FROM likes WHERE track_id = ?");
$delete_likes_stmt->bind_param("i", $track_id);

if (!$delete_likes_stmt->execute()) {
    die(json_encode(['success' => false, 'message' => 'Ошибка при удалении лайков']));
}

// Удаление файлов с сервера
$deleted_files = [];
$errors = [];

// Функция для безопасного удаления файлов
function deleteFile($path, &$errors, &$deleted_files, $type) {
    if (!empty($path)) {
        $full_path = $_SERVER['DOCUMENT_ROOT'] . $path;
        if (file_exists($full_path)) {
            if (is_writable($full_path)) {
                if (!unlink($full_path)) {
                    $errors[] = "Не удалось удалить $type файл: $path";
                } else {
                    $deleted_files[] = "Успешно удален $type файл: $path";
                    // Пытаемся удалить пустую директорию
                    $dir = dirname($full_path);
                    if (is_dir($dir) && count(scandir($dir)) == 2) { // Директория пуста
                        rmdir($dir);
                    }
                }
            } else {
                $errors[] = "Нет прав на удаление $type файла: $path";
            }
        } else {
            $errors[] = "$type файл не найден: $path";
        }
    }
}

// Удаляем аудиофайл
deleteFile($track['track_url'], $errors, $deleted_files, 'аудио');

// Удаляем изображение
deleteFile($track['image_url'], $errors, $deleted_files, 'изображение');

// Удаляем запись о треке из базы данных
$delete_stmt = $conn->prepare("DELETE FROM tracks WHERE id = ?");
$delete_stmt->bind_param("i", $track_id);

if ($delete_stmt->execute()) {
    $response = [
        'success' => true,
        'message' => 'Трек успешно удален',
        'deleted_files' => $deleted_files
    ];
    
    if (!empty($errors)) {
        $response['warnings'] = $errors;
    }
    
    echo json_encode($response);
} else {
    echo json_encode([
        'success' => false, 
        'message' => 'Ошибка базы данных при удалении трека',
        'database_error' => $conn->error,
        'file_errors' => $errors
    ]);
}
?>