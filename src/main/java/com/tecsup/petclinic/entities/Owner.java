package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla owners.
 */
@Entity
@Table(name = "owners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Owner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	private String address;
	private String city;
	private String telephone;
}
