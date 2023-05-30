-- Base de datos para almacenar la información de la red.
CREATE DATABASE db;

-- Se conecta a la BD.
\c db;

-- Almacena la lista de extremos.
CREATE TABLE active_peers (
    peerIp VARCHAR(46) PRIMARY KEY,
    lastConnectionTimestamp BIGINT NOT NULL
);

-- Almacena la lista de archivos.
CREATE TABLE files (
    id SERIAL PRIMARY KEY,
    fileName VARCHAR(50) NOT NULL,
    sizeInBytes BIGINT NOT NULL,
    peerIp VARCHAR(46) NOT NULL,
    CONSTRAINT fk_files_active_peers
        FOREIGN KEY (peerIp)
        REFERENCES active_peers(peerIp)
        ON DELETE CASCADE
);
