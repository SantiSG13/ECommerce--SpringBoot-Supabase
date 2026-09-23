-- Datos por defecto para la tabla Clientes
INSERT INTO clientes (nombre, apellido, email, create_at) VALUES ('Santiago', 'Gómez', 'santiago@correo.com', NOW());
INSERT INTO clientes (nombre, apellido, email, create_at) VALUES ('Maria', 'Pérez', 'maria.perez@correo.com', NOW());
INSERT INTO clientes (nombre, apellido, email, create_at) VALUES ('Carlos', 'Rodríguez', 'carlos.rodriguez@correo.com', NOW());

-- Datos por defecto para la tabla Productos
INSERT INTO productos (nombre, precio, stock, categoria, descripcion) VALUES ('Laptop Asus ROG Strix', 1299.99, 15, 'Electrónica', 'Laptop Gamer i7, 16GB RAM, RTX 4060');
INSERT INTO productos (nombre, precio, stock, categoria, descripcion) VALUES ('Teclado Mecánico RGB', 89.50, 30, 'Periféricos', 'Teclado mecánico con switches red y retroiluminación RGB');
INSERT INTO productos (nombre, precio, stock, categoria, descripcion) VALUES ('Monitor Gamer 144Hz 27"', 249.00, 20, 'Monitores', 'Monitor IPS Full HD con tiempo de respuesta de 1ms');

-- Usuarios por defecto (Contraseña para ambos es: 12345)
INSERT INTO usuarios (usuario, contraseña, rol, create_at) VALUES ('admin', '{noop}12345', 'ROLE_ADMIN', NOW());
INSERT INTO usuarios (usuario, contraseña, rol, create_at) VALUES ('user', '{noop}12345', 'ROLE_USER', NOW());
