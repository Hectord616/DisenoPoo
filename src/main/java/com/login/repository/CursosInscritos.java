package com.login.repository;

import com.login.model.*;
import com.login.service.Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursosInscritos implements Servicios<Inscripcion> {
    private final List<Inscripcion> listado = new ArrayList<>();
    private Connection conn;

    private static final String DB_URL = "jdbc:h2:file:./data/universidad;AUTO_SERVER=TRUE";

    private final Programa programa; //  Se recibe desde Main

    public CursosInscritos(Programa programa) {
        this.programa = programa;
    }

    public void cargarDatos(List<Persona> personas, List<Curso> cursos) {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "");
            Statement stmt = conn.createStatement();

            listado.clear();

            stmt.execute("CREATE TABLE IF NOT EXISTS INSCRIPCIONES (" +
                    "CURSO_ID INT, ESTUDIANTE_ID DOUBLE, ANIO INT, SEMESTRE INT, " +
                    "PRIMARY KEY (CURSO_ID, ESTUDIANTE_ID))");

            // Datos iniciales ejemplo
            stmt.execute("MERGE INTO INSCRIPCIONES (CURSO_ID, ESTUDIANTE_ID, ANIO, SEMESTRE) " +
                    "VALUES (101, 3, 2024, 1)");
            stmt.execute("MERGE INTO INSCRIPCIONES (CURSO_ID, ESTUDIANTE_ID, ANIO, SEMESTRE) " +
                    "VALUES (102, 4, 2024, 2)");

            ResultSet rs = stmt.executeQuery("SELECT * FROM INSCRIPCIONES");
            while (rs.next()) {
                int cursoId = rs.getInt("CURSO_ID");
                double estudianteId = rs.getDouble("ESTUDIANTE_ID");

                // Buscar curso existente en la lista pasada
                Curso curso = cursos.stream()
                        .filter(c -> c.getID() == cursoId)
                        .findFirst()
                        .orElse(new Curso(
                                cursoId,
                                (cursoId == 101) ? "Matemáticas Básicas" :
                                        (cursoId == 102) ? "Programación I" :
                                                "Curso genérico",
                                programa,
                                true
                        ));

                // Buscar estudiante existente
                Persona personaAsociada = personas.stream()
                        .filter(p -> p.getID() == estudianteId)
                        .findFirst()
                        .orElse(null);

                Estudiante estudiante;
                if (personaAsociada instanceof Estudiante) {
                    estudiante = (Estudiante) personaAsociada;
                } else if (personaAsociada != null) {
                    estudiante = new Estudiante(
                            personaAsociada.getID(),
                            personaAsociada.getNombres(),
                            personaAsociada.getApellidos(),
                            personaAsociada.getEmail(),
                            personaAsociada.getID(),
                            programa,
                            true,
                            0.0
                    );
                } else {
                    estudiante = new Estudiante(
                            estudianteId,
                            "Estudiante",
                            "Desconocido",
                            "",
                            estudianteId,
                            programa,
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
            System.out.println("INSCRIPCIONES cargadas: " + listado.size());
        } catch (Exception e) {
            System.err.println("Error al cargar INSCRIPCIONES:");
            e.printStackTrace();
        }
    }

    // --- Métodos CRUD ---

    public void inscribir(Inscripcion inscripcion) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO INSCRIPCIONES (CURSO_ID, ESTUDIANTE_ID, ANIO, SEMESTRE) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, inscripcion.getCurso().getID());
                ps.setDouble(2, inscripcion.getEstudiante().getID());
                ps.setInt(3, inscripcion.getAño());
                ps.setInt(4, inscripcion.getSemestre());
                ps.executeUpdate();
            }
            listado.add(inscripcion);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void eliminar(Inscripcion inscripcion) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM INSCRIPCIONES WHERE CURSO_ID=? AND ESTUDIANTE_ID=? AND ANIO=? AND SEMESTRE=?")) {
                ps.setInt(1, inscripcion.getCurso().getID());
                ps.setDouble(2, inscripcion.getEstudiante().getID());
                ps.setInt(3, inscripcion.getAño());
                ps.setInt(4, inscripcion.getSemestre());
                ps.executeUpdate();
            }
            listado.removeIf(i ->
                    i.getCurso().getID() == inscripcion.getCurso().getID()
                            && i.getEstudiante().getID() == inscripcion.getEstudiante().getID()
                            && i.getAño() == inscripcion.getAño()
                            && i.getSemestre() == inscripcion.getSemestre());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Inscripcion inscripcion) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE INSCRIPCIONES SET ANIO=?, SEMESTRE=? WHERE CURSO_ID=? AND ESTUDIANTE_ID=?")) {
                ps.setInt(1, inscripcion.getAño());
                ps.setInt(2, inscripcion.getSemestre());
                ps.setInt(3, inscripcion.getCurso().getID());
                ps.setDouble(4, inscripcion.getEstudiante().getID());
                ps.executeUpdate();
            }

            for (int i = 0; i < listado.size(); i++) {
                Inscripcion cur = listado.get(i);
                if (cur.getCurso().getID() == inscripcion.getCurso().getID() &&
                        cur.getEstudiante().getID() == inscripcion.getEstudiante().getID()) {
                    listado.set(i, inscripcion);
                    break;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void guardarInformacion(Inscripcion inscripcion) {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO INSCRIPCIONES (CURSO_ID, ESTUDIANTE_ID, ANIO, SEMESTRE) KEY(CURSO_ID, ESTUDIANTE_ID) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, inscripcion.getCurso().getID());
                ps.setDouble(2, inscripcion.getEstudiante().getID());
                ps.setInt(3, inscripcion.getAño());
                ps.setInt(4, inscripcion.getSemestre());
                ps.executeUpdate();
            }

            boolean updated = false;
            for (int i = 0; i < listado.size(); i++) {
                Inscripcion cur = listado.get(i);
                if (cur.getCurso().getID() == inscripcion.getCurso().getID() &&
                        cur.getEstudiante().getID() == inscripcion.getEstudiante().getID()) {
                    listado.set(i, inscripcion);
                    updated = true;
                    break;
                }
            }
            if (!updated) listado.add(inscripcion);
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
    public List<Inscripcion> imprimirListado() {
        return new ArrayList<>(listado);
    }

    public void imprimirBaseDeDatos() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(DB_URL, "sa", "");
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM INSCRIPCIONES")) {
                System.out.println("\n=== TABLA INSCRIPCIONES ===");
                while (rs.next()) {
                    System.out.println(
                            "CursoID=" + rs.getInt("CURSO_ID") +
                                    ", EstudianteID=" + rs.getDouble("ESTUDIANTE_ID") +
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
                System.out.println("🔒 Conexión INSCRIPCIONES cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
