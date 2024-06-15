<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

$json = array();

if (isset($_GET["email"]) && isset($_GET["contrasena"])) {
    $email = trim($_GET["email"]);
    $contrasena = trim($_GET["contrasena"]);
    
    $hashedPassword = password_hash($contrasena, PASSWORD_DEFAULT);
    
    $conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);
    
    if ($conexion->connect_error) {
        $json['error'] = 'Error de conexion: ' . $conexion->connect_error;
        echo json_encode($json);
        exit();
    }
    
    $consulta = "INSERT INTO admin (email, contrasena) VALUES (?, ?)";
    $stmt = $conexion->prepare($consulta);
    $stmt->bind_param("ss", $email, $hashedPassword);
    
    if ($stmt->execute()) {
        $json['success'] = 'Administrador insertado correctamente';
    } else {
        $json['error'] = 'Error: ' . $stmt->error;
    }
    
    $stmt->close();
    $conexion->close();
    echo json_encode($json);
} else {
    $json['error'] = 'Parámetros incompletos';
    echo json_encode($json);
}
?>
