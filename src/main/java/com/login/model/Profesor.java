package com.login.model;

public class Profesor extends Persona {
    private String tipoContrato;

    public Profesor(double ID, String nombres, String apellidos, String email, String tipoContrato) {
        super(ID, nombres, apellidos, email);
        this.tipoContrato = tipoContrato;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public double getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "tipoContrato='" + tipoContrato + '\'' +
                ", persona=" + super.toString() +
                '}';
    }

}