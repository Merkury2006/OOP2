<?php
session_start();
include '../../database/db_connect.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $response = array();


    if (!isset($_SESSION['loggedin']) || $_SESSION['loggedin'] !== true) {
        $response['redirect'] = true;
        echo json_encode($response);
        exit();
    }

    $user_id = $_POST['user_id'];
    $track_id = $_POST['track_id'];

    $sql_check_user = "SELECT id FROM userdata WHERE id = ?";
    $stmt_check_user = $conn->prepare($sql_check_user);
    $stmt_check_user->bind_param("i", $user_id);
    $stmt_check_user->execute();
    $stmt_check_user->store_result();

    if ($stmt_check_user->num_rows == 0) {
        $response['error'] = "Error: User with ID " . $user_id . " does not exist.";
        echo json_encode($response);
        exit();
    }
    $stmt_check_user->close();

    $sql_check_track = "SELECT id FROM tracks WHERE id = ?";
    $stmt_check_track = $conn->prepare($sql_check_track);
    $stmt_check_track->bind_param("i", $track_id);
    $stmt_check_track->execute();
    $stmt_check_track->store_result();

    if ($stmt_check_track->num_rows == 0) {
        $response['error'] = "Error: Track with ID " . $track_id . " does not exist.";
        echo json_encode($response);
        exit();
    }
    $stmt_check_track->close();

    $sql = "SELECT id FROM likes WHERE user_id = ? AND track_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ii", $user_id, $track_id);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $sql_delete = "DELETE FROM likes WHERE user_id = ? AND track_id = ?";
        $stmt_delete = $conn->prepare($sql_delete);
        $stmt_delete->bind_param("ii", $user_id, $track_id);

        if ($stmt_delete->execute()) {
            $response['status'] = "unliked";
        } else {
            $response['error'] = "Error unliking track: " . $stmt_delete->error;
        }
        $stmt_delete->close();
    } else {
        $sql_insert = "INSERT INTO likes (user_id, track_id) VALUES (?, ?)";
        $stmt_insert = $conn->prepare($sql_insert);
        $stmt_insert->bind_param("ii", $user_id, $track_id);

        if ($stmt_insert->execute()) {
            $response['status'] = "liked";
        } else {
            $response['error'] = "Error liking track: " . $stmt_insert->error;
        }
        $stmt_insert->close();
    }

    $stmt->close();
    $conn->close();

    echo json_encode($response);
} else {
    $response = array('redirect' => true);
    echo json_encode($response);
    exit();
}

exit();
?>