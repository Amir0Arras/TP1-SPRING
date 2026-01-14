package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Dispensaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer code;

    @NonNull
    @NotBlank
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NonNull
    @NotBlank
    @Size(min = 1, max = 60)
    @Column(nullable = false, length = 60)
    private String adresse;

    @NonNull
    @NotBlank
    @Size(min = 1, max = 10)
    @Column(name = "code_postal", nullable = false, length = 10)
    private String codePostal;

    @NonNull
    @NotBlank
    @Size(min = 1, max = 15)
    @Column(nullable = false, length = 15)
    private String ville;

    @NonNull
    @NotBlank
    @Size(min = 1, max = 15)
    @Column(nullable = false, length = 15)
    private String region;

    @NonNull
    @NotBlank
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String pays;

    @Size(max = 20)
    @Pattern(
        regexp = "^\\+?[0-9\\s-]{6,20}$",
        message = "Numéro de téléphone invalide"
    )
    @Column(length = 20)
    private String telephone;

    @Size(max = 20)
    @Pattern(
        regexp = "^\\+?[0-9\\s-]{6,20}$",
        message = "Numéro de fax invalide"
    )
    @Column(length = 20)
    private String fax;

    @Size(max = 100)
    @Column(length = 100)
    private String contact;

    @Size(max = 100)
    @Column(length = 100)
    private String fonction;
}