<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

$json = array();

if (isset($_GET["med_id"])) {
    $med_id = $_GET["med_id"];

    $conexion = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

    if ($conexion->connect_error) {
        $json['error'] = 'Error de conexion: ' . $conexion->connect_error;
        echo json_encode($json);
        exit();
    }

    $consulta = "DELETE FROM medicamentos WHERE med_id = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bind_param("i", $med_id);

    if ($stmt->execute()) {
        $json['success'] = 'Medicamento eliminado correctamente';
    } else {
        $json['error'] = 'Error en la consulta: ' . $stmt->error;
    }

    $stmt->close();
    mysqli_close($conexion);
    echo json_encode($json);
} else {
    $json['error'] = 'Parámetros incompletos';
    echo json_encode($json);
}
?>
