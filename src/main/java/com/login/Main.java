package com.login;

import com.login.model.*;
import com.login.repository.CursosInscritos;
import com.login.repository.CursosProfesores;
import com.login.repository.InscripcionesPersonas;
import com.login.views.InterfazGrafica;

import javax.swing.*;
import java.util.List;

public class Main {


    public static Facultad facultad = new Facultad(1, "Facultad de Ingeniería");
    public static Programa programa = new Programa(101, "Ingeniería de Sistemas", facultad);

    /**
     * Método para crear un curso genérico si no se encuentra en la lista.
     */
    public static Curso crearCursoGenerico(int id) {
        return new Curso(
                id,
                (id == 101) ? "Matemáticas Básicas" :
                        (id == 102) ? "Programación I" :
                                "Curso genérico",
                programa,
                true);
    }

    /**
     * Convierte una Persona en Estudiante usando el programa global.
     */
    public static Estudiante crearEstudianteDesdePersona(Persona p) {
        return new Estudiante(
                p.getID(),
                p.getNombres(),
                p.getApellidos(),
                p.getEmail(),
                p.getID(),
                programa,
                true,
                0.0);
    }

    /**
     * Método principal que inicia la aplicación.
     */
    public static void main(String[] args) {
        System.out.println("=== Aplicación Universitaria (GUI) ===");

        // Repositorios
        InscripcionesPersonas personas = new InscripcionesPersonas();
        CursosProfesores cursosProfesores = new CursosProfesores(programa);
        CursosInscritos cursosInscritos = new CursosInscritos(programa);

        // Cargar datos iniciales
        personas.cargarDatos();
        List<Persona> listaPersonas = personas.getListado();
        cursosProfesores.cargarDatos(listaPersonas);
        List<Curso> listaCursos = cursosProfesores.getCursos();
        cursosInscritos.cargarDatos(listaPersonas, listaCursos);

        // Verificar que los datos se cargaron
        System.out.println("✅ Personas cargadas: " + personas.cantidadActual());
        System.out.println("✅ Cursos asignados a profesores: " + cursosProfesores.cantidadActual());
        System.out.println("✅ Inscripciones de estudiantes: " + cursosInscritos.cantidadActual());

        // Iniciar interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                new InterfazGrafica(personas, cursosProfesores, cursosInscritos, listaCursos);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al iniciar la interfaz: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}