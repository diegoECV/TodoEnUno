package vallegrande.edu.pe.view;

import vallegrande.edu.pe.controller.ClienteController;
import vallegrande.edu.pe.model.Cliente;
import vallegrande.edu.pe.model.ClienteDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException; // Import SQLException
import java.time.LocalDate; // Import LocalDate
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class ClienteView {
    private JPanel panel;
    private JTextField textNombre, textApellido, textEdad, textFechaNacimiento, textTelefono, textDireccion, textEmail, textNumeroDocumento;
    private JComboBox<String> comboSexo, comboPrefijo, comboTipoDocumento;
    private JCheckBox checkAceptaTerminos;
    private ClienteController controller;
    private JTable tabla;
    private List<Cliente> listaClientes; // Para mantener referencia a los clientes actuales

    // Patrones para validación
    private static final Pattern SOLO_LETRAS = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    private static final Pattern SOLO_NUMEROS = Pattern.compile("^[0-9]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    private static final Pattern FECHA_PATTERN = Pattern.compile(
            "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/(19|20)\\d\\d$");
    private static final Pattern DNI_PATTERN = Pattern.compile("^\\d{8}$"); // 8 dígitos para DNI peruano
    private static final Pattern CEDULA_PATTERN = Pattern.compile("^\\d{10}$"); // 10 dígitos para cédula
    private static final Pattern PASAPORTE_PATTERN = Pattern.compile("^[A-Z0-9]{6,12}$"); // Alfanumérico para pasaporte

    // Date formatter for UI display (DD/MM/YYYY)
    private static final DateTimeFormatter UI_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ClienteView(DefaultTableModel modelo) {
        controller = new ClienteController();
        ClienteDAO.setModeloTabla(modelo);

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Inicializar componentes
        textNombre = new JTextField();
        textApellido = new JTextField();
        textEdad = new JTextField();
        textFechaNacimiento = new JTextField();
        textTelefono = new JTextField();
        textDireccion = new JTextField();
        textEmail = new JTextField();
        textNumeroDocumento = new JTextField();

        comboSexo = new JComboBox<>(new String[]{"Masculino", "Femenino", "Otro"});
        comboPrefijo = new JComboBox<>(new String[]{"+51", "+001", "+002"});
        comboTipoDocumento = new JComboBox<>(new String[]{"DNI", "Cédula", "Pasaporte"});
        checkAceptaTerminos = new JCheckBox("Sí, acepto los términos y condiciones");

        // Aplicar validaciones en tiempo real
        aplicarValidaciones();

        // Crear botones
        JButton botonRegistrar = new JButton("Registrar");
        JButton botonActualizar = new JButton("Actualizar");
        JButton botonEliminar = new JButton("Eliminar");
        JButton botonLimpiar = new JButton("Limpiar Campos");

        // Añadir tooltips a los botones
        botonRegistrar.setToolTipText("Registrar un nuevo cliente");
        botonActualizar.setToolTipText("Actualizar el cliente seleccionado");
        botonEliminar.setToolTipText("Eliminar el cliente seleccionado");
        botonLimpiar.setToolTipText("Limpiar todos los campos del formulario");

        // Configurar acciones de los botones
        botonRegistrar.addActionListener(e -> procesarFormulario(-1));
        botonActualizar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                // Ensure listaClientes is up-to-date before getting ID
                actualizarTabla(); // Re-fetch to ensure correct ID mapping
                if (fila < listaClientes.size()) { // Check if row is still valid after refresh
                    int clienteId = listaClientes.get(fila).getId();
                    procesarFormulario(clienteId);
                } else {
                    JOptionPane.showMessageDialog(null, "La fila seleccionada ya no es válida. Por favor, re-seleccione.",
                            "Error de selección", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione un cliente para actualizar.",
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
            }
        });
        botonEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int opcion = JOptionPane.showConfirmDialog(null,
                        "¿Está seguro de que desea eliminar este cliente?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    // Ensure listaClientes is up-to-date before getting ID
                    actualizarTabla(); // Re-fetch to ensure correct ID mapping
                    if (fila < listaClientes.size()) { // Check if row is still valid after refresh
                        int clienteId = listaClientes.get(fila).getId();
                        try {
                            controller.eliminarCliente(clienteId);
                            JOptionPane.showMessageDialog(null, "Cliente eliminado lógicamente.", "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
                            limpiarCampos(); // Clear fields after deletion
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al eliminar cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        actualizarTabla(); // Refresh table after operation
                    } else {
                        JOptionPane.showMessageDialog(null, "La fila seleccionada ya no es válida. Por favor, re-seleccione.",
                                "Error de selección", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione un cliente para eliminar.",
                        "Selección requerida", JOptionPane.WARNING_MESSAGE);
            }
        });
        botonLimpiar.addActionListener(e -> limpiarCampos());

        // Añadir componentes al panel con etiquetas descriptivas
        panel.add(new JLabel("Nombre:*")); panel.add(textNombre);
        panel.add(new JLabel("Apellido:*")); panel.add(textApellido);
        panel.add(new JLabel("Edad:*")); panel.add(textEdad);
        panel.add(new JLabel("Fecha de Nacimiento (DD/MM/YYYY):*")); panel.add(textFechaNacimiento);
        panel.add(new JLabel("Sexo:*")); panel.add(comboSexo);
        panel.add(new JLabel("Prefijo:*")); panel.add(comboPrefijo);
        panel.add(new JLabel("Teléfono:*")); panel.add(textTelefono);
        panel.add(new JLabel("Dirección:*")); panel.add(textDireccion);
        panel.add(new JLabel("Correo Electrónico:*")); panel.add(textEmail);
        panel.add(new JLabel("Tipo de Documento:*")); panel.add(comboTipoDocumento);
        panel.add(new JLabel("Número de Documento:*")); panel.add(textNumeroDocumento);
        panel.add(new JLabel("Términos y Condiciones:*")); panel.add(checkAceptaTerminos);

        // Panel para los botones con FlowLayout
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botonesPanel.add(botonRegistrar);
        botonesPanel.add(botonActualizar);
        botonesPanel.add(botonEliminar);
        botonesPanel.add(botonLimpiar);

        // Añadir el panel de botones ocupando dos columnas
        panel.add(new JLabel("")); // Celda vacía para alinear
        panel.add(botonesPanel);

        // Añadir una nota sobre campos obligatorios
        JLabel notaObligatorios = new JLabel("* Campos obligatorios");
        notaObligatorios.setForeground(Color.RED);
        panel.add(notaObligatorios);

        // Cargar datos iniciales
        actualizarTabla();
    }

    private void aplicarValidaciones() {
        // Validación para Nombre (solo letras)
        textNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String texto = textField.getText();
                if (!texto.isEmpty() && !SOLO_LETRAS.matcher(texto).matches()) {
                    textField.setBackground(new Color(255, 200, 200));
                    textField.setToolTipText("Solo se permiten letras y espacios");
                } else {
                    textField.setBackground(Color.WHITE);
                    textField.setToolTipText(null);
                }
            }
        });

        // Validación para Apellido (solo letras)
        textApellido.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String texto = textField.getText();
                if (!texto.isEmpty() && !SOLO_LETRAS.matcher(texto).matches()) {
                    textField.setBackground(new Color(255, 200, 200));
                    textField.setToolTipText("Solo se permiten letras y espacios");
                } else {
                    textField.setBackground(Color.WHITE);
                    textField.setToolTipText(null);
                }
            }
        });

        // Validación para Edad (solo números y rango 1-120)
        textEdad.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String texto = textField.getText();
                try {
                    if (!texto.isEmpty()) {
                        if (!SOLO_NUMEROS.matcher(texto).matches()) {
                            textField.setBackground(new Color(255, 200, 200));
                            textField.setToolTipText("Solo se permiten números");
                        } else {
                            int edad = Integer.parseInt(texto);
                            if (edad < 1 || edad > 120) {
                                textField.setBackground(new Color(255, 200, 200));
                                textField.setToolTipText("La edad debe estar entre 1 y 120 años");
                            } else {
                                textField.setBackground(Color.WHITE);
                                textField.setToolTipText(null);
                            }
                        }
                    } else {
                        textField.setBackground(Color.WHITE);
                        textField.setToolTipText(null);
                    }
                } catch (NumberFormatException ex) {
                    textField.setBackground(new Color(255, 200, 200));
                    textField.setToolTipText("Formato de número inválido");
                }
            }
        });

        // Validación para Fecha de Nacimiento (formato DD/MM/YYYY)
        textFechaNacimiento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String texto = textField.getText();
                if (!texto.isEmpty()) {
                    if (!FECHA_PATTERN.matcher(texto).matches()) {
                        textField.setBackground(new Color(255, 200, 200));
                        textField.setToolTipText("Formato de fecha inválido. Use DD/MM/YYYY");
                    } else {
                        // Validar que sea una fecha válida y no futura
                        try {
                            LocalDate fechaNacimiento = LocalDate.parse(texto, UI_DATE_FORMATTER);
                            LocalDate hoy = LocalDate.now();

                            if (fechaNacimiento.isAfter(hoy)) {
                                textField.setBackground(new Color(255, 200, 200));
                                textField.setToolTipText("La fecha no puede ser futura");
                            } else {
                                textField.setBackground(Color.WHITE);
                                textField.setToolTipText(null);
                            }
                        } catch (DateTimeParseException ex) {
                            textField.setBackground(new Color(255, 200, 200));
                            textField.setToolTipText("Fecha inválida");
                        }
                    }
                } else {
                    textField.setBackground(Color.WHITE);
                    textField.setToolTipText(null);
                }
            }
        });

        // Validación para Teléfono (solo números y longitud adecuada)
        textTelefono.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String texto = textField.getText();
                if (!texto.isEmpty()) {
                    if (!SOLO_NUMEROS.matcher(texto).matches()) {
                        textField.setBackground(new Color(255, 200, 200));
                        textField.setToolTipText("Solo se permiten números");
                    } else if (texto.length() < 7 || texto.length() > 15) {
                        textField.setBackground(new Color(255, 200, 200));
                        textField.setToolTipText("El teléfono debe tener entre 7 y 15 dígitos");
                    } else {
                        textField.setBackground(Color.WHITE);
                        textField.setToolTipText(null);
                    }
                } else {
                    textField.setBackground(Color.WHITE);
                    textField.setToolTipText(null);
                }
            }
        });

        // Validación para Email
        textEmail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String texto = textField.getText();
                if (!texto.isEmpty() && !EMAIL_PATTERN.matcher(texto).matches()) {
                    textField.setBackground(new Color(255, 200, 200));
                    textField.setToolTipText("Formato de email inválido");
                } else {
                    textField.setBackground(Color.WHITE);
                    textField.setToolTipText(null);
                }
            }
        });

        // Validación para Número de Documento según tipo seleccionado
        textNumeroDocumento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarNumeroDocumento();
            }
        });

        comboTipoDocumento.addActionListener(e -> validarNumeroDocumento());
    }

    private void validarNumeroDocumento() {
        String tipoDoc = (String) comboTipoDocumento.getSelectedItem();
        String numDoc = textNumeroDocumento.getText();

        if (!numDoc.isEmpty()) {
            boolean valido = false;
            String mensaje = "";

            switch (tipoDoc) {
                case "DNI":
                    valido = DNI_PATTERN.matcher(numDoc).matches();
                    mensaje = "El DNI debe tener 8 dígitos numéricos";
                    break;
                case "Cédula":
                    valido = CEDULA_PATTERN.matcher(numDoc).matches();
                    mensaje = "La cédula debe tener 10 dígitos numéricos";
                    break;
                case "Pasaporte":
                    valido = PASAPORTE_PATTERN.matcher(numDoc).matches();
                    mensaje = "El pasaporte debe tener entre 6 y 12 caracteres alfanuméricos";
                    break;
            }

            if (!valido) {
                textNumeroDocumento.setBackground(new Color(255, 200, 200));
                textNumeroDocumento.setToolTipText(mensaje);
            } else {
                textNumeroDocumento.setBackground(Color.WHITE);
                textNumeroDocumento.setToolTipText(null);
            }
        } else {
            textNumeroDocumento.setBackground(Color.WHITE);
            textNumeroDocumento.setToolTipText(null);
        }
    }

    private void limpiarCampos() {
        textNombre.setText("");
        textApellido.setText("");
        textEdad.setText("");
        textFechaNacimiento.setText("");
        textTelefono.setText("");
        textDireccion.setText("");
        textEmail.setText("");
        textNumeroDocumento.setText("");
        comboSexo.setSelectedIndex(0);
        comboPrefijo.setSelectedIndex(0);
        comboTipoDocumento.setSelectedIndex(0);
        checkAceptaTerminos.setSelected(false);

        // Restablecer colores de fondo
        textNombre.setBackground(Color.WHITE);
        textApellido.setBackground(Color.WHITE);
        textEdad.setBackground(Color.WHITE);
        textFechaNacimiento.setBackground(Color.WHITE);
        textTelefono.setBackground(Color.WHITE);
        textEmail.setBackground(Color.WHITE);
        textNumeroDocumento.setBackground(Color.WHITE);

        // Quitar selección de la tabla
        if (tabla != null) {
            tabla.clearSelection();
        }
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        // Validar Nombre
        if (textNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        } else if (!SOLO_LETRAS.matcher(textNombre.getText()).matches()) {
            errores.append("- El nombre solo debe contener letras y espacios\n");
        }

        // Validar Apellido
        if (textApellido.getText().trim().isEmpty()) {
            errores.append("- El apellido es obligatorio\n");
        } else if (!SOLO_LETRAS.matcher(textApellido.getText()).matches()) {
            errores.append("- El apellido solo debe contener letras y espacios\n");
        }

        // Validar Edad
        if (textEdad.getText().trim().isEmpty()) {
            errores.append("- La edad es obligatoria\n");
        } else {
            try {
                int edad = Integer.parseInt(textEdad.getText());
                if (edad < 1 || edad > 120) {
                    errores.append("- La edad debe estar entre 1 y 120 años\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- La edad debe ser un número entero\n");
            }
        }

        // Validar Fecha de Nacimiento
        if (textFechaNacimiento.getText().trim().isEmpty()) {
            errores.append("- La fecha de nacimiento es obligatoria\n");
        } else if (!FECHA_PATTERN.matcher(textFechaNacimiento.getText()).matches()) {
            errores.append("- El formato de fecha debe ser DD/MM/YYYY\n");
        } else {
            try {
                LocalDate fechaNacimiento = LocalDate.parse(textFechaNacimiento.getText(), UI_DATE_FORMATTER);
                LocalDate hoy = LocalDate.now();

                if (fechaNacimiento.isAfter(hoy)) {
                    errores.append("- La fecha de nacimiento no puede ser futura\n");
                }

                // Validar que la edad coincida con la fecha de nacimiento
                // This calculation is more complex with LocalDate, but still possible
                // For simplicity, we'll just check if the year difference is reasonable
                int edadIngresada = Integer.parseInt(textEdad.getText());
                int yearDiff = hoy.getYear() - fechaNacimiento.getYear();

                if (yearDiff < 0 || yearDiff > 120) { // Basic check for extreme ages
                    errores.append("- La edad ingresada no parece coincidir con la fecha de nacimiento.\n");
                } else if (yearDiff != edadIngresada) {
                    // More precise age calculation
                    LocalDate tempDate = fechaNacimiento.plusYears(edadIngresada);
                    if (tempDate.isAfter(hoy)) {
                        errores.append("- La edad ingresada no coincide con la fecha de nacimiento.\n");
                    }
                }

            } catch (DateTimeParseException e) {
                errores.append("- Fecha de nacimiento inválida\n");
            } catch (NumberFormatException e) {
                // Already handled age parsing
            }
        }

        // Validar Teléfono
        if (textTelefono.getText().trim().isEmpty()) {
            errores.append("- El teléfono es obligatorio\n");
        } else if (!SOLO_NUMEROS.matcher(textTelefono.getText()).matches()) {
            errores.append("- El teléfono solo debe contener números\n");
        } else if (textTelefono.getText().length() < 7 || textTelefono.getText().length() > 15) {
            errores.append("- El teléfono debe tener entre 7 y 15 dígitos\n");
        }

        // Validar Dirección
        if (textDireccion.getText().trim().isEmpty()) {
            errores.append("- La dirección es obligatoria\n");
        } else if (textDireccion.getText().length() < 5) {
            errores.append("- La dirección debe tener al menos 5 caracteres\n");
        }

        // Validar Email
        if (textEmail.getText().trim().isEmpty()) {
            errores.append("- El email es obligatorio\n");
        } else if (!EMAIL_PATTERN.matcher(textEmail.getText()).matches()) {
            errores.append("- El formato del email es inválido\n");
        }

        // Validar Número de Documento
        if (textNumeroDocumento.getText().trim().isEmpty()) {
            errores.append("- El número de documento es obligatorio\n");
        } else {
            String tipoDoc = (String) comboTipoDocumento.getSelectedItem();
            String numDoc = textNumeroDocumento.getText();

            switch (tipoDoc) {
                case "DNI":
                    if (!DNI_PATTERN.matcher(numDoc).matches()) {
                        errores.append("- El DNI debe tener 8 dígitos numéricos\n");
                    }
                    break;
                case "Cédula":
                    if (!CEDULA_PATTERN.matcher(numDoc).matches()) {
                        errores.append("- La cédula debe tener 10 dígitos numéricos\n");
                    }
                    break;
                case "Pasaporte":
                    if (!PASAPORTE_PATTERN.matcher(numDoc).matches()) {
                        errores.append("- El pasaporte debe tener entre 6 y 12 caracteres alfanuméricos\n");
                    }
                    break;
            }
        }

        // Validar Términos y Condiciones
        if (!checkAceptaTerminos.isSelected()) {
            errores.append("- Debe aceptar los términos y condiciones\n");
        }

        // Mostrar errores si los hay
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(null,
                    "Por favor, corrija los siguientes errores:\n" + errores.toString(),
                    "Errores de validación",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void procesarFormulario(int id) {
        try {
            // Validar todos los campos
            if (!validarFormulario()) {
                return;
            }

            String nombre = textNombre.getText().trim();
            String apellido = textApellido.getText().trim();
            int edad = Integer.parseInt(textEdad.getText().trim());
            LocalDate fechaNacimiento = LocalDate.parse(textFechaNacimiento.getText().trim(), UI_DATE_FORMATTER); // Parse to LocalDate
            String sexo = (String) comboSexo.getSelectedItem();
            String prefijo = (String) comboPrefijo.getSelectedItem();
            String telefono = textTelefono.getText().trim();
            String direccion = textDireccion.getText().trim();
            String email = textEmail.getText().trim();
            String tipoDocumento = (String) comboTipoDocumento.getSelectedItem();
            String numeroDocumento = textNumeroDocumento.getText().trim();
            boolean aceptaTerminos = checkAceptaTerminos.isSelected();

            Cliente cliente = new Cliente(nombre, apellido, edad, fechaNacimiento, sexo, prefijo, telefono, direccion, email, tipoDocumento, numeroDocumento, aceptaTerminos);

            if (id == -1) {
                controller.registrarCliente(cliente);
                JOptionPane.showMessageDialog(null, "Cliente registrado con éxito.", "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                controller.actualizarCliente(id, cliente);
                JOptionPane.showMessageDialog(null, "Cliente actualizado con éxito.", "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
            }

            // Actualizar la tabla y limpiar campos
            actualizarTabla();
            limpiarCampos();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(null, "Error de formato de fecha: " + ex.getMessage() + ". Asegúrese de usar DD/MM/YYYY.", "Error de Fecha", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Error de formato numérico: " + ex.getMessage() + ". Asegúrese de que la edad sea un número válido.", "Error Numérico", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) { // Catch SQLException from service layer
            JOptionPane.showMessageDialog(null, "Error en la operación de base de datos: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTabla() {
        // Actualizar la lista de clientes
        try {
            listaClientes = ClienteDAO.listar(); // This now throws SQLException
            ClienteDAO.actualizarTabla(); // This method also handles updating the model
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos de clientes: " + e.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel(JTable tabla) {
        this.tabla = tabla;

        // Añadir listener para cargar datos al hacer clic en una fila
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    // Ensure listaClientes is up-to-date before getting ID
                    actualizarTabla(); // Re-fetch to ensure correct ID mapping
                    if (fila < listaClientes.size()) { // Check if row is still valid after refresh
                        Cliente clienteSeleccionado = listaClientes.get(fila);
                        cargarDatosEnFormulario(clienteSeleccionado);
                    } else {
                        JOptionPane.showMessageDialog(null, "La fila seleccionada ya no es válida. Por favor, re-seleccione.",
                                "Error de selección", JOptionPane.ERROR_MESSAGE);
                        limpiarCampos();
                    }
                }
            }
        });

        return panel;
    }

    private void cargarDatosEnFormulario(Cliente cliente) {
        textNombre.setText(cliente.getNombre());
        textApellido.setText(cliente.getApellido());
        textEdad.setText(String.valueOf(cliente.getEdad()));
        // Format LocalDate for UI display
        textFechaNacimiento.setText(cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().format(UI_DATE_FORMATTER) : "");

        // Seleccionar el sexo en el combobox
        for (int i = 0; i < comboSexo.getItemCount(); i++) {
            if (comboSexo.getItemAt(i).equals(cliente.getSexo())) {
                comboSexo.setSelectedIndex(i);
                break;
            }
        }

        // Seleccionar el prefijo en el combobox
        for (int i = 0; i < comboPrefijo.getItemCount(); i++) {
            if (comboPrefijo.getItemAt(i).equals(cliente.getPrefijo())) {
                comboPrefijo.setSelectedIndex(i);
                break;
            }
        }

        textTelefono.setText(cliente.getTelefono());
        textDireccion.setText(cliente.getDireccion());
        textEmail.setText(cliente.getEmail());

        // Seleccionar el tipo de documento en el combobox
        for (int i = 0; i < comboTipoDocumento.getItemCount(); i++) {
            if (comboTipoDocumento.getItemAt(i).equals(cliente.getTipoDocumento())) {
                comboTipoDocumento.setSelectedIndex(i);
                break;
            }
        }

        textNumeroDocumento.setText(cliente.getNumeroDocumento());
        checkAceptaTerminos.setSelected(cliente.isAceptaTerminos());

        // Restablecer colores de fondo
        textNombre.setBackground(Color.WHITE);
        textApellido.setBackground(Color.WHITE);
        textEdad.setBackground(Color.WHITE);
        textFechaNacimiento.setBackground(Color.WHITE);
        textTelefono.setBackground(Color.WHITE);
        textEmail.setBackground(Color.WHITE);
        textNumeroDocumento.setBackground(Color.WHITE);
    }
}
