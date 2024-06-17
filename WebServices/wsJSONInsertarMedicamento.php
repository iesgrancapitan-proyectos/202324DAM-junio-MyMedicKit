<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

$json = array();

header('Content-Type: application/json');

if (isset($_GET["user_id"]) && isset($_GET["cn"]) && isset($_GET["nombre"]) && isset($_GET["laboratorio"]) && isset($_GET["pactivos"]) && isset($_GET["presc"]) && isset($_GET["formaFarmaceutica"]) && isset($_GET["dosis"]) && isset($_GET["viaAdministracion"]) && isset($_GET["pdf_1"]) && isset($_GET["pdf_2"])) {
    $user_id = trim($_GET['user_id']);
    $cn = trim($_GET['cn']);
    $nombre = trim($_GET['nombre']);
    $laboratorio = trim($_GET['laboratorio']);
    $pactivos = trim($_GET['pactivos']);
    $presc = trim($_GET['presc']);
    $formaFarmaceutica = trim($_GET['formaFarmaceutica']);
    $dosis = trim($_GET['dosis']);
    $viaAdministracion = trim($_GET['viaAdministracion']);
    $pdf_1 = trim($_GET['pdf_1']);
    $pdf_2 = trim($_GET['pdf_2']);

    // Verificar que todos los campos no estén vacíos
    if ($user_id && $cn && $nombre && $laboratorio && $pactivos && $presc && $formaFarmaceutica && $dosis && $viaAdministracion && $pdf_1 && $pdf_2) {
        // Conexión a la base de datos
        $conexion = new mysqli($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

        if ($conexion->connect_error) {
            $json['error'] = 'Error de conexion: ' . $conexion->connect_error;
            echo json_encode($json);
            exit();
        }

        // Uso de declaraciones preparadas para prevenir inyecciones SQL
        $stmt = $conexion->prepare("INSERT INTO medicamentos (user_id, cn, nombre, laboratorio, pactivos, presc, formaFarmaceutica, dosis, viaAdministracion, pdf_1, pdf_2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("iisssssssss", $user_id, $cn, $nombre, $laboratorio, $pactivos, $presc, $formaFarmaceutica, $dosis, $viaAdministracion, $pdf_1, $pdf_2);

        if ($stmt->execute()) {
            $med_id = $stmt->insert_id; // Obtener el ID del último registro insertado

            $stmt->close();

            $stmt = $conexion->prepare("SELECT * FROM medicamentos WHERE med_id = ?");
            $stmt->bind_param("i", $med_id);

            if ($stmt->execute()) {
                $result = $stmt->get_result();
                if ($registro = $result->fetch_assoc()) {
                    $json['medicamento'][] = $registro;
                }
            }
            $stmt->close();
        } else {
            $json['error'] = 'Error al insertar el registro: ' . $stmt->error;
        }

        $conexion->close();
        echo json_encode($json);

    } else {
        $json['error'] = 'Todos los campos son obligatorios';
        echo json_encode($json);
    }
} else {
    $json['error'] = 'Parametros incompletos';
    echo json_encode($json);
}
?>
