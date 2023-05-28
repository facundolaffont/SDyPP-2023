var disconnectBtn = document.getElementById("disconnect-btn");

disconnectBtn.addEventListener("click", function() {
    if (confirm("¿Estás seguro que quieres desconectarte? Se cancelará cualquier"
            + " transferencia en curso.")) {
        fetch("http://localhost:8080/disconnect").finally(function() {
            document.getElementById("connected-panel").classList.add("d-none");
            document.getElementById("disconnected-panel").classList.remove("d-none");
        });
    }
});
