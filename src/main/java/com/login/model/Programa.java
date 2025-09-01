package com.login.model;

import java.time.LocalDate;

public class Programa {
    private double ID;
    private String nombre;
    private double duracion;
    private LocalDate registro;
    private Facultad facultad;

    public Programa(double ID, String nombre, double duracion, LocalDate registro, Facultad facultad) {
        this.ID = ID;
        this.nombre = nombre;
        this.duracion = duracion;
        this.registro = registro;
        this.facultad = facultad;
    }

    public Programa(double ID, String nombre, Facultad facultad) {
        this.ID = ID;
        this.nombre = nombre;
        this.duracion = 0; // valor por defecto
        this.registro = LocalDate.now(); // fecha actual
        this.facultad = facultad;
    }

    public double getID() {
        return ID;
    }

    public void setID(double ID) {
        this.ID = ID;
    }

    public double getDuracion() {
        return duracion;
    }

    public void setDuracion(double duracion) {
        this.duracion = duracion;
    }

    public LocalDate getRegistro() {
        return registro;
    }

    public void setRegistro(LocalDate registro) {
        this.registro = registro;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Programa{" +
                "ID=" + ID +
                ", nombre='" + nombre + '\'' +
                ", duracion=" + duracion +
                ", registro=" + registro +
                ", facultad=" + (facultad != null ? facultad.getNombre() : "null") +
                '}';
    }
}
