package com.login;

import com.login.model.*;
import com.login.repository.CursosInscritos;
import com.login.repository.CursosProfesores;
import com.login.repository.InscripcionesPersonas;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Aplicación Universitaria ===\n");

        // Inicializar repositorios
        InscripcionesPersonas personas = new InscripcionesPersonas();
        CursosProfesores cursosProfesores = new CursosProfesores();
        CursosInscritos cursosInscritos = new CursosInscritos();

        // Cargar datos desde H2
        personas.cargarDatos();
        List<Persona> listaPersonas = personas.getListado();
        cursosProfesores.cargarDatos(listaPersonas);
        List<Curso> listaCursos = cursosProfesores.getCursos();
        cursosInscritos.cargarDatos(listaPersonas, listaCursos);

        // Mostrar información
        System.out.println("Personas registradas:");
        personas.imprimirListado().forEach(System.out::println);

        System.out.println("\nCursos asignados a profesores:");
        cursosProfesores.imprimirListado().forEach(System.out::println);

        System.out.println("\nCursos inscritos por estudiantes:");
        cursosInscritos.imprimirListado().forEach(System.out::println);

        // Agregar nueva persona
        Persona nuevaPersona = new Persona(6, "Luis", "Martinez", "luis@uni.com");
        personas.inscribir(nuevaPersona);

        System.out.println("\nDespués de agregar nueva persona:");
        personas.imprimirListado().forEach(System.out::println);

        // Cerrar conexiones
        personas.cerrarConexion();
        cursosProfesores.cerrarConexion();
        cursosInscritos.cerrarConexion();

        System.out.println("\nAplicación finalizada.");
        System.exit(0);
    }
}
