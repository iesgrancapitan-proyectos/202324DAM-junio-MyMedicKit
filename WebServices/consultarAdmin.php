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

    $consulta = "SELECT admin_id, email, contrasena FROM admin WHERE email = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bind_param("s", $email);

    if ($stmt->execute()) {
        $resultado = $stmt->get_result();
        
        if ($registro = $resultado->fetch_assoc()) {
            if (password_verify($contrasena, $registro['contrasena'])) {
                unset($registro['contrasena']);
                $json['admin'][] = $registro;
                $json['success'] = true;
            } else {
                $json['error'] = 'Contraseña incorrecta';
                $json['success'] = false;
            }
        } else {
            $json['error'] = 'Administrador no encontrado';
            $json['success'] = false;
        }
    } else {
        $json['error'] = 'Error en la consulta: ' . $stmt->error;
        $json['success'] = false;
    }

    $stmt->close();
    $conexion->close();
    echo json_encode($json);
} else {
    $json['error'] = 'Parámetros incompletos';
    $json['success'] = false;
    echo json_encode($json);
}
?>
