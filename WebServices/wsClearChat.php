<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

header('Content-Type: application/json');

$json = file_get_contents('php://input');
$params = json_decode($json, true);

if (isset($params["user_id"])) {
    $user_id = $params['user_id'];

    $conexion = new mysqli($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

    if ($conexion->connect_error) {
        echo json_encode(['error' => 'Error de conexion: ' . $conexion->connect_error]);
        exit();
    }

    $stmt = $conexion->prepare("DELETE FROM chat_messages WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);

    if ($stmt->execute()) {
        echo json_encode(['success' => 'Chat limpiado correctamente']);
    } else {
        echo json_encode(['error' => 'Error al limpiar el chat: ' . $stmt->error]);
    }

    $stmt->close();
    $conexion->close();
} else {
    echo json_encode(['error' => 'Parametros incompletos']);
}
?>
