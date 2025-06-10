create database if not exists dbclientes;
use dbclientes;

-- crear la tabla cliente
create table if not exists cliente (
    id_cliente int auto_increment primary key,
    nombre varchar(50) not null,
    apellido varchar(50) not null,
    edad int not null,
    fechaNacimiento date not null,
    sexo varchar(10) not null,
    prefijo varchar(10) not null,
    telefono varchar(15) not null,
    direccion varchar(100) not null,
    email varchar(100) not null,
    tipoDocumento varchar(20) not null,
    numeroDocumento varchar(20) not null,
    aceptaTerminos boolean not null,
    fechaRegistro timestamp default current_timestamp,
    estado varchar(10) default 'activo'
    );

insert into cliente (
id_cliente, nombre, apellido, edad, fechaNacimiento, sexo,
prefijo, telefono, direccion, email,
tipoDocumento, numeroDocumento, aceptaTerminos
) values (
'María', 'Gonzales', 28, '15/10/1984', 'femenino',
'+51', '987654321', 'Av. Los Pinos 123, Lima', 'maria.gonzales@example.com',
'DNI', '12345678', true
);

insert into cliente (
nombre, apellido, edad, fechaNacimiento, sexo,
prefijo, telefono, direccion, email,
tipoDocumento, numeroDocumento, aceptaTerminos
) values (
'María', 'Gonzales', 28, '1984-10-15', 'femenino',
'+51', '987654321', 'Av. Los Pinos 123, Lima', 'maria.gonzales@example.com',
'DNI', '12345678', true
);

INSERT INTO cliente (
    nombre, apellido, edad, fechaNacimiento, sexo,
    prefijo, telefono, direccion, email,
    tipoDocumento, numeroDocumento, aceptaTerminos
) VALUES (
             'María', 'Gonzales', 28, '1984-10-15', 'femenino',
             '+51', '987654321', 'Av. Los Pinos 123, Lima', 'maria.gonzales@example.com',
             'DNI', '12345678', TRUE
         );

-- You can add more insert statements here
INSERT INTO cliente (
    nombre, apellido, edad, fechaNacimiento, sexo,
    prefijo, telefono, direccion, email,
    tipoDocumento, numeroDocumento, aceptaTerminos
) VALUES (
             'Juan', 'Pérez', 35, '1989-03-20', 'masculino',
             '+51', '912345678', 'Calle Falsa 123, Miraflores', 'juan.perez@example.com',
             'DNI', '87654321', TRUE
         );

SELECT * FROM cliente;