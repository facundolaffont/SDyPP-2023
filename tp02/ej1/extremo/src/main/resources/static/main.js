var disconnectBtn = document.getElementById("disconnect-btn");

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
