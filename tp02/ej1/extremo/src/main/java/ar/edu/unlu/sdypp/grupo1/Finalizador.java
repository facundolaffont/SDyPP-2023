package ar.edu.unlu.sdypp.grupo1;

import org.springframework.context.SmartLifecycle;

/**
 * Componente responsable de finalizar el nodo extremo.
 */
public class Finalizador implements SmartLifecycle {

    /**
     * Indica si la aplicación está corriendo.
     */
    private boolean isRunning = false;

    @Override
    public void start() {
        this.isRunning = true;
    }

    @Override
    public void stop() {
        try {
            // Cierra el puerto externo.
            UPnP.closePort();
        } catch (Exception e) {}
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

}
