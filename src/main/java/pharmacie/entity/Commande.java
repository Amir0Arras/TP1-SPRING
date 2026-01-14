package pharmacie.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString

public class Commande {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer numero;

    @NonNull
    @NotNull
    @Column(name = "envoyee_le", nullable = false)
    private LocalDate envoyeeLe;

    @NonNull
    @NotNull
    @Column(name = "saisie_le", nullable = false)
    private LocalDate saisieLe;

    @NotNull
    @Digits(integer = 16, fraction = 2)
    @Column(precision = 18, scale = 2)
    private BigDecimal port;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @Column(precision = 10, scale = 2)
    private BigDecimal remise;

    /* ==========================
       Relation avec Dispensaire
       ========================== */
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "dispensaire_code",
        nullable = false,
        referencedColumnName = "code",
        foreignKey = @ForeignKey(name = "fk_commande_dispensaire")
    )
    private Dispensaire dispensaire;

    /* ==========================
       Adresse destinataire
       ========================== */
    @NotBlank
    @Size(max = 25)
    @Column(name = "code_postale", length = 25, nullable = false)
    private String codePostale;

    @NotBlank
    @Size(max = 25)
    @Column(length = 25, nullable = false)
    private String region;

    @NotBlank
    @Size(max = 25)
    @Column(length = 25, nullable = false)
    private String ville;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String adresse;
}

