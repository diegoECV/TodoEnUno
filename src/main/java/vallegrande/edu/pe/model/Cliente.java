package vallegrande.edu.pe.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cliente {
private int id;
private String nombre;
private String apellido;
private int edad;
private LocalDate fechaNacimiento; // Changed to LocalDate
private String sexo;
private String prefijo;
private String telefono;
private String direccion;
private String email;
private String tipoDocumento;
private String numeroDocumento;
private boolean aceptaTerminos;
private String estado;
private LocalDateTime fechaRegistro; // Changed to LocalDateTime

// Constructor completo
public Cliente(String nombre, String apellido, int edad, LocalDate fechaNacimiento, String sexo,
               String prefijo, String telefono, String direccion, String email,
               String tipoDocumento, String numeroDocumento, boolean aceptaTerminos) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.prefijo = prefijo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.aceptaTerminos = aceptaTerminos;
        this.estado = "Activo"; // Default state for new clients
        }

// Constructor vacío requerido para usar setters
public Cliente() {
        }

// Getters
public int getId() { return id; }
public String getNombre() { return nombre; }
public String getApellido() { return apellido; }
public int getEdad() { return edad; }
public LocalDate getFechaNacimiento() { return fechaNacimiento; } // Getter for LocalDate
public String getSexo() { return sexo; }
public String getPrefijo() { return prefijo; }
public String getTelefono() { return telefono; }
public String getDireccion() { return direccion; }
public String getEmail() { return email; }
public String getTipoDocumento() { return tipoDocumento; }
public String getNumeroDocumento() { return numeroDocumento; }
public boolean isAceptaTerminos() { return aceptaTerminos; }
public String getEstado() { return estado; }
public LocalDateTime getFechaRegistro() { return fechaRegistro; } // Getter for LocalDateTime

// Setters
public void setId(int id) { this.id = id; }
public void setNombre(String nombre) { this.nombre = nombre; }
public void setApellido(String apellido) { this.apellido = apellido; }
public void setEdad(int edad) { this.edad = edad; }
public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; } // Setter for LocalDate
public void setSexo(String sexo) { this.sexo = sexo; }
public void setPrefijo(String prefijo) { this.prefijo = prefijo; }
public void setTelefono(String telefono) { this.telefono = telefono; }
public void setDireccion(String direccion) { this.direccion = direccion; }
public void setEmail(String email) { this.email = email; }
public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
public void setAceptaTerminos(boolean aceptaTerminos) { this.aceptaTerminos = aceptaTerminos; }
public void setEstado(String estado) { this.estado = estado; }
public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; } // Setter for LocalDateTime
        }
