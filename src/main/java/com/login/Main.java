package com.login;

import com.login.model.*;
import com.login.repository.CursosInscritos;
import com.login.repository.CursosProfesores;
import com.login.repository.InscripcionesPersonas;
import com.login.views.InterfazGrafica;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static List<Facultad> listaFacultades = new ArrayList<>();
    public static List<Programa> listaProgramas = new ArrayList<>();
    public static List<Curso> listaCursos = new ArrayList<>();

    public static Facultad facultad = new Facultad(1, "Facultad de Ingeniería");
    public static Programa programa = new Programa(101, "Ingeniería de Sistemas", facultad);

    public static void main(String[] args) {
        System.out.println("=== Aplicación Universitaria (GUI + Consola) ===");

        // Inicializar repositorios
        InscripcionesPersonas personas = new InscripcionesPersonas();
        CursosProfesores cursosProfesores = new CursosProfesores(programa);
        CursosInscritos cursosInscritos = new CursosInscritos(programa);

        // Cargar datos iniciales
        personas.cargarDatos();
        List<Persona> listaPersonas = personas.getListado();
        cursosProfesores.cargarDatos(listaPersonas);
        listaCursos.addAll(cursosProfesores.getCursos());
        cursosInscritos.cargarDatos(listaPersonas, listaCursos);

        // Agregar facultad y programa por defecto
        listaFacultades.add(facultad);
        listaProgramas.add(programa);

        // Verificar datos
        System.out.println("Personas cargadas: " + personas.cantidadActual());
        System.out.println("Cursos asignados a profesores: " + cursosProfesores.cantidadActual());
        System.out.println("Inscripciones de estudiantes: " + cursosInscritos.cantidadActual());

        // Hilo GUI
        Thread guiThread = new Thread(() -> SwingUtilities.invokeLater(() -> {
            try {
                new InterfazGrafica(personas, cursosProfesores, cursosInscritos, listaCursos);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al iniciar la interfaz: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }));
        guiThread.start();

        // Hilo consola
        Thread consolaThread = new Thread(() -> ejecutarConsola(personas, cursosProfesores, cursosInscritos));
        consolaThread.start();
    }

    // ================== MÉTODOS AUXILIARES ==================
    public static Facultad crearFacultad(String nombre) {
        int id = listaFacultades.size() + 1;
        Facultad fac = new Facultad(id, nombre);
        listaFacultades.add(fac);
        return fac;
    }

    public static Programa crearPrograma(String nombre, Facultad facultad) {
        int id = listaProgramas.size() + 1;
        Programa prog = new Programa(id, nombre, facultad);
        listaProgramas.add(prog);
        return prog;
    }

    public static Curso crearCurso(String nombre, Programa programa) {
        int id = listaCursos.size() + 100;
        Curso curso = new Curso(id, nombre, programa, true);
        listaCursos.add(curso);
        return curso;
    }

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

    // ================== CONSOLA ==================
    public static void ejecutarConsola(InscripcionesPersonas personas,
                                       CursosProfesores cursosProfesores,
                                       CursosInscritos cursosInscritos) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("=== Modo Consola Activo ===");
        System.out.println("Comandos: salir, personas, cursos, asignaciones, inscripciones, crearfacultad, crearprograma, crearcurso, inscribir, asignar");

        do {
            System.out.print("> ");
            input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "salir":
                    System.out.println("Cerrando consola...");
                    break;

                case "personas":
                    mostrarTablaPersonas(personas.getListado());
                    break;

                case "cursos":
                    mostrarTablaCursos(listaCursos);
                    break;

                case "asignaciones":
                    mostrarTablaCursosProfesores(cursosProfesores.imprimirListado());
                    break;

                case "inscripciones":
                    mostrarTablaInscripciones(cursosInscritos.imprimirListado());
                    break;

                case "crearfacultad":
                    System.out.print("Nombre de la facultad: ");
                    String nomFac = scanner.nextLine();
                    Facultad f = crearFacultad(nomFac);
                    System.out.println("Facultad creada: " + f.getNombre());
                    break;

                case "crearprograma":
                    if (listaFacultades.isEmpty()) { System.out.println("No hay facultades."); break; }
                    System.out.println("Seleccione facultad por ID:");
                    listaFacultades.forEach(fac -> System.out.println(fac.getID() + " - " + fac.getNombre()));
                    int idFac = Integer.parseInt(scanner.nextLine());
                    Facultad facSel = listaFacultades.stream().filter(fac -> fac.getID() == idFac).findFirst().orElse(null);
                    if (facSel == null) { System.out.println("Facultad no encontrada."); break; }
                    System.out.print("Nombre del programa: ");
                    String nomProg = scanner.nextLine();
                    Programa prog = crearPrograma(nomProg, facSel);
                    System.out.println("Programa creado: " + prog.getNombre() + " en " + facSel.getNombre());
                    break;

                case "crearcurso":
                    if (listaProgramas.isEmpty()) { System.out.println("No hay programas."); break; }
                    System.out.println("Seleccione programa por ID:");
                    listaProgramas.forEach(p -> System.out.println(p.getID() + " - " + p.getNombre()));
                    int idProg = Integer.parseInt(scanner.nextLine());
                    Programa progSel = listaProgramas.stream().filter(p -> p.getID() == idProg).findFirst().orElse(null);
                    if (progSel == null) { System.out.println("Programa no encontrado."); break; }
                    System.out.print("Nombre del curso: ");
                    String nomCurso = scanner.nextLine();
                    Curso c = crearCurso(nomCurso, progSel);
                    System.out.println("Curso creado: " + c.getNombre() + " en " + progSel.getNombre());
                    break;

                case "inscribir":
                    if (personas.getListado().isEmpty() || listaCursos.isEmpty()) { System.out.println("No hay personas o cursos."); break; }
                    inscribirEstudiante(scanner, personas, cursosInscritos);
                    break;

                case "asignar":
                    if (personas.getListado().isEmpty() || listaCursos.isEmpty()) { System.out.println("No hay personas o cursos."); break; }
                    asignarProfesor(scanner, personas, cursosProfesores);
                    break;

                default:
                    System.out.println("Comando no reconocido.");
            }

        } while (!input.equals("salir"));
    }

    // ================== MÉTODOS DE TABLA ==================
    private static void mostrarTablaPersonas(List<Persona> lista) {
        System.out.printf("%-5s %-15s %-15s %-25s%n", "ID", "Nombres", "Apellidos", "Email");
        System.out.println("------------------------------------------------------------");
        for (Persona p : lista) {
            System.out.printf("%-5s %-15s %-15s %-25s%n",
                    p.getID(), p.getNombres(), p.getApellidos(), p.getEmail());
        }
    }

    private static void mostrarTablaCursos(List<Curso> lista) {
        System.out.printf("%-5s %-25s %-20s%n", "ID", "Nombre", "Programa");
        System.out.println("-------------------------------------------------");
        for (Curso c : lista) {
            System.out.printf("%-5s %-25s %-20s%n", c.getID(), c.getNombre(), c.getPrograma().getNombre());
        }
    }

    private static void mostrarTablaCursosProfesores(List<CursoProfesor> lista) {
        System.out.printf("%-25s %-25s %-5s %-5s%n", "Profesor", "Curso", "Año", "Sem");
        System.out.println("-------------------------------------------------------------");
        for (CursoProfesor cp : lista) {
            System.out.printf("%-25s %-25s %-5s %-5s%n",
                    cp.getProfesor().getNombres() + " " + cp.getProfesor().getApellidos(),
                    cp.getCurso().getNombre(),
                    cp.getAnio(),
                    cp.getSemestre());
        }
    }

    private static void mostrarTablaInscripciones(List<Inscripcion> lista) {
        System.out.printf("%-25s %-25s %-5s %-5s%n", "Estudiante", "Curso", "Año", "Sem");
        System.out.println("-------------------------------------------------------------");
        for (Inscripcion ins : lista) {
            System.out.printf("%-25s %-25s %-5s %-5s%n",
                    ins.getEstudiante().getNombres() + " " + ins.getEstudiante().getApellidos(),
                    ins.getCurso().getNombre(),
                    ins.getAño(),
                    ins.getSemestre());
        }
    }

    // ================== MÉTODOS DE INSCRIPCIÓN Y ASIGNACIÓN ==================
    private static void inscribirEstudiante(Scanner scanner, InscripcionesPersonas personas, CursosInscritos cursosInscritos) {
        List<Persona> persList = personas.getListado();
        for (int i = 0; i < persList.size(); i++) System.out.println(i + ": " + persList.get(i));
        System.out.print("Seleccione persona por índice: ");
        int idx = Integer.parseInt(scanner.nextLine());
        Persona p = persList.get(idx);
        Estudiante e = (p instanceof Estudiante) ? (Estudiante) p : crearEstudianteDesdePersona(p);

        for (int i = 0; i < listaCursos.size(); i++) System.out.println(i + ": " + listaCursos.get(i));
        System.out.print("Seleccione curso por índice: ");
        int idxCurso = Integer.parseInt(scanner.nextLine());
        Curso curso = listaCursos.get(idxCurso);

        System.out.print("Año: "); int año = Integer.parseInt(scanner.nextLine());
        System.out.print("Semestre: "); int semestre = Integer.parseInt(scanner.nextLine());

        Inscripcion ins = new Inscripcion(curso, año, semestre, e);
        cursosInscritos.guardarInformacion(ins);
        System.out.println("Inscripción realizada: " + ins);
    }

    private static void asignarProfesor(Scanner scanner, InscripcionesPersonas personas, CursosProfesores cursosProfesores) {
        List<Persona> docentes = personas.getListado();
        for (int i = 0; i < docentes.size(); i++) System.out.println(i + ": " + docentes.get(i));
        System.out.print("Seleccione docente por índice: ");
        int idxDoc = Integer.parseInt(scanner.nextLine());
        Persona doc = docentes.get(idxDoc);
        Profesor prof = (doc instanceof Profesor) ? (Profesor) doc :
                new Profesor(doc.getID(), doc.getNombres(), doc.getApellidos(), doc.getEmail(), "Tiempo Completo");

        for (int i = 0; i < listaCursos.size(); i++) System.out.println(i + ": " + listaCursos.get(i));
        System.out.print("Seleccione curso por índice: ");
        int idxC = Integer.parseInt(scanner.nextLine());
        Curso cur = listaCursos.get(idxC);

        System.out.print("Año: "); int año = Integer.parseInt(scanner.nextLine());
        System.out.print("Semestre: "); int semestre = Integer.parseInt(scanner.nextLine());

        CursoProfesor cp = new CursoProfesor(prof, año, semestre, cur);
        cursosProfesores.agregar(cp);
        System.out.println("Curso asignado: " + cp);
    }
}
