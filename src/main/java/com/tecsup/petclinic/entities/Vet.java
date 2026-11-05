package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla vets.
 */
@Entity
@Table(name = "vets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	private String email;
	private String phone;
	private Boolean active;
}
