# Ejercicio 5 <picture><img alt="Código" src="https://img.shields.io/badge/C%C3%B3digo-%E2%9C%94-success"></picture> <picture><img alt="Documentación" src="https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success"></picture>
## Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.
+ Docker 23.0.1.

## Cómo probar el ejercicio

### Levantar el servidor

Ejecutar desde una terminal el siguiente comando:

```sh
docker run -p 8080:80 facundol/tp1-ej5
```

**_Nota:_** se puede reemplazar el puerto 8080 por cualquier otro puerto.

### Utilización del servicio con cURL

**_Nota:_** debés tener instalado cURL, y es recomendable que instales el paquete `jq`, que ayudará a visualizar el resultado en formato JSON de forma más legible: `sudo apt-get install jq`.

Para obtener información del clima, que será recibida en formato JSON, realizar una petición HTTP GET al servidor, con endpoint `/clima`:

```sh
curl localhost:8080/clima | jq
```

Ejemplo del resultado:

<center>

![Ejemplo del servicio](img/ejemplo.png "Ejemplo del servicio.")

</center>

### Probar el servidor desde el cliente

En otra consola, ejecutar:

```sh
curl -LOs https://github.com/facundolaffont/SDyPP-2023/raw/main/tp01/ej5/cliente/target/ej5-cliente-1-jar-with-dependencies.jar && \
echo "" && \
java -jar ej5-cliente-1-jar-with-dependencies.jar localhost:8080/clima | jq
```

