# Sistemas Distribuidos y Programación Paralela

Trabajos prácticos de la asignatura Sistemas Distribuidos y Programación Paralela, de la carrera de Licenciatura en Sistemas de Información de la Universidad Nacional de Luján.

## Integrantes

+ Facundo Laffont.
+ Joaquín Tossello.
+ Juan Pablo Carini.

## Trabajo Práctico 1

Se puede acceder al enlace del documento de cada ejercicio cliqueando en el títlo. En este documento sólo se plasman las consignas.

### [Ejercicio 1](https://github.com/facundolaffont/SDyPP-2023/tree/main/tp01/ej1)

Crear un servidor, usando sockets TCP, que reciba un mensaje de texto y lo repita a su cliente. Desarrollar el correspondiente cliente que, además de enviar el mensaje, verifique el comportamiento del servidor.

### [Ejercicio 2](https://github.com/facundolaffont/SDyPP-2023/tree/main/tp01/ej2)

Modificar el servidor del ejercicio 1, para que pueda atender varios clientes a la vez. Crear, también, la versión UDP del servidor.

### [Ejercicio 5](https://github.com/facundolaffont/SDyPP-2023/tree/main/tp01/ej5)

Realizar un servidor HTTP que devuelva información de clima del lugar donde reside el servidor.

### [Ejercicio 7](https://github.com/facundolaffont/SDyPP-2023/tree/main/tp01/ej7)

Implementar un servidor que resuelva tareas genéricas. Para ello, se debe respetar lo siguiente:

#### Servidor

+ Debe ser desarrollado con tecnología HTTP.
+ Debe estar contenerizado y a la escucha de nuevas peticiones del cliente.
+ Debe tener un método ejecutarTareaRemota(). El objetivo es que desde el cliente se puedan escribir parámetros de objetos (que implementen la solución "tarea genérica") que hagan una operatoria en concreto (por ejemplo, calcular un número aleatorio, un primo, el valor de Pi con cierta precisión, procesar imágenes, etc.).
+ Debe recibir los parámetros del cliente a través de un método GET/POST HTTP con parámetros descritos en JSON.
+ El servidor levantará, sólo por el tiempo de ejecución, el servidor que brinda servicio de procesamiento (denominado en esta consigna "servicio tarea") como un contenedor. Una vez levantado, se le realizará a este servicio tarea una petición ejecutarTarea() (es decir, le comunicará los parámetros), y esperará los resultados para luego enviárselos al cliente.

#### Servicio tarea

+ Debe ser un web server.
+ Debe implementar el método ejecutarTarea().
+ Debe recibir los parámetros en formato JSON.
+ Se debe paquetizar la solución como imagen Docker, y se la debe publicar en el registro de Docker Hub.

#### Cliente

+ Conociendo el servicio tarea desarrollado, el cliente debe realizar una petición (GET/POST HTTP) al servidor, con los parámetros necesarios en formato JSON, determinando el cálculo a realizar, parámetros, datos e imagen docker (se puede incluir un usuario y contraseña si es un registro privado).

