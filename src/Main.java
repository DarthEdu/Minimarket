/**
 * Nombres de los integrantes:
 * @author Eduardo Porras (encargado del informe)
 * @author Sebastian Coronado (encargado de realizar login y menús: gerente y cliente)
 * @author Alan Rios (encargado de realizar las opciones por cada menú)
 * @author Erick Caiza (encargado del diseño y documentación en JavaDoc)
 *
 * @version 1.0V
 */
/**
 * Clase principal que ejecuta la ventana LOGIN para el funcionamiento del sistema.
 * Esta clase inicia la aplicación y muestra la ventana de inicio de sesión.
 */
public class Main {
    /**
     * Método principal que inicia la aplicación.
     * @param args Argumentos de la línea de comandos (no utilizado en este caso).
     */
    public static void main(String[] args) {
        // Crear una instancia de la clase Login y abrir la ventana de inicio de sesión
        Login login = new Login();
        login.abrirLogin();
    }
}
