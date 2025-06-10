package vallegrande.edu.pe.service;

import vallegrande.edu.pe.model.Cliente;
import vallegrande.edu.pe.model.ClienteDAO;

import java.sql.SQLException; // Import SQLException

public class ClienteService {
public static void registrar(Cliente cliente) throws SQLException { // Declare SQLException
        ClienteDAO.insertar(cliente);
// No JOptionPane here, let the UI handle messages
    }

public static void actualizar(int id, Cliente cliente) throws SQLException { // Declare SQLException
        ClienteDAO.modificar(id, cliente);
// No JOptionPane here
    }

public static void eliminar(int id) throws SQLException { // Declare SQLException
        ClienteDAO.eliminar(id);
// No JOptionPane here
    }
            }
