package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString

public class Ligne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    /* ==========================
       Relation vers Medicament
       ========================== */
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "medicament_reference",         // colonne FK dans cette table
        referencedColumnName = "reference",    // clé primaire de Medicament
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_detail_medicament")
    )
    private Medicament medicament;

    /* ==========================
       Relation vers Commande
       ========================== */
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "commande_numero",              // colonne FK dans cette table
        referencedColumnName = "numero",       // clé primaire de Commande
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_detail_commande")
    )
    private Commande commande;

    /* ==========================
       Quantité
       ========================== */
    @Min(1)
    @Column(nullable = false)
    private Integer quantite;
}
