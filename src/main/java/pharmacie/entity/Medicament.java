package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class Medicament {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE) // la clé est autogénérée par la BD, On ne veut pas de "setter"
	private Integer reference = null;

	@Column(unique=true, length = 255, nullable = false)
	private String nom;

	@Column(name = "quantite_par_unite", length = 255)
	private String quantiteParUnite = "Une boîte de 12";

	@PositiveOrZero
	@Column(name = "prix_unitaire", precision = 10, scale = 2)
	private BigDecimal prixUnitaire = BigDecimal.TEN;

	/**
	 * Nombre d'unités en stock
	 * Décrémenté quand on expédie une commande contenant ce médicament
	 */
	@ToString.Exclude
	@PositiveOrZero
	@Column(name = "unites_en_stock")
	private int unitesEnStock = 0;

	/**
	 * Nombre d'unités "en commande"
	 * Un médicament est "en commande" si il est dans une commande qui n'est pas encore expédiée
	 * Incrementé quand on ajoute des unités de ce médicament à une ligne de commande
	 * Décrémenté quand on expédie une commande contenant ce médicament
	 */
	@ToString.Exclude
	@PositiveOrZero
	@Column(name = "unites_commandees")
	private int unitesCommandees = 0;

	/**
	 * Niveau de reapprovisionnement
	 * Si le stock devient inférieur ou égal à ce niveau, 
	 * on doit approvisionner de nouvelles unités de ce médicament auprès d'un fournisseur
	 */
	@ToString.Exclude
	@PositiveOrZero
	@Column(name = "niveau_de_reappro")
	private int niveauDeReappro = 0;

	/**
	 * Indique si le médicament est indisponible
	 */
	@ToString.Exclude
	@Column(name = "indisponible")
	private boolean indisponible = false;

	@Column(name = "image_url", length = 500)
	private String imageURL;

	@ManyToOne(optional = false)
	@JoinColumn(name = "categorie_code", nullable = false)
	@ToString.Exclude
	private Categorie categorie ;

}
