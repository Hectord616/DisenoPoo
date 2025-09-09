package com.login.repository;

import com.login.model.Persona;
import com.login.service.Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscripcionesPersonas implements Servicios<Persona> {
    private final List<Persona> listado = new ArrayList<>();
    private Connection conn;

    private static final String DB_URL = "jdbc:h2:file:./data/universidad;AUTO_SERVER=TRUE";

    public List<Persona> getListado() {
        return listado;
    }

    public void cargarDatos() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "");

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS PERSONAS (" +
                        "ID DOUBLE PRIMARY KEY, " +
                        "NOMBRES VARCHAR(100), " +
                        "APELLIDOS VARCHAR(100), " +
                        "EMAIL VARCHAR(150))");

                // Datos iniciales ejemplo (MERGE evita duplicados)
                stmt.execute("MERGE INTO PERSONAS (ID, NOMBRES, APELLIDOS, EMAIL) VALUES (1, 'Juan', 'Perez', 'juan@uni.com')");
                stmt.execute("MERGE INTO PERSONAS (ID, NOMBRES, APELLIDOS, EMAIL) VALUES (2, 'Ana', 'Gomez', 'ana@uni.com')");
                stmt.execute("MERGE INTO PERSONAS (ID, NOMBRES, APELLIDOS, EMAIL) VALUES (3, 'Lucas', 'Duck', 'lucas@uni.com')");
                stmt.execute("MERGE INTO PERSONAS (ID, NOMBRES, APELLIDOS, EMAIL) VALUES (4, 'Isabella', 'Mesa', 'isabella@uni.com')");
                stmt.execute("MERGE INTO PERSONAS (ID, NOMBRES, APELLIDOS, EMAIL) VALUES (5, 'Carlos', 'Angulo', 'carlos@uni.com')");
            }

            listado.clear();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM PERSONAS ORDER BY ID")) {
                while (rs.next()) {
                    Persona persona = new Persona(
                            rs.getDouble("ID"),
                            rs.getString("NOMBRES"),
                            rs.getString("APELLIDOS"),
                            rs.getString("EMAIL")
                    );
                    listado.add(persona);
                }
            }

            System.out.println("PERSONAS cargadas correctamente: " + listado.size());
        } catch (Exception e) {
            System.err.println("Error al cargar datos en InscripcionesPersonas:");
            e.printStackTrace();
        }
    }

    public void inscribir(Persona persona) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO PERSONAS (ID, NOMBRES, APELLIDOS, EMAIL) VALUES (?, ?, ?, ?)")) {
                ps.setDouble(1, persona.getID());
                ps.setString(2, persona.getNombres());
                ps.setString(3, persona.getApellidos());
                ps.setString(4, persona.getEmail());
                ps.executeUpdate();
            }

            // actualizar lista local
            listado.removeIf(p -> p.getID() == persona.getID());
            listado.add(persona);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminar(double idPersona) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM PERSONAS WHERE ID = ?")) {
                ps.setDouble(1, idPersona);
                ps.executeUpdate();
            }
            listado.removeIf(p -> p.getID() == idPersona);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Persona persona) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE PERSONAS SET NOMBRES=?, APELLIDOS=?, EMAIL=? WHERE ID=?")) {
                ps.setString(1, persona.getNombres());
                ps.setString(2, persona.getApellidos());
                ps.setString(3, persona.getEmail());
                ps.setDouble(4, persona.getID());
                ps.executeUpdate();
            }

            for (int i = 0; i < listado.size(); i++) {
                if (listado.get(i).getID() == persona.getID()) {
                    listado.set(i, persona);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Persona buscarPorId(double idPersona) {
        for (Persona p : listado) {
            if (p.getID() == idPersona) return p;
        }
        return null;
    }

    @Override
    public String imprimirPosicion(int posicion) {
        if (posicion >= 0 && posicion < listado.size()) {
            return listado.get(posicion).toString();
        }
        return "Índice fuera de rango.";
    }

    @Override
    public int cantidadActual() {
        return listado.size();
    }

    @Override
    public List<Persona> imprimirListado() {
        return new ArrayList<>(listado);
    }

    public void imprimirBaseDeDatos() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM PERSONAS")) {
                System.out.println("\n=== TABLA PERSONAS ===");
                while (rs.next()) {
                    System.out.println(
                            "ID=" + rs.getDouble("ID") +
                                    ", Nombres=" + rs.getString("NOMBRES") +
                                    ", Apellidos=" + rs.getString("APELLIDOS") +
                                    ", Email=" + rs.getString("EMAIL")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarConexion() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Conexión PERSONAS cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
