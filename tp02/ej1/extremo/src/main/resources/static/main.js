const searchField = document.getElementById("search-field");
const searchBtn = document.getElementById("search-btn");
const result = document.getElementById("result");
const disconnectBtn = document.getElementById("disconnect-btn");

searchBtn.addEventListener("click", function() {
    const searchValue = searchField.value.trim();
    if (searchValue) {
        searchBtn.disabled = true;
        result.innerHTML = "";
        fetch(getUrlForFetch("/query?search=" + searchValue))
            .then(response => response.text())
            .then(response => {
                switch (response.split("=", 1)[0]) {
                    case "data":
                        result.innerHTML = buildTable(JSON.parse(response.substring(5)));
                        break;
                    case "error":
                        /** @TODO Mostrar error */
                        result.innerHTML = response;
                        break;
                    default:
                        /** @TODO Mostrar error */
                        result.innerHTML = response;
                }
            }).finally(() => {
                searchBtn.disabled = false;
            });
    }
});

disconnectBtn.addEventListener("click", function() {
    if (confirm("¿Estás seguro que quieres desconectarte? Se cancelará cualquier"
            + " transferencia en curso.")) {
        // Da aviso a los nodos maestros.
        fetch(getUrlForFetch("/disconnect"));
        // Muestra mensaje en el front.
        document.getElementById("connected-panel").classList.add("d-none");
        document.getElementById("disconnected-panel").classList.remove("d-none");
    }
});

/**
 * Genera el código HTML para la tabla de resultados de una búsqueda.
 * @param {Object} json JSON devuelto por los nodos maestros.
 * @returns {string} Código HTML de la tabla.
 */
function buildTable(json) {
    /** @TODO Mejorar tabla */
    let table = "<table class=\"table\">"
                + "  <thead>"
                + "    <tr>"
                + "      <th scope=\"col\">Nombre</th>"
                + "      <th scope=\"col\">Tamaño (bytes)</th>"
                + "      <th scope=\"col\">Host</th>"
                + "      <th scope=\"col\">Descargar</th>"
                + "    </tr>"
                + "  </thead>"
                + "  <tbody>";
    json.files.map(file => {
        table += "<tr>"
               + "  <th scope=\"row\">" + file.name + "</th>"
               + "  <td>" + file.sizeInBytes + "</td>"
               + "  <td>" + file.host + "</td>"
               + "  <td><button type=\"button\" onclick=\"downloadFile('"
               + file.host + "', '" + file.name + "')\">Descargar</button></td>"
               + "</tr>";
    });
    table += "</tbody></table>";
    return table;
}

/**
 * Solicita la descarga de un archivo.
 * @param {string} host Dirección IP del host desde el cual se descargará el archivo.
 * @param {string} name Nombre del archivo solicitado.
 */
function downloadFile(host, name) {
    fetch("http://" + host + ":8080/download?name=" + name)
        .then(response => response.blob())
        .then(blob => {
            saveAs(blob, name);
        });
}

/**
 * Retorna la URL de la instancia del nodo extremo para los fetchs.
 * @param {string} endpoint Endpoint del controlador del nodo extremo.
 * @returns {string}
 */
function getUrlForFetch(endpoint) {
    const { protocol, hostname, port } = window.location;
    return `${protocol}//${hostname}${port ? `:${port}` : ""}${endpoint}`;
}
