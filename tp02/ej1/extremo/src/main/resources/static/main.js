const searchField = document.getElementById("search-field");
const searchBtn = document.getElementById("search-btn");
const result = document.getElementById("result");
const disconnectBtn = document.getElementById("disconnect-btn");

searchBtn.addEventListener("click", function() {
    const searchValue = searchField.value.trim();
    if (searchValue) {
        searchBtn.disabled = true;
        result.innerHTML = "";
        fetch("http://localhost:8080/query?search=" + searchValue)
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
        fetch("http://localhost:8080/disconnect");
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
               + "  <td></td>"      // @TODO Link de descarga.
               + "</tr>";
    });
    table += "</tbody></table>";
    return table;
}
