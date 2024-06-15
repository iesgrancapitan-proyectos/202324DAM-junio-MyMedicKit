<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

header('Content-Type: application/json');

$json = file_get_contents('php://input');
$params = json_decode($json, true);

if (isset($params["user_id"]) && isset($params["nombre"]) && isset($params["apellidos"]) && isset($params["nacimiento"])) {
    $user_id = $params['user_id'];
    $nombre = trim($params['nombre']);
    $apellidos = trim($params['apellidos']);
    $nacimiento = trim($params['nacimiento']);

    $conexion = new mysqli($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

    if ($conexion->connect_error) {
        echo json_encode(['error' => 'Error de conexion: ' . $conexion->connect_error]);
        exit();
    }

    $stmt = $conexion->prepare("UPDATE users SET nombre = ?, apellidos = ?, nacimiento = ? WHERE user_id = ?");
    $stmt->bind_param("sssi", $nombre, $apellidos, $nacimiento, $user_id);

    if ($stmt->execute()) {
        echo json_encode(['success' => 'Usuario actualizado correctamente']);
    } else {
        echo json_encode(['error' => 'Error al actualizar el registro: ' . $stmt->error]);
    }

    $stmt->close();
    $conexion->close();
} else {
    echo json_encode(['error' => 'Parametros incompletos']);
}
?>
