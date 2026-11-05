package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Tabla vet_specialties.
 */
@Entity
@IdClass(VetSpecialtyId.class)
@Table(name = "vet_specialties")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VetSpecialty {

	@Id
	@Column(name = "vet_id")
	private Integer vetId;
	@Id
	@Column(name = "specialty_id")
	private Integer specialtyId;
	@Column(name = "certification_date")
	private LocalDate certificationDate;
	@Column(name = "years_experience")
	private Integer yearsExperience;
	@Column(name = "is_primary")
	private Boolean primarySpecialty;
	private String notes;
}
