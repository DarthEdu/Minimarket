import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Clase para la ventana de inicio de sesión.
 * Permite a los usuarios iniciar sesión en el sistema.
 * El acceso se gestiona según el rol del usuario.
 * Si las credenciales son válidas, se redirige al usuario al menú correspondiente.
 * Si las credenciales son inválidas, se muestra un mensaje de error.
 * Se registra el historial de sesión en la base de datos.
 * @author  Alan Rios
 */
public class Login extends JFrame {
    private JTextField usuarioTxt;
    private JPasswordField contrasenaTxt;
    private JButton ingresarButton;
    private JPanel panelLogin;

    /**
     * Constructor de la clase Login.
     * Configura la ventana de inicio de sesión y agrega el ActionListener para el botón de ingreso.
     */
    public Login() {
        super("Login");
        setContentPane(panelLogin);
        setUndecorated(true);

        ingresarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener usuario y contraseña
                String usuario = usuarioTxt.getText().trim(); // Trim para eliminar espacios en blanco al inicio y al final
                String contrasena = new String(contrasenaTxt.getPassword());

                // Validar campos no vacíos
                if (usuario.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(Login.this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Iniciar sesión
                inicioSesion(usuario, contrasena);
            }
        });
    }

    /**
     * Método para abrir la ventana de inicio de sesión.
     */
    public void abrirLogin() {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Método para iniciar sesión.
     * Se verifica la autenticidad de las credenciales en la base de datos.
     * @param usuario Nombre de usuario.
     * @param contrasena Contraseña del usuario.
     */
    public void inicioSesion(String usuario, String contrasena) {
        ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
        try (Connection conexion = manejadorMySQL.conexionMySQL()) {
            String sql = "SELECT id_rol, id FROM Usuario WHERE usuario = ? AND contrasena = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setString(1, usuario);
                pstmt.setString(2, contrasena);
                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (resultSet.next()) {
                        String rol = resultSet.getString("id_rol");
                        int idUsuario = resultSet.getInt("id");
                        insertarHistorialSesion(idUsuario, usuario);
                        abrirMenuSegunRol(rol);
                    } else {
                        JOptionPane.showMessageDialog(Login.this, "Usuario o contraseña incorrectos. Inténtalo de nuevo", "Error", JOptionPane.ERROR_MESSAGE);
                        limpiarCampos();
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Login.this, "Error al intentar acceder a la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para abrir el menú según el rol del usuario.
     * @param rol Rol del usuario.
     */
    private void abrirMenuSegunRol(String rol) {
        switch (rol) {
            case "1":
                Menu_gerente gerente = new Menu_gerente();
                gerente.abrirMenuGerente();
                break;
            case "2":
                Menu_cliente cliente = new Menu_cliente();
                cliente.abrirMenuCliente();
                break;
            default:
                JOptionPane.showMessageDialog(Login.this, "Rol no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
        dispose();
    }

    /**
     * Método para insertar el historial de sesión en la base de datos.
     * @param idCajero ID del cajero.
     * @param usuario Nombre de usuario.
     */
    private void insertarHistorialSesion(int idCajero, String usuario) {
        ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
        try (Connection conexion = manejadorMySQL.conexionMySQL()) {
            String sql = "INSERT INTO HistorialSesion (id_cajero, usuario) VALUES (?, ?)";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setInt(1, idCajero);
                pstmt.setString(2, usuario);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Login.this, "Error al insertar el historial de sesión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para limpiar los campos de usuario y contraseña.
     */
    private void limpiarCampos() {
        usuarioTxt.setText("");
        contrasenaTxt.setText("");
    }
}
