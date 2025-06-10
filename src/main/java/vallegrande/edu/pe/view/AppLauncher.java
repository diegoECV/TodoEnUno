package vallegrande.edu.pe.view;

import vallegrande.edu.pe.model.ClienteDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AppLauncher {
public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
        try {
        // Establecer el Look and Feel del sistema
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        e.printStackTrace();
            }

JFrame frame = new JFrame("Registro de Clientes - vallegrande.edu.pe");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 600); // Aumentado el ancho para acomodar todas las columnas
            frame.setLocationRelativeTo(null);

String[] columnas = {"ID", "Nombre", "Apellido", "Edad", "Fecha Nacimiento", "Sexo", "Prefijo", "Teléfono", "Correo", "Tipo Doc", "N° Doc", "Términos", "Hora Registro", "Estado"};
DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
@Override
public boolean isCellEditable(int row, int column) {
        return false; // Hacer que la tabla no sea editable
        }
        };

JTable tabla = new JTable(modelo);
            tabla.getTableHeader().setReorderingAllowed(false); // Evitar que se reordenen las columnas
            tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo permitir seleccionar una fila

// Ajustar el ancho de las columnas
            tabla.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
            tabla.getColumnModel().getColumn(1).setPreferredWidth(100); // Nombre
            tabla.getColumnModel().getColumn(2).setPreferredWidth(100); // Apellido
            tabla.getColumnModel().getColumn(3).setPreferredWidth(50); // Edad
            tabla.getColumnModel().getColumn(4).setPreferredWidth(120); // FechaNacimiento
            tabla.getColumnModel().getColumn(5).setPreferredWidth(80); // Sexo
            tabla.getColumnModel().getColumn(6).setPreferredWidth(60); // Prefijo
            tabla.getColumnModel().getColumn(7).setPreferredWidth(100); // Teléfono
            tabla.getColumnModel().getColumn(8).setPreferredWidth(150); // Correo
            tabla.getColumnModel().getColumn(9).setPreferredWidth(80); // Tipo Doc
            tabla.getColumnModel().getColumn(10).setPreferredWidth(100); // N° Doc
            tabla.getColumnModel().getColumn(11).setPreferredWidth(70); // Términos
            tabla.getColumnModel().getColumn(12).setPreferredWidth(150); // Hora Registro
            tabla.getColumnModel().getColumn(13).setPreferredWidth(70); // Estado

JScrollPane scrollPane = new JScrollPane(tabla);

ClienteView clienteView = new ClienteView(modelo);

JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(clienteView.getPanel(tabla), BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

// Añadir un panel de búsqueda en la parte superior
JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
JTextField searchField = new JTextField(20);
JButton searchButton = new JButton("Buscar");
JButton refreshButton = new JButton("Actualizar Lista");

            searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(searchField);
            searchPanel.add(searchButton);
            searchPanel.add(refreshButton);

            mainPanel.add(searchPanel, BorderLayout.NORTH);

// Implementar la funcionalidad de búsqueda (puedes expandir esto más tarde)
            searchButton.addActionListener(e -> {
        // Aquí iría la lógica de búsqueda
        JOptionPane.showMessageDialog(frame, "Funcionalidad de búsqueda en desarrollo");
            });

                    refreshButton.addActionListener(e -> {
        ClienteDAO.actualizarTabla();
            });

                    frame.add(mainPanel);
            frame.setVisible(true);

// Cargar datos iniciales
            ClienteDAO.actualizarTabla();
        });
                }
                }
