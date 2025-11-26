<?php
session_start();
include '../../database/db_connect.php';

if (!isset($_SESSION['loggedin']) || $_SESSION['loggedin'] !== true) {
    die("login_required");
}

if (!isset($_POST['track_id']) || empty($_POST['track_id'])) {
    die("Не указан ID трека.");
}

$track_id = intval($_POST['track_id']);

$stmt = $conn->prepare("SELECT track_name, track_url FROM tracks WHERE id = ?");
$stmt->bind_param("i", $track_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    die("Трек не найден.");
}

$track = $result->fetch_assoc();
$file_path = $_SERVER['DOCUMENT_ROOT'] . $track['track_url'];

if (!file_exists($file_path)) {
    die("Файл трека не найден.");
}

header('Content-Description: File Transfer');
header('Content-Type: application/octet-stream');
header('Content-Disposition: attachment; filename="' . 
       preg_replace('/[^a-zA-Z0-9_\-\.]/', '_', $track['track_name']) . '.mp3');
header('Content-Length: ' . filesize($file_path));
header('Expires: 0');
header('Cache-Control: must-revalidate');
header('Pragma: public');

readfile($file_path);
exit;
?>