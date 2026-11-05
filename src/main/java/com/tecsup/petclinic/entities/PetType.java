package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tabla types.
 */
@Entity
@Table(name = "types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String description;
	private Boolean active;
	@Column(name = "size_category")
	private String sizeCategory;
	@Column(name = "average_lifespan")
	private Integer averageLifespan;
	@Column(name = "care_level")
	private String careLevel;
}
