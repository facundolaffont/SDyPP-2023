package ar.edu.unlu.sdypp.grupo1;

import java.net.InetAddress;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;

/**
 * Clase encargada de abrir el puerto 8080 al exterior mediante el protocolo UPnP,
 * para permitir a otros nodos extremos (en otras redes) conectarse a éste para
 * descargar archivos.
 * @see http://bitletorg.github.io/weupnp/
 */
public class UPnP {

    /**
     * Instancia de GatewayDevice (si está abierto el puerto externo).
     */
    private static GatewayDevice gateway;

    /**
     * Abre el puerto 8080 al exterior, si la configuración del enrutador lo permite.
     * @return Booleano indicando si se abrió o no el puerto externo.
     * @throws Exception Si ocurre algún error en el proceso.
     */
    public static boolean openPort() throws Exception {
        // Inicia el descubrimiento del gateway.
        GatewayDiscover discover = new GatewayDiscover();
        discover.discover();

        // Selecciona un gateway válido para poder realizar mapeo de puertos.
        GatewayDevice gateway = discover.getValidGateway();

        // Si encuentra un gateway válido...
        if (gateway != null) {
            // Recupera las direcciones interna y externa del dispositivo.
            InetAddress localAddress = gateway.getLocalAddress();
            String externalIPAddress = gateway.getExternalIPAddress();
            System.out.println("Dirección interna: " + localAddress.getHostAddress());
            System.out.println("Dirección externa: " + externalIPAddress);

            // Crea una entrada del mapeo de puertos.
            PortMappingEntry portMapping = new PortMappingEntry();

            // Determina si no existe ya un mapeo para el puerto requerido.
            if (gateway.getSpecificPortMappingEntry(8080, "TCP", portMapping)) {
                // Se procede a crear el mapeo del puerto.
                if (gateway.addPortMapping(
                    8080,
                    8080,
                    localAddress.getHostAddress(),
                    "TCP",
                    "P2P"
                )) {
                    UPnP.gateway = gateway;
                    System.out.println("Se abrió el puerto 8080 al exterior.");
                    return true;
                }
                System.err.println("No se pudo abrir el puerto externo.");
                return false;
            }
            System.err.println("Ya existe un mapeo de puertos utilizando el puerto 8080.");
            return false;
        }
        System.err.println("No se pudo obtener un gateway válido, por lo que no"
                + " se abrirá el puerto externo.");
        return false;
    }

    /**
     * Cierra el puerto 8080 al exterior en caso de estar abierto.
     * @return Booleano indicando si se cerró o no el puerto externo.
     * @throws Exception Si ocurre algún error en el proceso.
     */
    public static boolean closePort() throws Exception {
        // Si el puerto está abierto...
        if (UPnP.gateway != null) {
            // Elimina el mapeo del puerto.
            UPnP.gateway.deletePortMapping(8080,"TCP");
            UPnP.gateway = null;
            System.out.println("Se cerró el puerto 8080 al exterior.");
            return true;
        }
        return false;
    }

}
