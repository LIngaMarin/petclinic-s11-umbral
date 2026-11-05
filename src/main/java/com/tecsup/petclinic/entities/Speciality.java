package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.*;

/** Representa una especialidad y su consultorio según la tabla specialties existente. */
@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
public class Speciality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 80)
    private String name;
    @Column(length = 40)
    private String office;
    @Column(name = "h_open")
    private Integer openingHour;
    @Column(name = "h_close")
    private Integer closingHour;
}