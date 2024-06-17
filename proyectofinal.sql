-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 15-06-2024 a las 21:28:51
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

CREATE DATABASE IF NOT EXISTS proyectofinal;
USE proyectofinal;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `proyectofinal`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `admin`
--

CREATE TABLE `admin` (
  `admin_id` int(11) NOT NULL,
  `email` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `contrasena` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `admin`
--

INSERT INTO `admin` (`admin_id`, `email`, `contrasena`) VALUES
(1, 'admin@gmail.com', '$2y$10$C3XozfyAm4QqStTteda3kOYYWsoXy7LU7WJpCWq9m1k3UlODQXLsy');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chat_messages`
--

CREATE TABLE `chat_messages` (
  `message_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `email` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamentos`
--

CREATE TABLE `medicamentos` (
  `med_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `cn` int(20) NOT NULL,
  `nombre` text NOT NULL,
  `laboratorio` text NOT NULL,
  `pactivos` text NOT NULL,
  `presc` text NOT NULL,
  `formaFarmaceutica` text NOT NULL,
  `dosis` text NOT NULL,
  `viaAdministracion` text NOT NULL,
  `pdf_1` text NOT NULL,
  `pdf_2` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `medicamentos`
--

INSERT INTO `medicamentos` (`med_id`, `user_id`, `cn`, `nombre`, `laboratorio`, `pactivos`, `presc`, `formaFarmaceutica`, `dosis`, `viaAdministracion`, `pdf_1`, `pdf_2`) VALUES
(17, 15, 0, 'BETADINE 100 MG/ML SOLUCIÓN CUTÁNEA , 1 frasco de 50 ml', 'Meda Pharma S.L.', 'POVIDONA IODADA', 'Sin Receta', 'SOLUCIÓN CUTÁNEA', '100 mg/ml', 'USO CUTÁNEO', 'https://cima.aemps.es/cima/pdfs/ft/36340/FT_36340.pdf', 'https://cima.aemps.es/cima/pdfs/p/36340/P_36340.pdf'),
(18, 10, 0, 'BETADINE 100 MG/ML SOLUCIÓN CUTÁNEA , 1 frasco de 50 ml', 'Meda Pharma S.L.', 'POVIDONA IODADA', 'Sin Receta', 'SOLUCIÓN CUTÁNEA', '100 mg/ml', 'USO CUTÁNEO', 'https://cima.aemps.es/cima/pdfs/ft/36340/FT_36340.pdf', 'https://cima.aemps.es/cima/pdfs/p/36340/P_36340.pdf'),
(19, 15, 694109, 'BETADINE 100 MG/ML SOLUCIÓN CUTÁNEA , 1 frasco de 50 ml', 'Meda Pharma S.L.', 'POVIDONA IODADA', 'Sin Receta', 'SOLUCIÓN CUTÁNEA', '100 mg/ml', 'USO CUTÁNEO', 'https://cima.aemps.es/cima/pdfs/ft/36340/FT_36340.pdf', 'https://cima.aemps.es/cima/pdfs/p/36340/P_36340.pdf'),
(24, 15, 658257, 'PARACETAMOL KERN PHARMA 1 g COMPRIMIDOS EFG, 40 comprimidos', 'Kern Pharma S.L.', 'PARACETAMOL', 'Medicamento Sujeto A Prescripción Médica', 'COMPRIMIDO', '1 g paracetamol', 'VÍA ORAL', 'https://cima.aemps.es/cima/pdfs/ft/68339/FT_68339.pdf', 'https://cima.aemps.es/cima/pdfs/p/68339/P_68339.pdf'),
(25, 15, 0, 'AZITROMICINA DARI PHARMA 500 mg COMPRIMIDOS RECUBIERTOS CON PELICULA EFG , 3 comprimidos', 'Dari Pharma S.L.', 'AZITROMICINA DIHIDRATO', 'Medicamento Sujeto A Prescripción Médica', 'COMPRIMIDO RECUBIERTO CON PELÍCULA', '500 mg', 'VÍA ORAL', 'https://cima.aemps.es/cima/pdfs/ft/65649/FT_65649.pdf', 'https://cima.aemps.es/cima/pdfs/p/65649/P_65649.pdf'),
(26, 15, 650839, 'AZITROMICINA DARI PHARMA 500 mg COMPRIMIDOS RECUBIERTOS CON PELICULA EFG , 3 comprimidos', 'Dari Pharma S.L.', 'AZITROMICINA DIHIDRATO', 'Medicamento Sujeto A Prescripción Médica', 'COMPRIMIDO RECUBIERTO CON PELÍCULA', '500 mg', 'VÍA ORAL', 'https://cima.aemps.es/cima/pdfs/ft/65649/FT_65649.pdf', 'https://cima.aemps.es/cima/pdfs/p/65649/P_65649.pdf'),
(27, 15, 694109, 'BETADINE 100 MG/ML SOLUCIÓN CUTÁNEA , 1 frasco de 50 ml', 'Meda Pharma S.L.', 'POVIDONA IODADA', 'Sin Receta', 'SOLUCIÓN CUTÁNEA', '100 mg/ml', 'USO CUTÁNEO', 'https://cima.aemps.es/cima/pdfs/ft/36340/FT_36340.pdf', 'https://cima.aemps.es/cima/pdfs/p/36340/P_36340.pdf'),
(31, 15, 650839, 'AZITROMICINA DARI PHARMA 500 mg COMPRIMIDOS RECUBIERTOS CON PELICULA EFG , 3 comprimidos', 'Dari Pharma S.L.', 'AZITROMICINA DIHIDRATO', 'Medicamento Sujeto A Prescripción Médica', '{\"id\":42,\"nombre\":\"COMPRIMIDO RECUBIERTO CON PELÍCULA\"}', '500 mg', 'VÍA ORAL', 'https://cima.aemps.es/cima/pdfs/ft/65649/FT_65649.pdf', 'https://cima.aemps.es/cima/pdfs/p/65649/P_65649.pdf');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `contrasena` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `apellidos` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nacimiento` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Volcado de datos para la tabla `users`
--

INSERT INTO `users` (`user_id`, `email`, `contrasena`, `nombre`, `apellidos`, `nacimiento`) VALUES
(10, 'borja@gmail.com', '$2y$10$.3Jx9HkxmGVVoqlWmlUD8eETLAbtO0llRNp6g4hlSPLdbc2hAQcGC', 'Borja', 'Garramiola', '2001-05-07'),
(12, 'rebeca@gmail.com', '$2y$10$acuHomaoGp.VkFWh3Se1X.7R3f3c0TeU46I568R0.RuWzgSvI0W8q', 'Rebeca', 'Lopez Ordoñez', '2004-11-21'),
(15, 'prueba1@gmail.com', '$2y$10$TAXcy/dFfUuPedaFyFyQ.u3iYJ1GsvwFq3L08ct0Eju.mcJ/SyrkW', 'Prueb', 'Pruebanez', '2001-06-07'),
(16, 'prueba2@gmail.com', '$2y$10$o3KnDPqKFL35PX4htNVOBem3Ej.Z/91We1faDmnmIhp781.JtnAXy', 'prueba2', 'prueba2', '2001-05-07');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`admin_id`);

--
-- Indices de la tabla `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD PRIMARY KEY (`message_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indices de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD PRIMARY KEY (`med_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indices de la tabla `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `admin`
--
ALTER TABLE `admin`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `chat_messages`
--
ALTER TABLE `chat_messages`
  MODIFY `message_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=75;

--
-- AUTO_INCREMENT de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  MODIFY `med_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT de la tabla `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD CONSTRAINT `chat_messages_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Filtros para la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
