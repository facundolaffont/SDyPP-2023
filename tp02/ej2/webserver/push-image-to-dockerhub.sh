# Genera el jar del maestro, construye la imagen de Docker
# y la sube al repositorio de Docker Hub.

set -e

mvn clean package
docker build -t facundol/tp2-ej1-maestro:latest .
docker push facundol/tp2-ej1-maestro:latest