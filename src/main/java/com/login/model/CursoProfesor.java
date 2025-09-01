package com.login.model;

public class CursoProfesor {
    private Profesor profesor;
    private int anio;
    private int semestre;
    private Curso curso;

    public CursoProfesor(Profesor profesor, int anio, int semestre, Curso curso) {
        this.profesor = profesor;
        this.anio = anio;
        this.semestre = semestre;
        this.curso = curso;
    }

    public Profesor getProfesor() { return profesor; }
    public int getAnio() { return anio; }
    public int getSemestre() { return semestre; }
    public Curso getCurso() { return curso; }

    @Override
    public String toString() {
        return "CursoProfesor {" +
                "profesor=" + profesor.getNombres() + " " + profesor.getApellidos() +
                ", anio=" + anio +
                ", semestre=" + semestre +
                ", curso=" + curso.getNombre() +
                '}';
    }
}
