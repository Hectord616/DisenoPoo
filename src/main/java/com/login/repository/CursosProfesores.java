package com.login.repository;

import com.login.model.Curso;
import com.login.model.CursoProfesor;
import com.login.model.Persona;
import com.login.model.Profesor;
import com.login.model.Programa;
import com.login.service.Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursosProfesores implements Servicios<CursoProfesor> {
    private final List<CursoProfesor> listado = new ArrayList<>();
    private Connection conn;

    private static final String DB_URL = "jdbc:h2:file:./data/universidad;AUTO_SERVER=TRUE";

    private final Programa programa;

    public CursosProfesores(Programa programa) {
        this.programa = programa;
    }

    public void cargarDatos(List<Persona> personas) {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "");
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS CURSOS_PROFESORES (" +
                    "CURSO_ID INT, CURSO_NOMBRE VARCHAR(255), " +
                    "PROFESOR_ID DOUBLE, ANIO INT, SEMESTRE INT, " +
                    "PRIMARY KEY (CURSO_ID, PROFESOR_ID))");

            // Datos iniciales de ejemplo (MERGE para idempotencia)
            stmt.execute("MERGE INTO CURSOS_PROFESORES (CURSO_ID, CURSO_NOMBRE, PROFESOR_ID, ANIO, SEMESTRE) " +
                    "KEY(CURSO_ID, PROFESOR_ID) VALUES (1001, 'Programación Orientada a Objetos', 1, 2024, 1)");
            stmt.execute("MERGE INTO CURSOS_PROFESORES (CURSO_ID, CURSO_NOMBRE, PROFESOR_ID, ANIO, SEMESTRE) " +
                    "KEY(CURSO_ID, PROFESOR_ID) VALUES (1002, 'Bases de Datos', 2, 2024, 2)");

            listado.clear();
            ResultSet rs = stmt.executeQuery("SELECT * FROM CURSOS_PROFESORES");
            while (rs.next()) {
                double profesorId = rs.getDouble("PROFESOR_ID");

                Persona personaAsociada = personas.stream()
                        .filter(p -> p.getID() == profesorId)
                        .findFirst()
                        .orElse(null);

                Profesor profesor = (personaAsociada != null)
                        ? new Profesor(
                        personaAsociada.getID(),
                        personaAsociada.getNombres(),
                        personaAsociada.getApellidos(),
                        personaAsociada.getEmail(),
                        "Tiempo Completo")
                        : new Profesor(profesorId, "", "", "", "Tiempo Completo");

                Curso curso = new Curso(
                        rs.getInt("CURSO_ID"),
                        rs.getString("CURSO_NOMBRE"),
                        programa,
                        true);

                CursoProfesor cp = new CursoProfesor(
                        profesor,
                        rs.getInt("ANIO"),
                        rs.getInt("SEMESTRE"),
                        curso);
                listado.add(cp);
            }
            rs.close();
            stmt.close();
            System.out.println("CURSOS_PROFESORES cargados: " + listado.size());
        } catch (Exception e) {
            System.err.println("Error al cargar CURSOS_PROFESORES:");
            e.printStackTrace();
        }
    }

    public void agregar(CursoProfesor cp) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO CURSOS_PROFESORES (CURSO_ID, CURSO_NOMBRE, PROFESOR_ID, ANIO, SEMESTRE) " +
                            "KEY(CURSO_ID, PROFESOR_ID) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, cp.getCurso().getID());
                ps.setString(2, cp.getCurso().getNombre());
                ps.setDouble(3, cp.getProfesor().getID());
                ps.setInt(4, cp.getAnio());
                ps.setInt(5, cp.getSemestre());
                ps.executeUpdate();
            }

            listado.add(cp);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(CursoProfesor cp) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM CURSOS_PROFESORES WHERE CURSO_ID=? AND PROFESOR_ID=?")) {
                ps.setInt(1, cp.getCurso().getID());
                ps.setDouble(2, cp.getProfesor().getID());
                ps.executeUpdate();
            }
            listado.removeIf(item ->
                    item.getCurso().getID() == cp.getCurso().getID()
                            && item.getProfesor().getID() == cp.getProfesor().getID());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void actualizar(CursoProfesor cp) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE CURSOS_PROFESORES SET CURSO_NOMBRE=?, ANIO=?, SEMESTRE=? WHERE CURSO_ID=? AND PROFESOR_ID=?")) {
                ps.setString(1, cp.getCurso().getNombre());
                ps.setInt(2, cp.getAnio());
                ps.setInt(3, cp.getSemestre());
                ps.setInt(4, cp.getCurso().getID());
                ps.setDouble(5, cp.getProfesor().getID());
                ps.executeUpdate();
            }

            for (int i = 0; i < listado.size(); i++) {
                CursoProfesor cur = listado.get(i);
                if (cur.getCurso().getID() == cp.getCurso().getID() &&
                        cur.getProfesor().getID() == cp.getProfesor().getID()) {
                    listado.set(i, cp);
                    break;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
    public List<CursoProfesor> imprimirListado() {
        return new ArrayList<>(listado);
    }

    public List<Curso> getCursos() {
        List<Curso> cursos = new ArrayList<>();
        for (CursoProfesor cp : listado) {
            cursos.add(cp.getCurso());
        }
        return cursos;
    }

    public void imprimirBaseDeDatos() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM CURSOS_PROFESORES")) {
                System.out.println("\n=== TABLA CURSOS_PROFESORES ===");
                while (rs.next()) {
                    System.out.println(
                            "CURSO_ID=" + rs.getInt("CURSO_ID") +
                                    ", Nombre=" + rs.getString("CURSO_NOMBRE") +
                                    ", ProfesorID=" + rs.getDouble("PROFESOR_ID") +
                                    ", Año=" + rs.getInt("ANIO") +
                                    ", Semestre=" + rs.getInt("SEMESTRE")
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
                System.out.println(" Conexión CURSOS_PROFESORES cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
