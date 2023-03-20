# SDyPP-2023

<div align="justify">

Trabajos prácticos de la asignatura Sistemas Distribuidos y Programación Paralela, de la carrera de Licenciatura en Sistemas de Información de la Universidad Nacional de Luján.

</div>

## Integrantes

+ Facundo Laffont.
+ Joaquín Tossello.
+ Juan Pablo Carini.

## Trabajo Práctico 1

### Ejercicio 1

<div align="justify">

#### Consigna

Crear un servidor, usando sockets TCP, que reciba un mensaje de texto y lo repita a su cliente. Desarrollar el correspondiente cliente que, además de enviar el mensaje, verifique el comportamiento del servidor.

</div>

### Ejercicio 2

<div align="justify">

#### Consigna

Modificar el servidor del ejercicio 1, para que pueda atender varios clientes a la vez. Crear, también, la versión UDP del servidor.

</div>

### Ejercicio 5

#### Consigna

<div align="justify">

Realizar un servidor HTTP que devuelva información de clima del lugar donde reside el servidor.

#### Cómo poner en funcionamiento el servidor

1. Descargar el archivo .jar y la carpeta `src` a un mismo directorio.
2. Eliminar la carpeta `java`, que se encuentra dentro de la carpeta descargada, específicamente en `src/main/java`.
3. En la misma carpeta a la cual se copió el .jar, ejecutar:

```sh
java -jar ej5-1.jar
```

#### Utilización del servicio

Para obtener información del clima como JSON, realizar una petición HTTP GET al servidor, con endpoint `/clima`.

![Ejemplo del servicio](/tp01/ej5/img/ejemplo.png "Ejemplo del servicio.")

</div>
