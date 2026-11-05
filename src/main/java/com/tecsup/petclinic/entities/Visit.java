package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Visita veterinaria según visits: mascota y fecha obligatorias; veterinario opcional.
 * Las relaciones no propagan guardado ni borrado a mascotas o veterinarios.
 */
@Entity
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id")
    private Vet vet;
    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;
    @Column(length = 255)
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal cost;
}