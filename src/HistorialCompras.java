import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Ventana para revisar el historial de compras, destinada al administrador.
 * Muestra información sobre los productos más vendidos.
 * @author  Alan Rios
 */
public class HistorialCompras extends JFrame{
    private JTable ventasTabla;
    private JButton menuButton;
    private JPanel panelHistorialCompras;

    /**
     * Constructor de la clase HistorialCompras.
     * Inicializa la ventana y muestra el historial de compras al abrirse.
     */
    public HistorialCompras() {
        super("Historial de compras");
        setContentPane(panelHistorialCompras);
        setUndecorated(true);
        mostrarCompras();
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre el menú del gerente y cierra esta ventana
                Menu_gerente cliente= new Menu_gerente();
                cliente.abrirMenuGerente();
                dispose();
            }
        });
    }

    /**
     * Método para mostrar el historial de compras en la tabla.
     * Obtiene los datos de las compras y los muestra en la tabla.
     */
    void mostrarCompras() {
        try {
            // Obtiene el modelo de la tabla con los datos de las compras
            DefaultTableModel model = obtenerComprasTableModel();
            ventasTabla.setModel(model); // Establece el modelo en la tabla

            // Muestra un mensaje si no hay compras disponibles
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay productos disponibles", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para obtener el modelo de la tabla con los datos de las compras.
     * @return El modelo de la tabla con los datos de las compras.
     * @throws SQLException Si hay un error al conectarse a la base de datos o al recuperar los datos.
     */
    DefaultTableModel obtenerComprasTableModel() throws SQLException {
        DefaultTableModel model = new DefaultTableModel(); // Modelo de la tabla
        model.addColumn("Cajeros");
        model.addColumn("Numero de transacciones");
        model.addColumn("Total de ventas");

        // Establece la conexión con la base de datos
        ManejadorMySQL conexionSQL = new ManejadorMySQL();
        Connection conexion = conexionSQL.conexionMySQL();

        String sql = "SELECT u.usuario AS Usuario, COUNT(t.id) AS Num_Transacciones, IFNULL(SUM(v.total), 0) AS Total_Ventas FROM Usuario u LEFT JOIN Transaccion t ON u.id = t.id_usuario LEFT JOIN Venta v ON u.id = v.id_cajero WHERE u.id_rol = 2 GROUP BY u.usuario;";
        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            // Agrega la primera fila como encabezado
            model.addRow(new Object[]{"Usuario", "Num_Transacciones", "Total_Ventas"});

            // Agrega los datos de las compras a la tabla
            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getString("Usuario"), // Nombre del cajero
                        resultSet.getInt("Num_Transacciones"), // Número de transacciones
                        "$" + resultSet.getFloat("Total_Ventas") // Total de ventas
                });
            }
        }

        // Cierra la conexión con la base de datos
        conexion.close();

        return model; // Retorna el modelo de la tabla con los datos de las compras
    }

    /**
     * Método para abrir la ventana de historial de compras.
     * Establece el tamaño, la ubicación y la visibilidad de la ventana.
     */
    void abrirHistorialCompraa() {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}