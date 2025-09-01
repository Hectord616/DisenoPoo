package com.login.model;

public class Inscripcion {
    private Curso curso;
    private int año;
    private int semestre;
    private Estudiante estudiante;

    public Inscripcion(Curso curso, int año, int semestre, Estudiante estudiante) {
        this.curso = curso;
        this.año = año;
        this.semestre = semestre;
        this.estudiante = estudiante;
    }

    public Curso getCurso() {
        return curso;
    }

    public int getAño() {
        return año;
    }

    public int getSemestre() {
        return semestre;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    @Override
    public String toString() {
        String estudianteStr = (estudiante != null) ? estudiante.toString() : "Sin estudiante";
        return "Inscripción -> " +
                curso +
                ", Año: " + año +
                ", Semestre: " + semestre +
                ", " + estudianteStr;
    }
}
