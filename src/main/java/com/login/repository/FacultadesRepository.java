package com.login.repository;

import com.login.model.Facultad;
import com.login.model.Persona;
import com.login.service.Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultadesRepository implements Servicios {
    private final List<Facultad> listado = new ArrayList<>();
    private Connection conn;

    private static final String DB_URL = "jdbc:h2:file:./data/universidad;AUTO_SERVER=TRUE";

    public void cargarDatos(List<Persona> personas) {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "");
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS FACULTADES (" +
                    "ID DOUBLE PRIMARY KEY, " +
                    "NOMBRE VARCHAR(100), " +
                    "DECANO_ID DOUBLE)");

            // Datos iniciales
            stmt.execute("MERGE INTO FACULTADES (ID, NOMBRE, DECANO_ID) VALUES (1, 'Ingeniería', 1)");
            stmt.execute("MERGE INTO FACULTADES (ID, NOMBRE, DECANO_ID) VALUES (2, 'Ciencias Sociales', 2)");

            listado.clear();
            ResultSet rs = stmt.executeQuery("SELECT * FROM FACULTADES");
            while (rs.next()) {
                double id = rs.getDouble("ID");
                String nombre = rs.getString("NOMBRE");
                double decanoId = rs.getDouble("DECANO_ID");

                Persona decano = personas.stream()
                        .filter(p -> p.getID() == decanoId)
                        .findFirst()
                        .orElse(null);

                listado.add(new Facultad(id, nombre, decano));
            }
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardar(Facultad facultad) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO FACULTADES (ID, NOMBRE, DECANO_ID) VALUES (?, ?, ?)");
            ps.setDouble(1, facultad.getID());
            ps.setString(2, facultad.getNombre());
            ps.setDouble(3, facultad.getDecano() != null ? facultad.getDecano().getID() : 0);
            ps.executeUpdate();
            listado.add(facultad);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public String imprimirPosicion(int pos) { return listado.get(pos).toString(); }
    @Override public int cantidadActual() { return listado.size(); }
    @Override public List<String> imprimirListado() {
        List<String> result = new ArrayList<>();
        for (Facultad f : listado) result.add(f.toString());
        return result;
    }

    public void cerrarConexion() {
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
