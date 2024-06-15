<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

$json = array();

if (isset($_GET["email"]) && isset($_GET["contrasena"])) {
    $email = trim($_GET["email"]);
    $contrasena = trim($_GET["contrasena"]);

    $conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

    if ($conexion->connect_error) {
        $json['error'] = 'Error de conexion: ' . $conexion->connect_error;
        echo json_encode($json);
        exit();
    }

    $consulta = "SELECT user_id, email, nombre, apellidos, nacimiento, contrasena FROM users WHERE email = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bind_param("s", $email);

    if ($stmt->execute()) {
        $resultado = $stmt->get_result();
        
        if ($registro = $resultado->fetch_assoc()) {
            if (password_verify($contrasena, $registro['contrasena'])) {
                unset($registro['contrasena']);
                $json['usuario'][] = $registro;
            } else {
                $json['error'] = 'Contraseña incorrecta';
            }
        } else {
            $json['error'] = 'Usuario no encontrado';
        }
    } else {
        $json['error'] = 'Error en la consulta: ' . $stmt->error;
    }

    $stmt->close();
    $conexion->close();
    echo json_encode($json);
} else {
    $json['error'] = 'Parámetros incompletos';
    echo json_encode($json);
}
?>
