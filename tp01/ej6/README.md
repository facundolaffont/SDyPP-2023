# Ejercicio 6 <picture><img alt="Código" src="https://img.shields.io/badge/C%C3%B3digo-%E2%9C%94-success"></picture> <picture><img alt="Documentación" src="https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success"></picture>

## Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.

## Cómo probar el ejercicio

**_Nota:_** debe tener instalado cURL.

1. Primero, en una consola levantamos el server:

    ```sh
    curl -LOs https://github.com/facundolaffont/SDyPP-2023/raw/main/tp01/ej6/target/ej6-1-jar-with-dependencies.jar && \
    echo "" && \
    java -jar ej6-1-jar-with-dependencies.jar
    ```

2. Luego, en otra consola, realizar la llamada:

    ```sh
    curl "localhost:8080/suma?vector1=5,100,1,1&vector2=5,5,6"
    ```

