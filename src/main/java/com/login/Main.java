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
        System.out.println("=== Aplicación Universitaria (GUI + Consola) ===");

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
        System.out.println(" Personas cargadas: " + personas.cantidadActual());
        System.out.println(" Cursos asignados a profesores: " + cursosProfesores.cantidadActual());
        System.out.println(" Inscripciones de estudiantes: " + cursosInscritos.cantidadActual());

        // Hilo para la interfaz gráfica
        Thread guiThread = new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    new InterfazGrafica(personas, cursosProfesores, cursosInscritos, listaCursos);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error al iniciar la interfaz: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            });
        });
        guiThread.start();

        // Hilo para la consola
        Thread consolaThread = new Thread(() -> {
            ejecutarConsola(personas, cursosProfesores, cursosInscritos, listaCursos);
        });
        consolaThread.start();
    }

    /**
     * Método para ejecutar la lógica de la aplicación por consola.
     */
    public static void ejecutarConsola(
            InscripcionesPersonas personas,
            CursosProfesores cursosProfesores,
            CursosInscritos cursosInscritos,
            List<Curso> listaCursos) {
        // Aquí puedes implementar el menú y la lógica de la consola
        System.out.println("=== Modo Consola Activo ===");
        // Ejemplo simple:
        System.out.println("Ingrese 'salir' para terminar la consola.");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input;
        do {
            System.out.print("> ");
            input = scanner.nextLine();
            if ("personas".equalsIgnoreCase(input)) {
                System.out.println("Listado de personas:");
                for (Persona p : personas.getListado()) {
                    System.out.println(p);
                }
            } else if ("cursos".equalsIgnoreCase(input)) {
                System.out.println("Listado de cursos:");
                for (Curso c : listaCursos) {
                    System.out.println(c);
                }
            } else if (!"salir".equalsIgnoreCase(input)) {
                System.out.println("Comando no reconocido.");
            }
        } while (!"salir".equalsIgnoreCase(input));
        System.out.println("Cerrando modo consola...");
    }
}