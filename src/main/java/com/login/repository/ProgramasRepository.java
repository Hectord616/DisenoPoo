package com.login.repository;

import com.login.model.Facultad;
import com.login.model.Programa;
import com.login.service.Servicios;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgramasRepository implements Servicios {
    private final List<Programa> listado = new ArrayList<>();
    private Connection conn;
    private static final String DB_URL = "jdbc:h2:file:./data/universidad;AUTO_SERVER=TRUE";

    public void cargarDatos(List<Facultad> facultades) {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "");
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS PROGRAMAS (" +
                    "ID DOUBLE PRIMARY KEY, " +
                    "NOMBRE VARCHAR(100), " +
                    "DURACION DOUBLE, " +
                    "REGISTRO DATE, " +
                    "FACULTAD_ID DOUBLE)");

            stmt.execute("MERGE INTO PROGRAMAS (ID, NOMBRE, DURACION, REGISTRO, FACULTAD_ID) " +
                    "VALUES (101, 'Ingeniería de Sistemas', 10, CURRENT_DATE, 1)");

            stmt.execute("MERGE INTO PROGRAMAS (ID, NOMBRE, DURACION, REGISTRO, FACULTAD_ID) " +
                    "VALUES (102, 'Psicología', 8, CURRENT_DATE, 2)");

            listado.clear();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PROGRAMAS");
            while (rs.next()) {
                double id = rs.getDouble("ID");
                String nombre = rs.getString("NOMBRE");
                double duracion = rs.getDouble("DURACION");
                LocalDate registro = rs.getDate("REGISTRO").toLocalDate();
                double facultadId = rs.getDouble("FACULTAD_ID");

                Facultad facultad = facultades.stream()
                        .filter(f -> f.getID() == facultadId)
                        .findFirst()
                        .orElse(null);

                listado.add(new Programa(id, nombre, duracion, registro, facultad));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardar(Programa programa) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO PROGRAMAS (ID, NOMBRE, DURACION, REGISTRO, FACULTAD_ID) VALUES (?, ?, ?, ?, ?)");
            ps.setDouble(1, programa.getID());
            ps.setString(2, programa.getNombre());
            ps.setDouble(3, programa.getDuracion());
            ps.setDate(4, Date.valueOf(programa.getRegistro()));
            ps.setDouble(5, programa.getFacultad() != null ? programa.getFacultad().getID() : 0);
            ps.executeUpdate();
            listado.add(programa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public String imprimirPosicion(int pos) { return listado.get(pos).toString(); }
    @Override public int cantidadActual() { return listado.size(); }
    @Override public List<String> imprimirListado() {
        List<String> result = new ArrayList<>();
        for (Programa p : listado) result.add(p.toString());
        return result;
    }

    public void cerrarConexion() {
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
