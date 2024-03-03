import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase para la ventana de agregar nuevos productos.
 * @author  Alan Rios
 */
public class Agregar_producto_nuevo extends JFrame {
    private JTextField productoTxt;
    private JTextField precioTxt;
    private JTextField stockTxt;
    private JTextField imagenTxt;
    private JButton regresarAlMenúButton;
    private JButton ingresarProductoButton;
    private JPanel panelProductoNuevo;

    /**
     * Constructor de la clase Agregar_producto_nuevo.
     */
    public Agregar_producto_nuevo() {
        super("Agregar Productos Nuevo");
        setContentPane(panelProductoNuevo);
        setUndecorated(true);

        // Acción para el botón de regresar al menú
        regresarAlMenúButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir el menú gerente y cerrar esta ventana
                new Menu_gerente().abrirMenuGerente();
                dispose();
            }
        });

        // Acción para el botón de ingresar producto
        ingresarProductoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarProductosNuevos();
            }
        });
    }

    /**
     * Método para abrir la ventana de agregar nuevos productos.
     */
    void abrirProductoNuevo() {
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Método para registrar nuevos productos en la base de datos.
     */
    void registrarProductosNuevos() {
        try {
            String nombre = productoTxt.getText();
            float precio = Float.parseFloat(precioTxt.getText()); // Se espera un valor numérico para el precio
            int stock = Integer.parseInt(stockTxt.getText()); // Se espera un valor numérico para el stock
            String imagen = imagenTxt.getText();

            ManejadorMySQL conexionSQL = new ManejadorMySQL();
            Connection conexion = conexionSQL.conexionMySQL();

            String query = "INSERT INTO Producto (nombre, precio, stock, imagen) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setString(1, nombre);
            preparedStatement.setFloat(2, precio);
            preparedStatement.setInt(3, stock);
            preparedStatement.setString(4, imagen);

            int filasInsertadas = preparedStatement.executeUpdate();

            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(this, "Producto nuevo registrado", "Información", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Producto nuevo no registrado", "Error", JOptionPane.ERROR_MESSAGE);
                limpiarCampos();
            }

            preparedStatement.close();
            conexion.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos para el precio y el stock", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para limpiar los campos de entrada.
     */
    private void limpiarCampos() {
        productoTxt.setText("");
        precioTxt.setText("");
        stockTxt.setText("");
        imagenTxt.setText("");
    }
}
