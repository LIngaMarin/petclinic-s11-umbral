package com.tecsup.petclinic.exceptions;

/** Informa que la visita solicitada no está registrada. */
public class VisitNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Conserva el detalle de la búsqueda que no encontró resultados. */
    public VisitNotFoundException(String message) {
        super(message);
    }
}