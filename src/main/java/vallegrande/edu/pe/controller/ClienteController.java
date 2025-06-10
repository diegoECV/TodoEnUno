package vallegrande.edu.pe.controller;

import vallegrande.edu.pe.model.Cliente;
import vallegrande.edu.pe.service.ClienteService;

import java.sql.SQLException; // Make sure this import is present

public class ClienteController {
    public void registrarCliente(Cliente cliente) throws SQLException { // <-- IMPORTANT: Add this
        ClienteService.registrar(cliente);
    }

    public void actualizarCliente(int id, Cliente cliente) throws SQLException { // <-- IMPORTANT: Add this
        ClienteService.actualizar(id, cliente);
    }

    public void eliminarCliente(int id) throws SQLException { // <-- IMPORTANT: Add this
        ClienteService.eliminar(id);
    }
}
