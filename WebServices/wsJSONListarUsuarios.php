<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

$json = array();

header('Content-Type: application/json');

$conexion = new mysqli($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

if ($conexion->connect_error) {
    $json['error'] = 'Error de conexion: ' . $conexion->connect_error;
    echo json_encode($json);
    exit();
}

$query = "SELECT user_id, email, nombre, apellidos, nacimiento FROM users";
$result = $conexion->query($query);

if ($result) {
    while ($registro = $result->fetch_assoc()) {
        $json['users'][] = $registro;
    }
    $result->close();
} else {
    $json['error'] = 'Error al realizar la consulta: ' . $conexion->error;
}

$conexion->close();
echo json_encode($json);
?>
