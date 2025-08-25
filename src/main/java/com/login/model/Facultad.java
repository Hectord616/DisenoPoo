package com.login.model;

import com.login.repository.InscripcionesPersonas;

public class Facultad {
    private double ID;
    private String nombre;
    private Persona decano;

    // Constructor completo
    public Facultad(double ID, String nombre, Persona decano) {
        this.ID = ID;
        this.nombre = nombre;
        this.decano = decano;
    }

    // Constructor alternativo (con búsqueda del decano en el repositorio)
    public Facultad(double ID, String nombre, double decanoId, InscripcionesPersonas personasRepo) {
        this.ID = ID;
        this.nombre = nombre;
        this.decano = (personasRepo != null) ? personasRepo.buscarPorId(decanoId) : null;
    }

    // 🔹 Nuevo: constructor simplificado SIN decano
    public Facultad(double ID, String nombre) {
        this.ID = ID;
        this.nombre = nombre;
        this.decano = null;
    }

    // Getters
    public double getID() { return ID; }
    public String getNombre() { return nombre; }
    public Persona getDecano() { return decano; }

    @Override
    public String toString() {
        return "Facultad{" +
                "ID=" + ID +
                ", nombre='" + nombre + '\'' +
                ", decano=" + (decano != null ? decano.getNombres() + " " + decano.getApellidos() : "Sin asignar") +
                '}';
    }
}
