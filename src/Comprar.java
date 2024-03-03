import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Clase para permitir al cliente realizar compras de productos registrados en la base de datos.
 * Permite al cliente agregar productos al carrito y finalizar la compra.
 * @author  Alan Rios
 */
public class Comprar extends JFrame {
    private JComboBox<String> productoCombo;
    private JTextField cantidadTxt;
    private JPanel panelComprar;
    private JButton ingresarProductoAlCarritoButton;
    private JButton finalizarCompraButton;

    /**
     * Constructor de la clase Comprar.
     * Crea una instancia de la ventana de compras.
     */
    public Comprar() {
        super("Compras");
        setContentPane(panelComprar);
        setUndecorated(true);

        llenarComboProductos();

        ingresarProductoAlCarritoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreProducto = (String) productoCombo.getSelectedItem();
                int cantidad = Integer.parseInt(cantidadTxt.getText());
                agregarCarrito(nombreProducto, cantidad);
            }
        });

        finalizarCompraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalizarCompra();
                Menu_cliente menuCliente = new Menu_cliente();
                menuCliente.abrirMenuCliente();
                dispose();
            }
        });
    }

    /**
     * Método para abrir la ventana de compras.
     * Configura el tamaño y la ubicación de la ventana.
     */
    void abrirComprar() {
        setSize(480, 480);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Método para agregar un producto al carrito.
     * Registra el producto seleccionado por el cliente en el carrito de compras.
     * @param nombreProducto El nombre del producto a agregar al carrito.
     * @param cantidad La cantidad del producto a agregar al carrito.
     */
    void agregarCarrito(String nombreProducto, int cantidad) {
        try {
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            // Obtener el ID del producto
            String queryIdProducto = "SELECT id FROM Producto WHERE nombre = ?";
            try (PreparedStatement preparedStatementIdProducto = conexion.prepareStatement(queryIdProducto)) {
                preparedStatementIdProducto.setString(1, nombreProducto);
                try (ResultSet resultSetIdProducto = preparedStatementIdProducto.executeQuery()) {
                    if (resultSetIdProducto.next()) {
                        int idProducto = resultSetIdProducto.getInt("id");

                        // Insertar en la tabla Detalle_Transaccion
                        String queryInsert = "INSERT INTO Detalle_Transaccion(id_transaccion, id_producto, cantidad) VALUES ((SELECT MAX(id) FROM Transaccion), ?, ?)";
                        try (PreparedStatement preparedStatement = conexion.prepareStatement(queryInsert)) {
                            preparedStatement.setInt(1, idProducto);
                            preparedStatement.setInt(2, cantidad);
                            int filasActualizadas = preparedStatement.executeUpdate();
                            if (filasActualizadas > 0) {
                                JOptionPane.showMessageDialog(Comprar.this, "Producto agregado al carrito", "Información", JOptionPane.INFORMATION_MESSAGE);
                                limpiarCampos();
                            } else {
                                JOptionPane.showMessageDialog(Comprar.this, "Producto no agregado al carrito", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(Comprar.this, "No se encontró el producto", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Comprar.this, "Error al agregar el producto al carrito: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para finalizar la compra.
     * Registra la compra realizada por el cliente en la base de datos.
     */
    void finalizarCompra() {
        try {
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            // Calcular el total de la compra
            String queryTotal = "SELECT SUM(precio * cantidad) AS total FROM Producto JOIN Detalle_Transaccion ON Producto.id = Detalle_Transaccion.id_producto";
            try (PreparedStatement preparedStatementTotal = conexion.prepareStatement(queryTotal)) {
                try (ResultSet resultSetTotal = preparedStatementTotal.executeQuery()) {
                    double total = 0;
                    if (resultSetTotal.next()) {
                        total = resultSetTotal.getDouble("total");
                    }

                    // Insertar en la tabla Venta
                    String queryInsertVenta = "INSERT INTO Venta (id_cajero, total) VALUES (?, ?)";
                    try (PreparedStatement preparedStatementVenta = conexion.prepareStatement(queryInsertVenta, Statement.RETURN_GENERATED_KEYS)) {
                        int idCajero = 1; // ID del cajero debe ser proporcionado
                        preparedStatementVenta.setInt(1, idCajero);
                        preparedStatementVenta.setDouble(2, total);
                        int filasInsertadas = preparedStatementVenta.executeUpdate();
                        if (filasInsertadas > 0) {
                            JOptionPane.showMessageDialog(Comprar.this, "Compra finalizada. Total: " + total, "Información", JOptionPane.INFORMATION_MESSAGE);
                            ResultSet generatedKeys = preparedStatementVenta.getGeneratedKeys();
                            int idVenta = -1;
                            if (generatedKeys.next()) {
                                idVenta = generatedKeys.getInt(1);
                            }
                            // Actualizar el stock de los productos
                            String queryUpdateStock = "UPDATE Producto SET stock = stock - (SELECT cantidad FROM Detalle_Transaccion WHERE id_producto = Producto.id AND id_transaccion = ?)";
                            try (PreparedStatement preparedStatementUpdateStock = conexion.prepareStatement(queryUpdateStock)) {
                                preparedStatementUpdateStock.setInt(1, idVenta);
                                preparedStatementUpdateStock.executeUpdate();
                            }
                        } else {
                            JOptionPane.showMessageDialog(Comprar.this, "Error al finalizar la compra", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Comprar.this, "Error al finalizar la compra: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para llenar el combo box de productos disponibles para comprar.
     * Recupera los nombres de los productos desde la base de datos y los agrega al combo box.
     */
    void llenarComboProductos() {
        try {
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();
            String query = "SELECT nombre FROM Producto";
            try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String nombreProducto = resultSet.getString("nombre");
                        productoCombo.addItem(nombreProducto);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener los nombres de los productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para limpiar los campos después de agregar un producto al carrito.
     * Borra el texto del campo de cantidad.
     */
    private void limpiarCampos() {
        cantidadTxt.setText("");
    }
}
