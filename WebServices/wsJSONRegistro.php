<?php
$hostname_localhost = "localhost";
$database_localhost = "proyectofinal";
$username_localhost = "root";
$password_localhost = "";

$json = array();

header('Content-Type: application/json');

if (isset($_GET["email"]) && isset($_GET["contrasena"]) && isset($_GET["nombre"]) && isset($_GET["apellidos"]) && isset($_GET["nacimiento"])) {
    $email = trim($_GET['email']);
    $contrasena = trim($_GET['contrasena']);
    $nombre = trim($_GET['nombre']);
    $apellidos = trim($_GET['apellidos']);
    $nacimiento = trim($_GET['nacimiento']);

    // Verificar que todos los campos no estén vacíos
    if ($email && $contrasena && $nombre && $apellidos && $nacimiento) {
        // Conexión a la base de datos
        $conexion = new mysqli($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);

        if ($conexion->connect_error) {
            $json['error'] = 'Error de conexion: ' . $conexion->connect_error;
            echo json_encode($json);
            exit();
        }

        // Hashing de la contraseña
        $hashed_password = password_hash($contrasena, PASSWORD_BCRYPT);

        // Uso de declaraciones preparadas para prevenir inyecciones SQL
        $stmt = $conexion->prepare("INSERT INTO users (email, contrasena, nombre, apellidos, nacimiento) VALUES (?, ?, ?, ?, ?)");
        $stmt->bind_param("sssss", $email, $hashed_password, $nombre, $apellidos, $nacimiento);

        if ($stmt->execute()) {
            $user_id = $stmt->insert_id; // Obtener el ID del último registro insertado

            $stmt->close();

            $stmt = $conexion->prepare("SELECT * FROM users WHERE user_id = ?");
            $stmt->bind_param("i", $user_id);

            if ($stmt->execute()) {
                $result = $stmt->get_result();
                if ($registro = $result->fetch_assoc()) {
                    $json['user'][] = $registro;
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
