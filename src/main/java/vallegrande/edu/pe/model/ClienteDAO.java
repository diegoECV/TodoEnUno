package vallegrande.edu.pe.model;

import vallegrande.edu.pe.database.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
private static DefaultTableModel modeloTabla;

// Define formatters for consistent date/time handling
private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
private static final DateTimeFormatter UI_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
private static final DateTimeFormatter UI_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

public static void setModeloTabla(DefaultTableModel modelo) {
modeloTabla = modelo;
}

// Helper method to parse UI date string to LocalDate
public static LocalDate parseFechaNacimiento(String fechaStr) throws DateTimeParseException {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
        return null;
        }
        return LocalDate.parse(fechaStr, UI_DATE_FORMATTER);
    }

// Insertar nuevo cliente
public static void insertar(Cliente cliente) throws SQLException {
String sql = "INSERT INTO cliente (nombre, apellido, edad, fechaNacimiento, sexo, prefijo, telefono, direccion, email, tipoDocumento, numeroDocumento, aceptaTerminos, estado) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Activo')";
        try (Connection conn = DatabaseConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getApellido());
        ps.setInt(3, cliente.getEdad());

        // Set LocalDate to SQL Date
        if (cliente.getFechaNacimiento() != null) {
        ps.setDate(4, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
        } else {
        ps.setNull(4, Types.DATE);
            }

                    ps.setString(5, cliente.getSexo());
        ps.setString(6, cliente.getPrefijo());
        ps.setString(7, cliente.getTelefono());
        ps.setString(8, cliente.getDireccion());
        ps.setString(9, cliente.getEmail());
        ps.setString(10, cliente.getTipoDocumento());
        ps.setString(11, cliente.getNumeroDocumento());
        ps.setBoolean(12, cliente.isAceptaTerminos());

        ps.executeUpdate();
        } catch (SQLException e) {
        // Log the error for debugging, then re-throw a more specific exception if needed
        System.err.println("Error al insertar cliente: " + e.getMessage());
        e.printStackTrace();
            throw e; // Re-throw SQLException to be handled by service layer
        }
                }

// Listar clientes activos desde la base de datos
public static List<Cliente> listar() throws SQLException {
List<Cliente> lista = new ArrayList<>();
String sql = "SELECT id_cliente, nombre, apellido, edad, fechaNacimiento, sexo, prefijo, telefono, direccion, email, tipoDocumento, numeroDocumento, aceptaTerminos, estado, fechaRegistro FROM cliente WHERE estado = 'Activo'";
        try (Connection conn = DatabaseConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql);
ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
Cliente c = new Cliente();
                c.setId(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setEdad(rs.getInt("edad"));

// Get SQL Date and convert to LocalDate
Date fechaBD = rs.getDate("fechaNacimiento");
                if (fechaBD != null) {
        c.setFechaNacimiento(fechaBD.toLocalDate());
        }

        c.setSexo(rs.getString("sexo"));
        c.setPrefijo(rs.getString("prefijo"));
        c.setTelefono(rs.getString("telefono"));
        c.setDireccion(rs.getString("direccion"));
        c.setEmail(rs.getString("email"));
        c.setTipoDocumento(rs.getString("tipoDocumento"));
        c.setNumeroDocumento(rs.getString("numeroDocumento"));
        c.setAceptaTerminos(rs.getBoolean("aceptaTerminos"));
        c.setEstado(rs.getString("estado"));

// Get SQL Timestamp and convert to LocalDateTime
Timestamp fechaRegistro = rs.getTimestamp("fechaRegistro");
                if (fechaRegistro != null) {
        c.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        lista.add(c);
            }
                    } catch (SQLException e) {
        System.err.println("Error al listar clientes: " + e.getMessage());
        e.printStackTrace();
            throw e; // Re-throw SQLException
        }
                return lista;
    }

// Modificar cliente por ID
public static void modificar(int id, Cliente cliente) throws SQLException {
String sql = "UPDATE cliente SET nombre=?, apellido=?, edad=?, fechaNacimiento=?, sexo=?, prefijo=?, telefono=?, direccion=?, email=?, tipoDocumento=?, numeroDocumento=?, aceptaTerminos=? WHERE id_cliente=?";
        try (Connection conn = DatabaseConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getApellido());
        ps.setInt(3, cliente.getEdad());

        // Set LocalDate to SQL Date
        if (cliente.getFechaNacimiento() != null) {
        ps.setDate(4, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
        } else {
        ps.setNull(4, Types.DATE);
            }

                    ps.setString(5, cliente.getSexo());
        ps.setString(6, cliente.getPrefijo());
        ps.setString(7, cliente.getTelefono());
        ps.setString(8, cliente.getDireccion());
        ps.setString(9, cliente.getEmail());
        ps.setString(10, cliente.getTipoDocumento());
        ps.setString(11, cliente.getNumeroDocumento());
        ps.setBoolean(12, cliente.isAceptaTerminos());
        ps.setInt(13, id);

            ps.executeUpdate();
        } catch (SQLException e) {
        System.err.println("Error al modificar cliente: " + e.getMessage());
        e.printStackTrace();
            throw e; // Re-throw SQLException
        }
                }

// Eliminación lógica (actualizar estado a Inactivo)
public static void eliminar(int id) throws SQLException {
String sql = "UPDATE cliente SET estado='Inactivo' WHERE id_cliente=?";
        try (Connection conn = DatabaseConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
        System.err.println("Error al eliminar cliente: " + e.getMessage());
        e.printStackTrace();
            throw e; // Re-throw SQLException
        }
                }

// Actualiza el modelo de la tabla Swing con los clientes activos
public static void actualizarTabla() {
        if (modeloTabla == null) return;
        try {
List<Cliente> lista = listar(); // This now throws SQLException
            modeloTabla.setRowCount(0); // Clear existing rows
            for (Cliente cliente : lista) {
String[] datos = {
        String.valueOf(cliente.getId()), // Use actual ID from database
        cliente.getNombre(),
                        cliente.getApellido(),
                        String.valueOf(cliente.getEdad()),
        cliente.getFechaNacimiento() != null ? cliente.getFechaNacimiento().format(UI_DATE_FORMATTER) : "", // Format LocalDate for UI
        cliente.getSexo(),
                        cliente.getPrefijo(),
                        cliente.getTelefono(),
                        cliente.getEmail(),
                        cliente.getTipoDocumento(),
                        cliente.getNumeroDocumento(),
                        cliente.isAceptaTerminos() ? "Sí" : "No",
        cliente.getFechaRegistro() != null ? cliente.getFechaRegistro().format(UI_DATETIME_FORMATTER) : "", // Format LocalDateTime for UI
        cliente.getEstado()
                };
                        modeloTabla.addRow(datos);
            }
                    } catch (SQLException e) {
        System.err.println("Error al actualizar la tabla: " + e.getMessage());
        e.printStackTrace();
// This error should ideally be handled by the UI layer, not just printed here.
// For now, we'll let the UI layer catch it if it calls this method.
        }
                }
                }
