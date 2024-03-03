import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * En este apartado se visualizan las opciones que el cliente tiene para navegar en el sistema
 * @author  Sebastian Coronado;
 */
public class Menu_cliente extends JFrame {
    private JButton comprarButton;
    private JButton salirButton;
    private JPanel Panel3;
    private JButton cerrarSesionButton;
    private JButton imprimirButton;
    private Document document;

    /**
     * El método constructor del menú de cliente permite visualizar la interfaz del sistema
     */
    public Menu_cliente() {
        super("Menu Cliente");
        setContentPane(Panel3);
        setUndecorated(true);


        comprarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id_usuario = obtenerIdUsuarioMaximo();
                crearTransaccion(id_usuario);
                Comprar comprar = new Comprar();
                comprar.abrirComprar();
                dispose();
            }
        });

        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                login.abrirLogin();
                dispose();
            }
        });

        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Menu_cliente.this, "Saliendo de la app");
                dispose();
            }
        });
        /**
         * Boton que permite generar el reporte de ventas extraido de la base de datos
         * @autor Eduardo Porras
         */
        imprimirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Document document = new Document();
                    String dest = "registro de ventas.pdf";
                    PdfWriter.getInstance(document, new FileOutputStream(dest));
                    document.open();

                    agregarTablaUsuarios(document);
                    agregarTablaVentas(document);
                    agregarTablaTransacciones(document);
                    agregarTablaProductos(document);
                    agregarTablaDetalleTransaccion(document);

                    document.close();

                    System.out.println("Informe PDF creado: " + dest);

                    // Abre el archivo PDF después de crearlo
                    abrirArchivoPDF(dest);

                } catch (DocumentException | FileNotFoundException ex) {
                    Logger.getLogger(Menu_cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    private void agregarTablaUsuarios(Document document) throws DocumentException {
        try {
            // Conexion a la base de datos y obtencion de los datos de la tabla Usuario
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            String query = "SELECT * FROM Usuario";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Crea la tabla PDF y agrega las columnas
            PdfPTable table = new PdfPTable(3); // Ajusta el número de columnas según la estructura de tu tabla

            // Añadir encabezados a la tabla
            table.addCell("ID");
            table.addCell("Usuario");
            table.addCell("ID Rol");

            // Añadir filas a la tabla con la información de la tabla Usuario
            while (resultSet.next()) {
                table.addCell(String.valueOf(resultSet.getInt("id")));
                table.addCell(resultSet.getString("usuario"));
                table.addCell(String.valueOf(resultSet.getInt("id_rol")));
            }

            // Cierra las conexiones
            resultSet.close();
            preparedStatement.close();
            conexion.close();

            // Agrega la tabla al documento
            document.add(new Paragraph("Tabla Usuario"));
            document.add(new Paragraph(" "));
            document.add(table);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + ex.getMessage());
        }
    }

    private void agregarTablaVentas(Document document) throws DocumentException {

        try {
            // Conéctate a la base de datos y obtén los datos de la tabla Venta
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            String query = "SELECT * FROM Venta";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Crea la tabla PDF y agrega las columnas
            PdfPTable table = new PdfPTable(4); // Ajusta el número de columnas según la estructura de tu tabla

            // Añade encabezados a la tabla
            table.addCell("ID");
            table.addCell("ID Cajero");
            table.addCell("Fecha");
            table.addCell("Total");

            // Añade filas a la tabla con la información de la tabla Venta
            while (resultSet.next()) {
                table.addCell(String.valueOf(resultSet.getInt("id")));
                table.addCell(String.valueOf(resultSet.getInt("id_cajero")));
                table.addCell(resultSet.getTimestamp("fecha").toString());
                table.addCell(String.valueOf(resultSet.getDouble("total")));
            }

            // Cierra las conexiones
            resultSet.close();
            preparedStatement.close();
            conexion.close();

            // Agrega la tabla al documento
            document.add(new Paragraph("Tabla Venta"));
            document.add(new Paragraph(" "));
            document.add(table);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void agregarTablaTransacciones(Document document) throws DocumentException {
        try {
            // Conéctate a la base de datos y obtén los datos de la tabla Transaccion
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            String query = "SELECT * FROM Transaccion";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Crea la tabla PDF y agrega las columnas
            PdfPTable table = new PdfPTable(3); // Ajusta el número de columnas según la estructura de tu tabla

            // Añade encabezados a la tabla
            table.addCell("ID");
            table.addCell("ID Usuario");
            table.addCell("Fecha");

            // Añade filas a la tabla con la información de la tabla Transaccion
            while (resultSet.next()) {
                table.addCell(String.valueOf(resultSet.getInt("id")));
                table.addCell(String.valueOf(resultSet.getInt("id_usuario")));
                table.addCell(resultSet.getTimestamp("fecha").toString());
            }

            // Cierra las conexiones
            resultSet.close();
            preparedStatement.close();
            conexion.close();

            // Agrega la tabla al documento
            document.add(new Paragraph("Tabla Transaccion"));
            document.add(new Paragraph(" "));
            document.add(table);

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Manejo de excepciones de la base de datos
        }
    }

    private void agregarTablaProductos(Document document) throws DocumentException {
        try {
            // Conéctate a la base de datos y obtén los datos de la tabla Producto
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            String query = "SELECT * FROM Producto";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Crea la tabla PDF y agrega las columnas
            PdfPTable table = new PdfPTable(4); // Ajusta el número de columnas según la estructura de tu tabla

            // Añade encabezados a la tabla
            table.addCell("ID");
            table.addCell("Nombre");
            table.addCell("Precio");
            table.addCell("Stock");

            // Añade filas a la tabla con la información de la tabla Producto
            while (resultSet.next()) {
                table.addCell(String.valueOf(resultSet.getInt("id")));
                table.addCell(resultSet.getString("nombre"));
                table.addCell(String.valueOf(resultSet.getDouble("precio")));
                table.addCell(String.valueOf(resultSet.getInt("stock")));
            }

            // Cierra las conexiones
            resultSet.close();
            preparedStatement.close();
            conexion.close();

            // Agrega la tabla al documento
            document.add(new Paragraph("Tabla Producto"));
            document.add(new Paragraph(" "));
            document.add(table);

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Manejo de excepciones de la base de datos
        }
    }

    private void agregarTablaDetalleTransaccion(Document document) throws DocumentException {
        try {
            // Conéctate a la base de datos y obtén los datos de la tabla Detalle_Transaccion
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            String query = "SELECT * FROM Detalle_Transaccion";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Crea la tabla PDF y agrega las columnas
            PdfPTable table = new PdfPTable(4); // Ajusta el número de columnas según la estructura de tu tabla

            // Añade encabezados a la tabla
            table.addCell("ID");
            table.addCell("ID Transaccion");
            table.addCell("ID Producto");
            table.addCell("Cantidad");

            // Añade filas a la tabla con la información de la tabla Detalle_Transaccion
            while (resultSet.next()) {
                table.addCell(String.valueOf(resultSet.getInt("id")));
                table.addCell(String.valueOf(resultSet.getInt("id_transaccion")));
                table.addCell(String.valueOf(resultSet.getInt("id_producto")));
                table.addCell(String.valueOf(resultSet.getInt("cantidad")));
            }

            // Cierra las conexiones
            resultSet.close();
            preparedStatement.close();
            conexion.close();

            // Agrega la tabla al documento
            document.add(new Paragraph("Tabla Detalle_Transaccion"));
            document.add(new Paragraph(" "));
            document.add(table);

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Manejo de excepciones de la base de datos
        }
    }

    private void abrirArchivoPDF(String filePath) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(new File(filePath));
                } else {
                    System.out.println("El entorno de escritorio no es compatible con la apertura automática del archivo PDF.");
                }
            } catch (IOException ex) {
                Logger.getLogger(Menu_cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    /**
     * Método void para abrir el menú del cliente
     */
    void abrirMenuCliente(){
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
/**
 *El método crearTransaccion permite el registro de compras en la base de datos por el usuario logueado
 * @param id_usuario
 */
    void crearTransaccion(int id_usuario){
        try{
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            String query = "INSERT INTO  Transaccion (id_usuario) VALUES (?)";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);

            preparedStatement.setInt(1, id_usuario); // Establecer el ID del usuario

            int filasAfectadas = preparedStatement.executeUpdate();
            if(filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Transacción creada con éxito.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo crear la transacción.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            preparedStatement.close();
            conexion.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta SQL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * El método obtenerIdUsuarioMaximo pemite obtener el id máximo del cajero
     * @return
     */

    int obtenerIdUsuarioMaximo() {
        int idUsuario = 0;
        try {
            ManejadorMySQL manejadorMySQL = new ManejadorMySQL();
            Connection conexion = manejadorMySQL.conexionMySQL();

            String query = "SELECT id_cajero FROM HistorialSesion WHERE id = (SELECT MAX(id) FROM HistorialSesion)";
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                idUsuario = resultSet.getInt("id_cajero");
            }

            resultSet.close();
            preparedStatement.close();
            conexion.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener el ID del usuario máximo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return idUsuario;
    }
}
