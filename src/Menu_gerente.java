import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase que representa el menú principal para el gerente.
 * @author  Sebastian Coronado
 */

/*En el panel de menú del gerente , el gerente puede agregar productos y usuarios para el ingreso
* al login , también se le permite agregar un producto nuevo a la depensa*/
public class Menu_gerente extends JFrame {
    private JButton agregarProductosButton;
    private JButton salirButton;
    private JPanel panelGerente;
    private JButton verStockButton;
    private JButton cerrarSesionButton;
    private JButton agregarProductoNuevoButton;
    private JButton agregarUsuariosNuevosButton;
    private JButton historialComprasButton;

    /**
     * Constructor de la clase Menu_gerente.
     */
    public Menu_gerente(){
        super("Menu Gerente");
        setContentPane(panelGerente);
        setUndecorated(true);

        // Acción para el botón de agregar productos
        agregarProductosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Agregar_productos productos = new Agregar_productos();
                productos.abrirAgregarProducto();
                dispose();
            }
        });

        // Acción para el botón de agregar nuevo producto
        agregarProductoNuevoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Agregar_producto_nuevo agregarProductoNuevo = new Agregar_producto_nuevo();
                agregarProductoNuevo.abrirProductoNuevo();
                dispose();
            }
        });

        // Acción para el botón de ver el stock
        verStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ver_stock stock = new Ver_stock();
                stock.abrirVerStock();
                dispose();
            }
        });

        // Acción para el botón de agregar nuevos usuarios
        agregarUsuariosNuevosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AgregarUsuarios aggUsuario = new AgregarUsuarios();
                aggUsuario.abrirAggUsuario();
                dispose();
            }
        });

        // Acción para el botón de cerrar sesión
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                login.abrirLogin();
                dispose();
            }
        });

        // Acción para el botón de salir
        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Menu_gerente.this, "Saliendo de la app", "Información", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        });
        historialComprasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HistorialCompras historialCompras = new HistorialCompras();
                historialCompras.abrirHistorialCompraa();
                dispose();
            }
        });
    }

    /**
     * Método para abrir el menú del gerente.
     */
    void abrirMenuGerente(){
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
