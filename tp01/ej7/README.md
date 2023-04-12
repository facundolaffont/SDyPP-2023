# Ejercicio 7 ![Código](https://img.shields.io/badge/C%C3%B3digo-%E2%9C%94-success) ![Documentación](https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success)

## Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.
+ Docker 23.0.1.

## Levantar los servicios

**_Nota:_** debe tener instalado cURL.

En la consola, ejecutar:

```sh
curl -L -O -s "https://raw.githubusercontent.com/facundolaffont/SDyPP-2023/main/tp01/ej7/docker-compose.yml" && \
docker compose up
```

Esperar a que se terminen de generar los logs para que estén listos los servicios.

## Ejecutar petición de servicio utilizando cURL

Para realizar una suma, desde otra consola, ejecutar:

```sh
curl \                                              
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{"tarea":"suma","parametros":[1,2,3000,25]}' \
-v \
localhost:8080/ejecutar-tarea-remota
```

**_Nota:_** puede observar, en la consola donde se ejecutaron los servicios, cómo se procesa la información, mientras espera a recibir la respuesta.

Igualmente, puede utilizar los siguientes parámetros para pedir que un servicio calcule PI con 1000 dígitos de precisión:

```sh
curl \                                              
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{"tarea":"calculo-pi","parametros":1000}' \
-v \
localhost:8080/ejecutar-tarea-remota
```
