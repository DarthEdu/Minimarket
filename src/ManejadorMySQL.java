import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para manejar la conexión a una base de datos MySQL.
 */
public class ManejadorMySQL {

    /**
     * Método para establecer una conexión a una base de datos MySQL.
     * @return La conexión establecida.
     */
    public Connection conexionMySQL() {
        Connection conexion = null;
        try {
            // Configuración de la conexión a la base de datos
            String url = "jdbc:mysql://localhost:3306/minimarket"; // URL de la base de datos
            String usuarioDB = "root"; // Nombre de usuario de la base de datos
            String contrasenaDB = "123456"; // Contraseña de la base de datos

            // Establecer la conexión
            conexion = DriverManager.getConnection(url, usuarioDB, contrasenaDB);
            // Si se establece la conexión correctamente, se retorna la conexión
            // JOptionPane.showMessageDialog(null, "Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            // Manejar errores al establecer la conexión
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion; // Devuelve la conexión (puede ser null si la conexión falla)
    }
}
