package com.tecsup.petclinic.exceptions;

/** Indica que la especialidad solicitada no existe, igual que los otros servicios. */
public class SpecialityNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Conserva el detalle del identificador que no se encontró. */
    public SpecialityNotFoundException(String message) {
        super(message);
    }
}