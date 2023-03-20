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

En Linux, ejecutar desde una consola los siguientes comandos:

</div>

```sh
curl -L https://github.com/facundolaffont/SDyPP-2023/raw/main/tp01/ej5/target/ej5-1.jar -o "ej5-1.jar"

curl -L "https://github.com/facundolaffont/SDyPP-2023/raw/main/tp01/ej5/src/main/resources/geolite2-city-bd/GeoLite2-City.mmdb" -o "GeoLite2-City.mmdb" --output-dir src/main/resources/geolite2-city-bd --create-dirs

java -jar ej5-1.jar
```

El servidor se estará ejecutando en localhost, en el puerto 8080.

<div align="justify">

#### Utilización del servicio

Para obtener información del clima como JSON, realizar una petición HTTP GET al servidor, con endpoint `/clima`. Esto se puede hacer, por ejemplo, con curl, de la siguiente forma:

```sh
curl localhost:8080/clima | jq
```

A continuación, un ejemplo del resultado:

![Ejemplo del servicio](/tp01/ej5/img/ejemplo.png "Ejemplo del servicio.")

</div>
