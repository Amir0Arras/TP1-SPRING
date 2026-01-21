package pharmacie.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pharmacie.entity.Categorie;
import pharmacie.entity.Commande;
import pharmacie.entity.Dispensaire;
import pharmacie.entity.Ligne;
import pharmacie.entity.Medicament;

@DataJpaTest
public class QueriesTest {

    @Autowired
    private DispensaireRepository dispensaireRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneRepository ligneRepository;

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    private Dispensaire dispensaire;
    private Commande commandeEnvoyee;
    private Commande commandeEnCours;
    private Categorie categorie;

    @BeforeEach
    public void setUp() {
        // Créer une catégorie
        categorie = new Categorie();
        categorie.setLibelle("TestQueryCategory");
        categorieRepository.save(categorie);
        categorieRepository.flush();

        // Créer un dispensaire
        dispensaire = new Dispensaire();
        dispensaire.setNom("PharmacieTestQueries");
        dispensaire.setAdresse("123 Rue Test");
        dispensaire.setCodePostal("75001");
        dispensaire.setVille("Paris");
        dispensaire.setRegion("Île-de-France");
        dispensaire.setPays("France");
        dispensaireRepository.save(dispensaire);
        dispensaireRepository.flush();

        // Créer une commande ENVOYÉE
        commandeEnvoyee = new Commande();
        commandeEnvoyee.setDispensaire(dispensaire);
        commandeEnvoyee.setEnvoyeeLe(LocalDate.now().minusDays(5));
        commandeEnvoyee.setSaisieLe(LocalDate.now().minusDays(10));
        commandeEnvoyee.setPort(BigDecimal.ZERO);
        commandeEnvoyee.setRemise(BigDecimal.ZERO);
        commandeEnvoyee.setCodePostale("75001");
        commandeEnvoyee.setRegion("Île-de-France");
        commandeEnvoyee.setVille("Paris");
        commandeEnvoyee.setAdresse("123 Rue Test");
        commandeRepository.save(commandeEnvoyee);
        commandeRepository.flush();

        // Créer une commande EN COURS (envoyeeLe = null)
        commandeEnCours = new Commande();
        commandeEnCours.setDispensaire(dispensaire);
        commandeEnCours.setEnvoyeeLe(null); // En cours = pas envoyée
        commandeEnCours.setSaisieLe(LocalDate.now());
        commandeEnCours.setPort(BigDecimal.ZERO);
        commandeEnCours.setRemise(BigDecimal.ZERO);
        commandeEnCours.setCodePostale("75001");
        commandeEnCours.setRegion("Île-de-France");
        commandeEnCours.setVille("Paris");
        commandeEnCours.setAdresse("123 Rue Test");
        commandeRepository.save(commandeEnCours);
        commandeRepository.flush();

        // Créer des médicaments
        // Médicament 1: Disponible à la commande
        Medicament med1 = new Medicament();
        med1.setNom("Medicament1_Available");
        med1.setPrixUnitaire(BigDecimal.TEN);
        med1.setCategorie(categorie);
        med1.setUnitesEnStock(100); // Stock > commande
        med1.setUnitesCommandees(30);
        med1.setIndisponible(false);
        medicamentRepository.save(med1);

        // Médicament 2: Pas disponible (indisponible = true)
        Medicament med2 = new Medicament();
        med2.setNom("Medicament2_Unavailable");
        med2.setPrixUnitaire(BigDecimal.TEN);
        med2.setCategorie(categorie);
        med2.setUnitesEnStock(50);
        med2.setUnitesCommandees(20);
        med2.setIndisponible(true); // Indisponible
        medicamentRepository.save(med2);

        // Médicament 3: Pas de stock suffisant
        Medicament med3 = new Medicament();
        med3.setNom("Medicament3_InsufficientStock");
        med3.setPrixUnitaire(BigDecimal.TEN);
        med3.setCategorie(categorie);
        med3.setUnitesEnStock(10); // Stock < commande
        med3.setUnitesCommandees(50);
        med3.setIndisponible(false);
        medicamentRepository.save(med3);

        // Médicament 4: Stock = commande (borderline, devrait être disponible)
        Medicament med4 = new Medicament();
        med4.setNom("Medicament4_EqualStock");
        med4.setPrixUnitaire(BigDecimal.TEN);
        med4.setCategorie(categorie);
        med4.setUnitesEnStock(40);
        med4.setUnitesCommandees(40);
        med4.setIndisponible(false);
        medicamentRepository.save(med4);

        medicamentRepository.flush();

        // Ajouter des lignes à la commande envoyée
        Ligne ligne1 = new Ligne();
        ligne1.setCommande(commandeEnvoyee);
        ligne1.setMedicament(med1);
        ligne1.setQuantite(20);
        commandeEnvoyee.getLignes().add(ligne1);
        ligneRepository.save(ligne1);

        Ligne ligne2 = new Ligne();
        ligne2.setCommande(commandeEnvoyee);
        ligne2.setMedicament(med2);
        ligne2.setQuantite(15);
        commandeEnvoyee.getLignes().add(ligne2);
        ligneRepository.save(ligne2);

        // Ajouter une ligne à la commande en cours
        Ligne ligne3 = new Ligne();
        ligne3.setCommande(commandeEnCours);
        ligne3.setMedicament(med3);
        ligne3.setQuantite(25);
        commandeEnCours.getLignes().add(ligne3);
        ligneRepository.save(ligne3);

        ligneRepository.flush();
    }

    // ==============================================
    // TEST REQUÊTE 1: Nombre d'articles commandés
    // ==============================================

    @Test
    public void testCountArticlesOrderedByDispensaire_Should_ReturnTotalQuantity() {
        // Arrange: Dispensaire avec une commande envoyée de 20 + 15 = 35 articles
        Integer dispensaireCode = dispensaire.getCode();

        // Act
        Long totalArticles = commandeRepository.countArticlesOrderedByDispensaire(dispensaireCode);

        // Assert: Seule la commande envoyée compte (35 = 20 + 15)
        assertEquals(35L, totalArticles, "Doit compter 35 articles des commandes envoyées");
    }

    @Test
    public void testCountArticlesOrderedByDispensaire_Should_IgnoreOngoingCommandes() {
        // Arrange: La commande en cours (25 articles) ne doit pas être comptée
        Integer dispensaireCode = dispensaire.getCode();

        // Act
        Long totalArticles = commandeRepository.countArticlesOrderedByDispensaire(dispensaireCode);

        // Assert: Seule la commande ENVOYÉE compte (35)
        assertNotEquals(60L, totalArticles, "Ne doit pas compter la commande en cours");
        assertEquals(35L, totalArticles, "Doit compter seulement les articles envoyés");
    }

    @Test
    public void testCountArticlesOrderedByDispensaire_With_NoOrderedCommandes() {
        // Arrange: Créer un dispensaire sans commandes envoyées
        Dispensaire dispensaireSansCommande = new Dispensaire();
        dispensaireSansCommande.setNom("PharmacieEmpty");
        dispensaireSansCommande.setAdresse("456 Rue Test");
        dispensaireSansCommande.setCodePostal("75002");
        dispensaireSansCommande.setVille("Lyon");
        dispensaireSansCommande.setRegion("Rhône-Alpes");
        dispensaireSansCommande.setPays("France");
        dispensaireRepository.save(dispensaireSansCommande);
        dispensaireRepository.flush();

        // Act
        Long totalArticles = commandeRepository.countArticlesOrderedByDispensaire(dispensaireSansCommande.getCode());

        // Assert
        assertEquals(0L, totalArticles, "Doit retourner 0 pour un dispensaire sans commandes envoyées");
    }

    // ==============================================
    // TEST REQUÊTE 2: Commandes en cours
    // ==============================================

    @Test
    public void testFindOngoingCommandesByDispensaire_Should_ReturnOnlyNotShipped() {
        // Arrange
        Integer dispensaireCode = dispensaire.getCode();

        // Act
        List<Commande> ongoingCommandes = commandeRepository.findOngoingCommandesByDispensaire(dispensaireCode);

        // Assert
        assertEquals(1, ongoingCommandes.size(), "Doit retourner 1 commande en cours");
        assertTrue(ongoingCommandes.get(0).getEnvoyeeLe() == null, "La commande doit avoir envoyeeLe = null");
        assertEquals(commandeEnCours.getNumero(), ongoingCommandes.get(0).getNumero());
    }

    @Test
    public void testFindOngoingCommandesByDispensaire_Should_IgnoreShippedCommandes() {
        // Arrange
        Integer dispensaireCode = dispensaire.getCode();

        // Act
        List<Commande> ongoingCommandes = commandeRepository.findOngoingCommandesByDispensaire(dispensaireCode);

        // Assert
        assertFalse(ongoingCommandes.stream()
                .anyMatch(c -> c.getNumero().equals(commandeEnvoyee.getNumero())),
                "Ne doit pas inclure les commandes envoyées");
    }

    @Test
    public void testFindOngoingCommandesByDispensaire_With_NoOngoingCommandes() {
        // Arrange: Créer un dispensaire avec une seule commande envoyée
        Dispensaire dispensaireShipped = new Dispensaire();
        dispensaireShipped.setNom("PharmacieShipped");
        dispensaireShipped.setAdresse("789 Rue Test");
        dispensaireShipped.setCodePostal("75003");
        dispensaireShipped.setVille("Marseille");
        dispensaireShipped.setRegion("Provence");
        dispensaireShipped.setPays("France");
        dispensaireRepository.save(dispensaireShipped);
        dispensaireRepository.flush();

        Commande cmd = new Commande();
        cmd.setDispensaire(dispensaireShipped);
        cmd.setEnvoyeeLe(LocalDate.now()); // Déjà envoyée
        cmd.setSaisieLe(LocalDate.now().minusDays(1));
        cmd.setPort(BigDecimal.ZERO);
        cmd.setRemise(BigDecimal.ZERO);
        cmd.setCodePostale("75003");
        cmd.setRegion("Île-de-France");
        cmd.setVille("Paris");
        cmd.setAdresse("789 Rue Test");
        commandeRepository.save(cmd);
        commandeRepository.flush();

        // Act
        List<Commande> ongoingCommandes = commandeRepository.findOngoingCommandesByDispensaire(dispensaireShipped.getCode());

        // Assert
        assertEquals(0, ongoingCommandes.size(), "Ne doit retourner aucune commande en cours");
    }

    // ==============================================
    // TEST REQUÊTE 3: Médicaments disponibles
    // ==============================================

    @Test
    public void testFindAvailableMedicamentsByCategorie_Should_ReturnOnlyAvailable() {
        // Arrange
        Integer categorieCode = categorie.getCode();

        // Act
        List<Medicament> availableMedicaments = medicamentRepository.findAvailableMedicamentsByCategorie(categorieCode);

        // Assert: 2 médicaments disponibles (med1 et med4)
        assertEquals(2, availableMedicaments.size(), "Doit retourner 2 médicaments disponibles");
        
        // Vérifier que ce sont les bons médicaments
        assertTrue(availableMedicaments.stream()
                .anyMatch(m -> m.getNom().equals("Medicament1_Available")),
                "Doit inclure Medicament1_Available");
        assertTrue(availableMedicaments.stream()
                .anyMatch(m -> m.getNom().equals("Medicament4_EqualStock")),
                "Doit inclure Medicament4_EqualStock avec stock = commande");
    }

    @Test
    public void testFindAvailableMedicamentsByCategorie_Should_ExcludeIndisponible() {
        // Arrange
        Integer categorieCode = categorie.getCode();

        // Act
        List<Medicament> availableMedicaments = medicamentRepository.findAvailableMedicamentsByCategorie(categorieCode);

        // Assert
        assertFalse(availableMedicaments.stream()
                .anyMatch(m -> m.getNom().equals("Medicament2_Unavailable")),
                "Ne doit pas inclure les médicaments indisponibles");
    }

    @Test
    public void testFindAvailableMedicamentsByCategorie_Should_ExcludeInsufficientStock() {
        // Arrange
        Integer categorieCode = categorie.getCode();

        // Act
        List<Medicament> availableMedicaments = medicamentRepository.findAvailableMedicamentsByCategorie(categorieCode);

        // Assert
        assertFalse(availableMedicaments.stream()
                .anyMatch(m -> m.getNom().equals("Medicament3_InsufficientStock")),
                "Ne doit pas inclure les médicaments avec stock insuffisant");
    }

    @Test
    public void testFindAvailableMedicamentsByCategorie_Should_IncludeEqualStock() {
        // Arrange
        Integer categorieCode = categorie.getCode();

        // Act
        List<Medicament> availableMedicaments = medicamentRepository.findAvailableMedicamentsByCategorie(categorieCode);

        // Assert: Med4 a stock = commande (100 >= 50), donc disponible
        assertTrue(availableMedicaments.stream()
                .anyMatch(m -> m.getNom().equals("Medicament4_EqualStock")),
                "Doit inclure les médicaments où stock = commande");
    }

    @Test
    public void testFindAvailableMedicamentsByCategorie_With_EmptyCategory() {
        // Arrange: Créer une catégorie vide
        Categorie emptyCategory = new Categorie();
        emptyCategory.setLibelle("EmptyCategory");
        categorieRepository.save(emptyCategory);
        categorieRepository.flush();

        // Act
        List<Medicament> availableMedicaments = medicamentRepository.findAvailableMedicamentsByCategorie(emptyCategory.getCode());

        // Assert
        assertEquals(0, availableMedicaments.size(), "Doit retourner une liste vide pour une catégorie sans médicaments");
    }

    @Test
    public void testFindAvailableMedicamentsByCategorie_Should_BeSorted() {
        // Arrange
        Integer categorieCode = categorie.getCode();

        // Act
        List<Medicament> availableMedicaments = medicamentRepository.findAvailableMedicamentsByCategorie(categorieCode);

        // Assert: Vérifier le tri alphabétique par nom
        if (availableMedicaments.size() >= 2) {
            for (int i = 0; i < availableMedicaments.size() - 1; i++) {
                assertTrue(availableMedicaments.get(i).getNom().compareTo(availableMedicaments.get(i + 1).getNom()) <= 0,
                        "Les médicaments doivent être triés par nom");
            }
        }
    }
}
