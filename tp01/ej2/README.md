# Ejercicio 2 <picture><img alt="Código" src="https://img.shields.io/badge/C%C3%B3digo-%E2%9C%94-success"></picture> <picture><img alt="Documentación" src="https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success"></picture>
## Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.

## Cómo probar el ejercicio

### Prueba del servidor TCP

**_Nota:_** debe tener instalado cURL y parallel.

1. Primero, en una consola levantamos el server:

    ```sh
    curl -LOs https://github.com/facundolaffont/SDyPP-2023/raw/main/tp01/ej2/serverTCP/target/ej2-servidor-tcp-1.jar && \
    echo "" && \
    java -jar ej2-servidor-tcp-1.jar
    ```

2. Luego, en otra consola, se ejecuta paralelamente el cliente:

    ```sh
    parallel java -jar ej1-cliente-1.jar -- {1..5}
    ```

    **_Nota:_** las tareas se ejecutarán de forma paralela sólo si así lo determina el SO. Modificando el valor '5' se puede cambiar la cantidad de tareas que se van a ejecutar de forma paralela, aunque su ejecución en paralelo dependerá de lo que se comentó recién.
    
    **_Nota:_** ejecutar este comando varias veces para examinar cómo en el servidor se mezclan los mensajes de los diferentes procesos.

