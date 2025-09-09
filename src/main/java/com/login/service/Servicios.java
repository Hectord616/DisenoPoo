package com.login.service;

import java.util.List;

public interface Servicios<T> {
    String imprimirPosicion(int posicion);
    int cantidadActual();
    List<T> imprimirListado();
}
