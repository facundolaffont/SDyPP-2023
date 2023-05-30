# Nodo extremo

## Cómo ejecutar

### Local

#### Precondiciones

* Debe estar instalado Java 19.
* Debe estar libre el puerto 8080 dado que es el utilizado por el nodo.

#### Pasos

Para iniciar el nodo extremo localmente se debe ejecutar el siguiente comando:
```
java -jar target/tp2-ej1-extremo-1.0.0-rc.jar <fichero_maestros> <directorio_compartido>
```
Para obtener ayuda sobre los argumentos ejecutar:
```
java -jar target/tp2-ej1-extremo-1.0.0-rc.jar
```

### Docker

#### Precondiciones

* Debe estar instalado Docker.

#### Pasos

Para iniciar el nodo extremo dockerizado se deben seguir los siguientes pasos:

1. Construir la imagen:
```
docker build -t nodoextremo .
```
2. Iniciar un contenedor:
```
docker run [--name <nombre_contenedor>] -p <puerto_local>:8080 -v <fichero_maestros>:/app/masters -v <directorio_compartido>:/app/sharedFolder nodoextremo /bin/sh -c "java -jar app.jar masters sharedFolder"
```