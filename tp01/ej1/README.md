# Ejercicio 1 <picture><img alt="Código" src="https://img.shields.io/badge/C%C3%B3digo-%E2%9C%94-success"></picture> <picture><img alt="Documentación" src="https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success"></picture>

## Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.

## Cómo probar el ejercicio

**_Nota:_** debe tener instalado cURL.

1. Primero, en una consola levantamos el server:

    ```sh
    curl -LOs https://github.com/facundolaffont/SDyPP-2023/raw/main/tp01/ej1/server/target/ej1-servidor-1.jar && \
    echo "" && \
    java -jar ej1-servidor-1.jar
    ```

2. Luego, en otra consola, se ejecuta el cliente:

    ```sh
    curl -LOs https://github.com/facundolaffont/SDyPP-2023/raw/main/tp01/ej1/cliente/target/ej1-cliente-1.jar && \
    echo "" && \
    java -jar ej1-cliente-1.jar
    ```

