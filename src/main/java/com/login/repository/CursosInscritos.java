package com.login.repository;

import com.login.model.*;
import com.login.service.Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursosInscritos implements Servicios {
    private final List<Inscripcion> listado = new ArrayList<>();
    private Connection conn;

    // Base en memoria
    private static final String DB_URL = "jdbc:h2:mem:universidad;DB_CLOSE_DELAY=-1";

    public void cargarDatos(List<Persona> personas, List<Curso> cursos) {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();

            listado.clear();

            stmt.execute("CREATE TABLE IF NOT EXISTS INSCRIPCIONES (" +
                    "CURSO_ID INT, ESTUDIANTE_ID DOUBLE, ANIO INT, SEMESTRE INT, " +
                    "PRIMARY KEY (CURSO_ID, ESTUDIANTE_ID))");

            // Datos de prueba
            stmt.execute("MERGE INTO INSCRIPCIONES (CURSO_ID, ESTUDIANTE_ID, ANIO, SEMESTRE) " +
                    "VALUES (101, 3, 2024, 1)");
            stmt.execute("MERGE INTO INSCRIPCIONES (CURSO_ID, ESTUDIANTE_ID, ANIO, SEMESTRE) " +
                    "VALUES (102, 4, 2024, 2)");

            ResultSet rs = stmt.executeQuery("SELECT * FROM INSCRIPCIONES");
            while (rs.next()) {
                int cursoId = rs.getInt("CURSO_ID");
                double estudianteId = rs.getDouble("ESTUDIANTE_ID");

                // 🔹 Buscar curso real, o crear uno genérico
                Curso curso = cursos.stream()
                        .filter(c -> c.getID() == cursoId)
                        .findFirst()
                        .orElse(crearCursoGenerico(cursoId));

                // 🔹 Buscar persona y convertirla en Estudiante
                Persona personaAsociada = personas.stream()
                        .filter(p -> p.getID() == estudianteId)
                        .findFirst()
                        .orElse(null);

                Estudiante estudiante;
                if (personaAsociada instanceof Estudiante) {
                    estudiante = (Estudiante) personaAsociada;
                } else if (personaAsociada != null) {
                    estudiante = crearEstudianteDesdePersona(personaAsociada);
                } else {
                    estudiante = new Estudiante(
                            estudianteId,
                            "Estudiante",
                            "Desconocido",
                            "",
                            estudianteId,
                            new Programa(1, "Programa genérico", null),
                            true,
                            3.0
                    );
                }

                Inscripcion inscripcion = new Inscripcion(
                        curso,
                        rs.getInt("ANIO"),
                        rs.getInt("SEMESTRE"),
                        estudiante
                );
                listado.add(inscripcion);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Curso crearCursoGenerico(int id) {
        return new Curso(
                id,
                (id == 101) ? "Matemáticas Básicas" :
                (id == 102) ? "Programación I" :
                        "Curso genérico",
                new Programa(1, "Ingeniería de Sistemas", null),
                true
        );
    }

    private Estudiante crearEstudianteDesdePersona(Persona p) {
        return new Estudiante(
                p.getID(),
                p.getNombres(),
                p.getApellidos(),
                p.getEmail(),
                p.getID(),
                new Programa(1, "Ingeniería de Sistemas", null),
                true,
                0.0
        );
    }

    // Métodos CRUD
    public void inscribir(Inscripcion inscripcion) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO INSCRIPCIONES VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, inscripcion.getCurso().getID());
            ps.setDouble(2, inscripcion.getEstudiante().getID());
            ps.setInt(3, inscripcion.getAño());
            ps.setInt(4, inscripcion.getSemestre());
            ps.executeUpdate();
            ps.close();

            listado.add(inscripcion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(Inscripcion inscripcion) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM INSCRIPCIONES WHERE CURSO_ID=? AND ESTUDIANTE_ID=? AND ANIO=? AND SEMESTRE=?"
            );
            ps.setInt(1, inscripcion.getCurso().getID());
            ps.setDouble(2, inscripcion.getEstudiante().getID());
            ps.setInt(3, inscripcion.getAño());
            ps.setInt(4, inscripcion.getSemestre());
            ps.executeUpdate();
            ps.close();

            listado.remove(inscripcion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Inscripcion inscripcion) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE INSCRIPCIONES SET ANIO=?, SEMESTRE=? WHERE CURSO_ID=? AND ESTUDIANTE_ID=?"
            );
            ps.setInt(1, inscripcion.getAño());
            ps.setInt(2, inscripcion.getSemestre());
            ps.setInt(3, inscripcion.getCurso().getID());
            ps.setDouble(4, inscripcion.getEstudiante().getID());
            ps.executeUpdate();
            ps.close();

            for (int i = 0; i < listado.size(); i++) {
                if (listado.get(i).getCurso().getID() == inscripcion.getCurso().getID() &&
                        listado.get(i).getEstudiante().getID() == inscripcion.getEstudiante().getID()) {
                    listado.set(i, inscripcion);
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void guardarInformacion(Inscripcion inscripcion) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO INSCRIPCIONES (CURSO_ID, ESTUDIANTE_ID, ANIO, SEMESTRE) KEY(CURSO_ID, ESTUDIANTE_ID) VALUES (?, ?, ?, ?)"
            );
            ps.setInt(1, inscripcion.getCurso().getID());
            ps.setDouble(2, inscripcion.getEstudiante().getID());
            ps.setInt(3, inscripcion.getAño());
            ps.setInt(4, inscripcion.getSemestre());
            ps.executeUpdate();
            ps.close();

            boolean updated = false;
            for (int i = 0; i < listado.size(); i++) {
                if (listado.get(i).getCurso().getID() == inscripcion.getCurso().getID() &&
                        listado.get(i).getEstudiante().getID() == inscripcion.getEstudiante().getID()) {
                    listado.set(i, inscripcion);
                    updated = true;
                    break;
                }
            }
            if (!updated) listado.add(inscripcion);

        } catch (SQLException e) {
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
    public List<String> imprimirListado() {
        List<String> result = new ArrayList<>();
        for (Inscripcion i : listado) {
            result.add(i.toString());
        }
        return result;
    }

    public void cerrarConexion() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
