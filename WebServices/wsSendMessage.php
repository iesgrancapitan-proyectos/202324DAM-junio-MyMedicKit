<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

header('Content-Type: application/json');

$json = file_get_contents('php://input');
$params = json_decode($json, true);

if (isset($params["user_id"]) && isset($params["message"]) && isset($params["email"])) {
    $user_id = $params['user_id'];
    $message = $params['message'];
    $email = $params['email'];

    $conexion = new mysqli($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

    if ($conexion->connect_error) {
        echo json_encode(['error' => 'Error de conexion: ' . $conexion->connect_error]);
        exit();
    }

    $stmt = $conexion->prepare("INSERT INTO chat_messages (user_id, email, message) VALUES (?, ?, ?)");
    $stmt->bind_param("iss", $user_id, $email, $message);

    if ($stmt->execute()) {
        echo json_encode(['success' => 'Message sent successfully']);
    } else {
        echo json_encode(['error' => 'Error sending message: ' . $stmt->error]);
    }

    $stmt->close();
    $conexion->close();
} else {
    echo json_encode(['error' => 'Parametros incompletos']);
}
?>
