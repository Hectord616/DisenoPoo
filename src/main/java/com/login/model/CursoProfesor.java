package com.login.model;

public class CursoProfesor {
    private Profesor profesor;
    private int año;
    private int semestre;
    private Curso curso;

    public CursoProfesor(Profesor profesor, int año, int semestre, Curso curso) {
        this.profesor = profesor;
        this.año = año;
        this.semestre = semestre;
        this.curso = curso;
    }

    // GETTERS
    public Profesor getProfesor() { return profesor; }
    public int getAño() { return año; }
    public int getSemestre() { return semestre; }
    public Curso getCurso() { return curso; }

    @Override
public String toString() {
    return "CursoProfesor{" +
            "profesor=" + profesor +
            ", año=" + año +
            ", semestre=" + semestre +
            ", curso=" + curso +
            '}';
}

}