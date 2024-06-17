<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

$json = array();

if (isset($_GET["user_id"])) {
    $user_id = $_GET["user_id"];

    $conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

    if ($conexion->connect_error) {
        $json['error'] = 'Error de conexion: ' . $conexion->connect_error;
        echo json_encode($json);
        exit();
    }

    $consulta = "SELECT med_id, nombre FROM medicamentos WHERE user_id = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bind_param("i", $user_id);

    if ($stmt->execute()) {
        $resultado = $stmt->get_result();
        
        while ($registro = $resultado->fetch_assoc()) {
            $json['medicamentos'][] = $registro;
        }
        $stmt->close();
    } else {
        $json['error'] = 'Error en la consulta: ' . $stmt->error;
    }

    mysqli_close($conexion);
    echo json_encode($json);
} else {
    $json['error'] = 'Parámetros incompletos';
    echo json_encode($json);
}
?>
