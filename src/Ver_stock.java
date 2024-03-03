import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Clase para la ventana de visualización de stock de productos.
 * @author  Alan Rios
 */
public class Ver_stock extends JFrame {
    private JTable productosTabla;
    private JPanel panelVerStock;
    private JButton menuButton;

    /**
     * Constructor de la clase Ver_stock.
     */
    public Ver_stock() {
        super("Ver Stock"); // Título de la ventana
        setContentPane(panelVerStock); // Establece el panel como contenido principal
        setUndecorated(true); // Elimina la decoración de la ventana

        mostrarProductos(); // Muestra los productos al iniciar la ventana

        // Acción para el botón del menú
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre el menú del gerente y cierra esta ventana
                Menu_gerente gerente = new Menu_gerente();
                gerente.abrirMenuGerente();
                dispose();
            }
        });
    }

    /**
     * Método para mostrar los productos en la tabla.
     */
    void mostrarProductos() {
        try {
            // Obtiene el modelo de la tabla con los datos de los productos
            DefaultTableModel model = obtenerProductosTableModel();
            productosTabla.setModel(model); // Establece el modelo en la tabla

            // Muestra un mensaje si no hay productos disponibles
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay productos disponibles", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para obtener el modelo de la tabla con los datos de los productos.
     */
    DefaultTableModel obtenerProductosTableModel() throws SQLException {
        DefaultTableModel model = new DefaultTableModel(); // Modelo de la tabla
        model.addColumn("Producto"); // Columna para el nombre del producto
        model.addColumn("Stock"); // Columna para el stock
        model.addColumn("Precio"); // Columna para el precio

        // Establece la conexión con la base de datos
        ManejadorMySQL conexionSQL = new ManejadorMySQL();
        Connection conexion = conexionSQL.conexionMySQL();

        // Consulta SQL para obtener los productos con su stock y precio
        String sql = "SELECT nombre AS Producto, stock AS Stock, precio AS Precio FROM Producto";
        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Agrega la primera fila como encabezado
            model.addRow(new Object[]{"Producto", "Stock", "Precio"});

            // Agrega los datos de los productos a la tabla
            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getString("Producto"), // Nombre del producto
                        resultSet.getInt("Stock"), // Stock del producto
                        "$" + resultSet.getFloat("Precio") // Precio del producto
                });
            }
        }

        // Cierra la conexión con la base de datos
        conexion.close();

        return model; // Retorna el modelo de la tabla con los datos de los productos
    }

    /**
     * Método para abrir la ventana de visualización de stock.
     */
    void abrirVerStock() {
        setSize(400, 400); // Tamaño de la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setVisible(true); // Hace visible la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
    }
}
