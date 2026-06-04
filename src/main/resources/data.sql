-- ===============================================
-- DATOS INICIALES - ACADEMIA SAN LUIS  
-- ===============================================

-- Roles
INSERT IGNORE INTO roles (id, nombre, descripcion) VALUES (1, 'ROLE_ADMIN', 'Administrador del sistema');
INSERT IGNORE INTO roles (id, nombre, descripcion) VALUES (2, 'ROLE_PROFESOR', 'Profesor de la academia');
INSERT IGNORE INTO roles (id, nombre, descripcion) VALUES (3, 'ROLE_ALUMNO', 'Alumno de la academia');
INSERT IGNORE INTO roles (id, nombre, descripcion) VALUES (4, 'ROLE_PADRE', 'Padre de familia');

-- Niveles
INSERT IGNORE INTO niveles (id, nombre, descripcion) VALUES (1, 'A1 - Principiante', 'Nivel básico inicial');
INSERT IGNORE INTO niveles (id, nombre, descripcion) VALUES (2, 'A2 - Elemental', 'Nivel básico avanzado');
INSERT IGNORE INTO niveles (id, nombre, descripcion) VALUES (3, 'B1 - Intermedio', 'Nivel intermedio inicial');
INSERT IGNORE INTO niveles (id, nombre, descripcion) VALUES (4, 'B2 - Intermedio Alto', 'Nivel intermedio avanzado');
INSERT IGNORE INTO niveles (id, nombre, descripcion) VALUES (5, 'Robótica Básica', 'Introducción a la robótica educativa');
INSERT IGNORE INTO niveles (id, nombre, descripcion) VALUES (6, 'Robótica Intermedia', 'Robótica con Arduino y sensores');
INSERT IGNORE INTO niveles (id, nombre, descripcion) VALUES (7, 'Robótica Avanzada', 'Programación de robots autónomos');

-- Cursos de Inglés
INSERT IGNORE INTO cursos (id, nombre, nivel_id, descripcion, duracion_semanas, precio, activo) 
VALUES (1, 'English for Beginners', 1, 'Curso introductorio de inglés con enfoque comunicativo', 12, 75.00, true);
INSERT IGNORE INTO cursos (id, nombre, nivel_id, descripcion, duracion_semanas, precio, activo) 
VALUES (2, 'English Elementary', 2, 'Inglés elemental para jóvenes y adultos', 12, 85.00, true);
INSERT IGNORE INTO cursos (id, nombre, nivel_id, descripcion, duracion_semanas, precio, activo) 
VALUES (3, 'English Intermediate', 3, 'Inglés nivel intermedio con preparación de exámenes', 14, 100.00, true);

-- Cursos de Robótica
INSERT IGNORE INTO cursos (id, nombre, nivel_id, descripcion, duracion_semanas, precio, activo) 
VALUES (4, 'Robótica para Niños', 5, 'Introducción divertida a la robótica usando kits educativos', 8, 60.00, true);
INSERT IGNORE INTO cursos (id, nombre, nivel_id, descripcion, duracion_semanas, precio, activo) 
VALUES (5, 'Robótica con Arduino', 6, 'Aprende a programar Arduino, usar sensores y circuitos', 10, 80.00, true);
INSERT IGNORE INTO cursos (id, nombre, nivel_id, descripcion, duracion_semanas, precio, activo) 
VALUES (6, 'Programación de Robots', 7, 'Robots autónomos e inteligencia artificial básica', 12, 95.00, true);

-- Horarios
INSERT IGNORE INTO horarios (id, curso_id, dia_semana, hora_inicio, hora_fin, cupo_maximo) 
VALUES (1, 1, 'Lunes y Miércoles', '08:00', '10:00', 20);
INSERT IGNORE INTO horarios (id, curso_id, dia_semana, hora_inicio, hora_fin, cupo_maximo) 
VALUES (2, 1, 'Martes y Jueves', '14:00', '16:00', 20);
INSERT IGNORE INTO horarios (id, curso_id, dia_semana, hora_inicio, hora_fin, cupo_maximo) 
VALUES (3, 2, 'Sábado', '09:00', '12:00', 15);
INSERT IGNORE INTO horarios (id, curso_id, dia_semana, hora_inicio, hora_fin, cupo_maximo) 
VALUES (4, 4, 'Viernes', '14:00', '16:00', 15);
INSERT IGNORE INTO horarios (id, curso_id, dia_semana, hora_inicio, hora_fin, cupo_maximo) 
VALUES (5, 5, 'Sábado', '14:00', '17:00', 12);
INSERT IGNORE INTO horarios (id, curso_id, dia_semana, hora_inicio, hora_fin, cupo_maximo) 
VALUES (6, 6, 'Martes y Jueves', '16:00', '18:00', 10);

-- Usuarios de prueba (contraseña: admin123)
-- BCrypt hash de "admin123"
INSERT IGNORE INTO usuarios (id, nombre, apellido, email, password, telefono, activo, rol_id, fecha_registro) 
VALUES (1, 'Maritza', 'Méndez', 'admin@sanluis.edu.sv', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '6308-8331', true, 1, NOW());
INSERT IGNORE INTO usuarios (id, nombre, apellido, email, password, telefono, activo, rol_id, fecha_registro) 
VALUES (2, 'Carlos', 'García', 'profesor@sanluis.edu.sv', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '7777-1111', true, 2, NOW());
INSERT IGNORE INTO usuarios (id, nombre, apellido, email, password, telefono, activo, rol_id, fecha_registro) 
VALUES (3, 'Ana', 'López', 'alumno@sanluis.edu.sv', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '7777-2222', true, 3, NOW());