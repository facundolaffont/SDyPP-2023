# Sistemas Distribuidos y Programación Paralela

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

#### 5.1. Consigna

<div align="justify">

Realizar un servidor HTTP que devuelva información de clima del lugar donde reside el servidor.

#### 5.2. Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ Javac 19.0.1.
+ JRE 19.0.1+10-21.
+ Docker 23.0.1.

#### 5.3. Compilación y empaquetado del jar con Maven

1. En una terminal, ubicarse en la carpeta del proyecto `tp01/ej5`.
2. Ejecutar `mvn package`.

#### 5.4. Cómo poner en funcionamiento el servidor

**_Requisitos previos:_** debe tener instalado Docker.

1. Ejecutar desde una terminal los siguientes comandos:

</div>

```sh
sudo docker build -t tp1:ej5 .
```

2. Una vez que se cree la imagen, ejecutar:

```sh
sudo docker run -p 8080:80 -it --name tp1-ej5 tp1:ej5
```

**_Nota:_** se puede reemplazar el puerto 8080 por cualquier otro puerto.

**_Nota:_** el servidor se estará ejecutando en localhost, en el puerto elegido para el host (en este caso fue el 8080).

<div align="justify">

#### 5.5. Utilización del servicio

**_Recomendación:_** instalar el paquete `jq`, que ayudará a visualizar el resultado en formato JSON de forma más legible: `sudo apt-get install jq`.

Para obtener información del clima, que será recibida en formato JSON, realizar una petición HTTP GET al servidor, con endpoint `/clima`. Esto se puede hacer, por ejemplo, con `curl` (y luego se puede formatear con `jq`), de la siguiente forma:

```sh
curl localhost:8080/clima | jq
```

A continuación, un ejemplo del resultado:

</div>

<center>

![Ejemplo del servicio](/tp01/ej5/img/ejemplo.png "Ejemplo del servicio.")

</center>