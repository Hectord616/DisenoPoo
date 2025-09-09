package com.login.repository;

import com.login.model.Curso;
import com.login.model.Programa;
import com.login.service.Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursosRepository implements Servicios {
    private final List<Curso> listado = new ArrayList<>();
    private Connection conn;
    private static final String DB_URL = "jdbc:h2:file:./data/universidad;AUTO_SERVER=TRUE";

    public void cargarDatos(List<Programa> programas) {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "");
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS CURSOS (" +
                    "ID INT PRIMARY KEY, " +
                    "NOMBRE VARCHAR(100), " +
                    "ACTIVO BOOLEAN, " +
                    "PROGRAMA_ID DOUBLE)");

            stmt.execute("MERGE INTO CURSOS (ID, NOMBRE, ACTIVO, PROGRAMA_ID) VALUES (201, 'Matemáticas I', TRUE, 101)");
            stmt.execute("MERGE INTO CURSOS (ID, NOMBRE, ACTIVO, PROGRAMA_ID) VALUES (202, 'Psicología General', TRUE, 102)");

            listado.clear();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CURSOS");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String nombre = rs.getString("NOMBRE");
                boolean activo = rs.getBoolean("ACTIVO");
                double programaId = rs.getDouble("PROGRAMA_ID");

                Programa programa = programas.stream()
                        .filter(p -> p.getID() == programaId)
                        .findFirst()
                        .orElse(null);

                listado.add(new Curso(id, nombre, programa, activo));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardar(Curso curso) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO CURSOS (ID, NOMBRE, ACTIVO, PROGRAMA_ID) VALUES (?, ?, ?, ?)");
            ps.setInt(1, curso.getID());
            ps.setString(2, curso.getNombre());
            ps.setBoolean(3, curso.isActivo());
            ps.setDouble(4, curso.getPrograma() != null ? curso.getPrograma().getID() : 0);
            ps.executeUpdate();
            listado.add(curso);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public String imprimirPosicion(int pos) { return listado.get(pos).toString(); }
    @Override public int cantidadActual() { return listado.size(); }
    @Override public List<String> imprimirListado() {
        List<String> result = new ArrayList<>();
        for (Curso c : listado) result.add(c.toString());
        return result;
    }

    public void cerrarConexion() {
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
