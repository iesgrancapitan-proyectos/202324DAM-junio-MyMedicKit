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

    $consulta = "SELECT med_id, user_id, cn, nombre, laboratorio, pactivos, presc, formaFarmaceutica, dosis, viaAdministracion, pdf_1, pdf_2 FROM medicamentos WHERE med_id = ?";
    $stmt = $conexion->prepare($consulta);
    $stmt->bind_param("i", $med_id);

    if ($stmt->execute()) {
        $resultado = $stmt->get_result();
        
        if ($registro = $resultado->fetch_assoc()) {
            $json['medicamento'] = $registro;
        } else {
            $json['error'] = 'Medicamento no encontrado';
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
