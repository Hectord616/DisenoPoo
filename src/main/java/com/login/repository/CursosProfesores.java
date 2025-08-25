package com.login.repository;

import com.login.model.Curso;
import com.login.model.CursoProfesor;
import com.login.model.Facultad;
import com.login.model.Persona;
import com.login.model.Profesor;
import com.login.model.Programa;
import com.login.service.Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursosProfesores implements Servicios {
    private final List<CursoProfesor> listado = new ArrayList<>();
    private Connection conn;

    // Base de datos en memoria H2
    private static final String DB_URL = "jdbc:h2:mem:universidad;DB_CLOSE_DELAY=-1";

    // Facultad y Programa "simulados"
    private Facultad facultad = new Facultad(1, "Facultad de Ingeniería");
    private Programa programa = new Programa(101, "Ingeniería de Sistemas", facultad);

    public void cargarDatos(List<Persona> personas) {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();

            stmt.execute("CREATE TABLE IF NOT EXISTS CURSOS_PROFESORES (" +
                    "CURSO_ID INT, CURSO_NOMBRE VARCHAR(255), " +
                    "PROFESOR_ID DOUBLE, ANIO INT, SEMESTRE INT, " +
                    "PRIMARY KEY (CURSO_ID, PROFESOR_ID))");

            // Insertamos cursos con nombres REALES en lugar de "Pendiente"
            stmt.execute("INSERT INTO CURSOS_PROFESORES VALUES (1001, 'Programación Orientada a Objetos', 1, 2024, 1)");
            stmt.execute("INSERT INTO CURSOS_PROFESORES VALUES (1002, 'Bases de Datos', 2, 2024, 2)");

            ResultSet rs = stmt.executeQuery("SELECT * FROM CURSOS_PROFESORES");
            while (rs.next()) {
                double profesorId = rs.getDouble("PROFESOR_ID");

                // Buscar persona
                Persona personaAsociada = personas.stream()
                        .filter(p -> p.getID() == profesorId)
                        .findFirst()
                        .orElse(null);

                // Crear profesor
                Profesor profesor = (personaAsociada != null)
                        ? new Profesor(
                                personaAsociada.getID(),
                                personaAsociada.getNombres(),
                                personaAsociada.getApellidos(),
                                personaAsociada.getEmail(),
                                "Tiempo Completo")
                        : new Profesor(profesorId, "", "", "", "Tiempo Completo");

                // Crear curso con Programa y Facultad
                Curso curso = new Curso(
                        rs.getInt("CURSO_ID"),
                        rs.getString("CURSO_NOMBRE"),
                        programa,
                        true
                );

                // Crear CursoProfesor
                CursoProfesor cp = new CursoProfesor(
                        profesor,
                        rs.getInt("ANIO"),
                        rs.getInt("SEMESTRE"),
                        curso
                );
                listado.add(cp);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Métodos CRUD
    public void agregar(CursoProfesor cp) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO CURSOS_PROFESORES VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, cp.getCurso().getID());
            ps.setString(2, cp.getCurso().getNombre());
            ps.setDouble(3, cp.getProfesor().getID());
            ps.setInt(4, cp.getAño());
            ps.setInt(5, cp.getSemestre());
            ps.executeUpdate();
            listado.add(cp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(CursoProfesor cp) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM CURSOS_PROFESORES WHERE CURSO_ID=? AND PROFESOR_ID=?");
            ps.setInt(1, cp.getCurso().getID());
            ps.setDouble(2, cp.getProfesor().getID());
            ps.executeUpdate();
            listado.remove(cp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizar(CursoProfesor cp) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE CURSOS_PROFESORES SET CURSO_NOMBRE=?, ANIO=?, SEMESTRE=? WHERE CURSO_ID=? AND PROFESOR_ID=?");
            ps.setString(1, cp.getCurso().getNombre());
            ps.setInt(2, cp.getAño());
            ps.setInt(3, cp.getSemestre());
            ps.setInt(4, cp.getCurso().getID());
            ps.setDouble(5, cp.getProfesor().getID());
            ps.executeUpdate();

            for (int i = 0; i < listado.size(); i++) {
                if (listado.get(i).getCurso().getID() == cp.getCurso().getID() &&
                        listado.get(i).getProfesor().getID() == cp.getProfesor().getID()) {
                    listado.set(i, cp);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Implementación de Servicios
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
    public List<String> imprimirListado() {
        List<String> result = new ArrayList<>();
        for (CursoProfesor cp : listado) {
            result.add(cp.toString());
        }
        return result;
    }

    public void cerrarConexion() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Obtener lista de cursos
    public List<Curso> getCursos() {
        List<Curso> cursos = new ArrayList<>();
        for (CursoProfesor cp : listado) {
            cursos.add(cp.getCurso());
        }
        return cursos;
    }
}
