import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase para la ventana de agregar productos y actualizar el stock en la base de datos.
 * @author  Alan Rios
 */
public class Agregar_productos extends JFrame {
    private JComboBox<String> comboProducto;
    private JPanel Panel4;
    private JTextField cantidadProducto;
    private JButton regresarAlMenuButton;
    private JButton ingresarProductosButton;

    /**
     * Constructor de la clase Agregar_productos.
     */
    public Agregar_productos() {
        super("Agregar Stock");
        setContentPane(Panel4);
        setUndecorated(true);

        // Llenar el JComboBox con los nombres de los productos
        llenarComboProductos();

        // Acción para el botón de ingresar productos
        ingresarProductosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtener el nombre del producto seleccionado y la cantidad ingresada
                String nombreProducto = (String) comboProducto.getSelectedItem();
                int cantidad = Integer.parseInt(cantidadProducto.getText());
                try {
                    // Llamar al método para agregar stock
                    agregarStock(nombreProducto, cantidad);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Agregar_productos.this, "Error al agregar stock: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción para el botón de regresar al menú gerente
        regresarAlMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir el menú gerente y cerrar esta ventana
                Menu_gerente gerente = new Menu_gerente();
                gerente.abrirMenuGerente();
                dispose();
            }
        });
    }

    /**
     * Método para abrir la ventana de agregar productos.
     */
    void abrirAgregarProducto() {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Método para llenar el JComboBox con los nombres de los productos desde la base de datos.
     */
    void llenarComboProductos() {
        try {
            // Conexión a la base de datos para obtener los nombres de los productos
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();
            String query = "SELECT nombre FROM Producto";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                // Agregar cada nombre de producto al JComboBox
                String nombreProducto = resultSet.getString("nombre");
                comboProducto.addItem(nombreProducto);
            }
            resultSet.close();
            preparedStatement.close();
            conexion.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener los nombres de los productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para agregar stock a un producto en la base de datos.
     * @param nombreProducto Nombre del producto.
     * @param cantidad Cantidad de stock a agregar.
     * @throws SQLException Si hay un error al ejecutar la consulta SQL.
     */
    void agregarStock(String nombreProducto, int cantidad) throws SQLException {
        // Conexión a la base de datos y actualización del stock del producto
        ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
        Connection conexion = manejadorMySQL.conexionMySQL();
        String query = "UPDATE Producto SET stock = stock + ? WHERE nombre = ?";
        PreparedStatement preparedStatement = conexion.prepareStatement(query);
        preparedStatement.setInt(1, cantidad);
        preparedStatement.setString(2, nombreProducto);
        int filasActualizadas = preparedStatement.executeUpdate();
        if (filasActualizadas > 0) {
            JOptionPane.showMessageDialog(this, "Stock actualizado", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el stock", "Error", JOptionPane.ERROR_MESSAGE);
        }
        preparedStatement.close();
        conexion.close();
    }

    /**
     * Método para limpiar los campos de entrada.
     */
    private void limpiarCampos() {
        // Limpiar el campo de cantidad
        cantidadProducto.setText("");
    }
}
