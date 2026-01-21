package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Categorie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE) // la clé est auto-générée par la BD, On ne veut pas de "setter"
	private Integer code;

	@NotBlank // pour éviter les libellés vides
	@Size(min = 1, max = 255)
	@Column(unique=true, length = 255, nullable = false)
	private String libelle;

	@Size(max = 255)
	@Column(length = 255)
	private String description;

	@ToString.Exclude
	// CascadeType.MERGE et PERSIST uniquement, pas de DELETE
	// On ne peut pas supprimer une catégorie qui a des médicaments
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "categorie")
	private List<Medicament> medicaments = new LinkedList<>();

}
