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

+ Servidor Java, usando sockets TCP, que recibe un mensaje de texto y lo repite a su cliente.
+ Correspondiente cliente Java que también verifica el comportamiento del servidor.

</div>

### Ejercicio 2

<div align="justify">

+ Modificación del server del ejercicio 1, para que pueda atender varios clientes a la vez.
+ Versión UDP del servidor.

</div>

### Ejercicio 5

<div align="justify">

+ Servidor HTTP que devuelve información de clima del lugar donde reside el servidor, junto con la información de la localidad. Para obtener información del clima, realizar una petición `GET` al servidor, con endpoint `/clima` (si no se añade el endpoint, el servidor da la bienvenida y aclara cómo utilizar el servicio).

![Ejemplo del servicio](/tp01/ej5/img/ejemplo.png "Ejemplo del servicio.")

#### Instalación

1. Descargar el archivo .jar y la carpeta `src` a un mismo directorio.
2. Eliminar la carpeta `java`, que se encuentra dentro de la carpeta descargada, específicamente en `src/main/java`.
3. En la misma carpeta a la cual se copió el .jar, ejecutar:

```sh
java -jar ej5-1.jar
```

</div>
