import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase para la ventana de agregar usuarios desde el menú del gerente.
 * Permite al gerente agregar nuevos usuarios al sistema.
 * @author  Alan Rios
 */
public class AgregarUsuarios extends JFrame {
    private JTextField usuarioTxt;
    private JPasswordField contrasenaTxt;
    private JComboBox<String> rolCombo;
    private JButton regresarAlMenuButton;
    private JButton agregarUsuarioButton;
    private JPanel panelAggUsuario;

    /**
     * Constructor de la clase AgregarUsuarios.
     * Crea una instancia de la ventana para agregar usuarios.
     */
    public AgregarUsuarios() {
        super("Menú Gerente");
        setContentPane(panelAggUsuario);
        setUndecorated(true);
        AgregarRolesCombo();

        regresarAlMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu_gerente gerente = new Menu_gerente();
                gerente.abrirMenuGerente();
                dispose();
            }
        });

        agregarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuarios();
            }
        });
    }

    /**
     * Método para abrir la ventana de agregar usuarios.
     * La ventana se abre con un tamaño predeterminado y se coloca en el centro de la pantalla.
     */
    void abrirAggUsuario() {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Método para agregar los roles disponibles al JComboBox de roles.
     * Obtiene los roles disponibles desde la base de datos y los añade al combo box.
     * En caso de error, muestra un mensaje de error al usuario.
     */
    void AgregarRolesCombo() {
        ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
        try (Connection connection = manejadorMySQL.conexionMySQL()) {
            String query = "SELECT nombre FROM Rol"; // Seleccionar solo el nombre del rol
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String nombreRol = resultSet.getString("nombre");
                        rolCombo.addItem(nombreRol);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles desde la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para registrar un nuevo usuario en la base de datos.
     * Obtiene los datos ingresados por el usuario y los registra en la base de datos.
     * Muestra un mensaje de confirmación o error al usuario.
     */
    void registrarUsuarios() {
        // Obtener los datos ingresados por el usuario
        String usuario = usuarioTxt.getText();
        String contrasena = new String(contrasenaTxt.getPassword());
        String rol = (String) rolCombo.getSelectedItem();

        // Verificar si se han ingresado todos los campos
        if (usuario.isEmpty() || contrasena.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registrar el usuario en la base de datos
        ManejadorMySQL conexionSQL = new ManejadorMySQL();
        try (Connection conexion = conexionSQL.conexionMySQL()) {
            String query = "INSERT INTO Usuario (usuario, contrasena, id_rol) SELECT ?, ?, id FROM Rol WHERE nombre = ?";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
                preparedStatement.setString(1, usuario);
                preparedStatement.setString(2, contrasena);
                preparedStatement.setString(3, rol);

                int filasInsertadas = preparedStatement.executeUpdate();
                if (filasInsertadas > 0) {
                    JOptionPane.showMessageDialog(this, "Usuario registrado", "Información", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario no registrado", "Error", JOptionPane.ERROR_MESSAGE);
                    limpiarCampos();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta SQL \n " + e, "Error", JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
        }
    }

    /**
     * Método para limpiar los campos de entrada.
     * Limpia los campos de usuario y contraseña después de realizar el registro.
     */
    private void limpiarCampos() {
        usuarioTxt.setText("");
        contrasenaTxt.setText("");
    }
}
