# Sistemas Distribuidos y Programación Paralela

Trabajos prácticos de la asignatura Sistemas Distribuidos y Programación Paralela, de la carrera de Licenciatura en Sistemas de Información de la Universidad Nacional de Luján.

## Integrantes

+ Facundo Laffont.
+ Joaquín Tossello.
+ Juan Pablo Carini.

## Trabajo Práctico 1

### Ejercicio 1 ![Documentación 100%](https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success)

#### 1.1. Consigna

Crear un servidor, usando sockets TCP, que reciba un mensaje de texto y lo repita a su cliente. Desarrollar el correspondiente cliente que, además de enviar el mensaje, verifique el comportamiento del servidor.

#### 1.2. Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.

#### 1.3. Cómo probar el ejercicio

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

### Ejercicio 2 ![Documentación 100%](https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success)

#### 2.1. Consigna

Modificar el servidor del ejercicio 1, para que pueda atender varios clientes a la vez. Crear, también, la versión UDP del servidor.

#### 2.2. Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.

#### 2.3. Cómo probar el ejercicio

##### 2.3.1. Prueba del servidor TCP

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

### Ejercicio 5 ![Documentación 100%](https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success)

#### 5.1. Consigna

Realizar un servidor HTTP que devuelva información de clima del lugar donde reside el servidor.

#### 5.2. Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.
+ Docker 23.0.1.

#### 5.3. Cómo poner en funcionamiento el servidor

Ejecutar desde una terminal el siguiente comando:

```sh
docker run -p 8080:80 facundol/tp1-ej5
```

**_Nota:_** se puede reemplazar el puerto 8080 por cualquier otro puerto.

#### 5.4. Utilización del servicio

**_Nota:_** debés tener instalado cURL, y es recomendable que instales el paquete `jq`, que ayudará a visualizar el resultado en formato JSON de forma más legible: `sudo apt-get install jq`.

Para obtener información del clima, que será recibida en formato JSON, realizar una petición HTTP GET al servidor, con endpoint `/clima`:
```sh
curl localhost:8080/clima | jq
```

A continuación, un ejemplo del resultado:

<center>

![Ejemplo del servicio](/tp01/ej5/img/ejemplo.png "Ejemplo del servicio.")

</center>

### Ejercicio 7 ![Documentación 100%](https://img.shields.io/badge/Documentaci%C3%B3n-%E2%9C%94-success)

#### 7.1. Consigna

Implementar un servidor que resuelva tareas genéricas. Para ello, se debe respetar lo siguiente:

##### 7.1.1.  Servidor

+ Debe ser desarrollado con tecnología HTTP.
+ Debe estar contenerizado y a la escucha de nuevas peticiones del cliente.
+ Debe tener un método ejecutarTareaRemota(). El objetivo es que desde el cliente se puedan escribir parámetros de objetos (que implementen la solución "tarea genérica") que hagan una operatoria en concreto (por ejemplo, calcular un número aleatorio, un primo, el valor de Pi con cierta precisión, procesar imágenes, etc.).
+ Debe recibir los parámetros del cliente a través de un método GET/POST HTTP con parámetros descritos en JSON.
+ El servidor levantará, sólo por el tiempo de ejecución, el servidor que brinda servicio de procesamiento (denominado en esta consigna "servicio tarea") como un contenedor. Una vez levantado, se le realizará a este servicio tarea una petición ejecutarTarea() (es decir, le comunicará los parámetros), y esperará los resultados para luego enviárselos al cliente.

##### 7.1.2. Servicio tarea

+ Debe ser un web server.
+ Debe implementar el método ejecutarTarea().
+ Debe recibir los parámetros en formato JSON.
+ Se debe paquetizar la solución como imagen Docker, y se la debe publicar en el registro de Docker Hub.

##### 7.1.3. Cliente

+ Conociendo el servicio tarea desarrollado, el cliente debe realizar una petición (GET/POST HTTP) al servidor, con los parámetros necesarios en formato JSON, determinando el cálculo a realizar, parámetros, datos e imagen docker (se puede incluir un usuario y contraseña si es un registro privado).

#### 7.2. Software utilizado

+ Debian 11 (bulseye), versión 5.10.0-21-amd64.
+ Maven 4.0.0-alpha-5.
+ JDK 19.0.1.
+ Docker 23.0.1.

#### 7.3. Levantar los servicios

**_Nota:_** debe tener instalado cURL.

En la consola, ejecutar:

```sh
curl -L -O -s "https://raw.githubusercontent.com/facundolaffont/SDyPP-2023/main/tp01/ej7/docker-compose.yml" && \
docker compose up
```

Esperar a que se terminen de generar los logs para que estén listos los servicios.

#### 7.4. Ejecutar una petición utilizando cURL

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
