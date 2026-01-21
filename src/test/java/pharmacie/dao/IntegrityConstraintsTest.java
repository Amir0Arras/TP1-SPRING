package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import pharmacie.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests des contraintes d'intégrité (Diapositive 64):
 * 1. Un médicament doit avoir une catégorie (NOT NULL contraint)
 * 2. On peut supprimer une catégorie qui n'a pas de médicaments
 * 3. On ne peut pas supprimer une catégorie qui a des médicaments (Contrainte FK)
 * 4. Quand on supprime une commande, on supprime ses lignes (CASCADE DELETE)
 * 5. Quand on supprime un dispensaire, on supprime ses commandes (CASCADE DELETE)
 */
@DataJpaTest
public class IntegrityConstraintsTest {

    @Autowired
    private CategorieRepository categorieRepository;
    
    @Autowired
    private MedicamentRepository medicamentRepository;
    
    @Autowired
    private CommandeRepository commandeRepository;
    
    @Autowired
    private DispensaireRepository dispensaireRepository;
    
    @Autowired
    private LigneRepository ligneRepository;

    // ============================================================
    // CONTRAINTE 1: Un médicament DOIT avoir une catégorie (NOT NULL)
    // ============================================================

    /**
     * TEST 1.1: Un médicament sans catégorie lève DataIntegrityViolationException
     */
    @Test
    public void testMedicamentWithoutCategoryThrowsException() {
        Medicament med = new Medicament();
        med.setNom("MedicamentSansCategorie_" + System.currentTimeMillis());
        med.setPrixUnitaire(BigDecimal.TEN);
        med.setCategorie(null); // Violation du NOT NULL

        assertThrows(DataIntegrityViolationException.class, () -> {
            medicamentRepository.save(med);
            medicamentRepository.flush();
        });
    }

    /**
     * TEST 1.2: Un médicament avec une catégorie valide peut être créé
     */
    @Test
    public void testMedicamentWithValidCategoryCanBeCreated() {
        // Créer une catégorie
        Categorie cat = new Categorie();
        cat.setLibelle("Categorie_" + System.currentTimeMillis());
        cat.setDescription("Catégorie test");
        Categorie savedCat = categorieRepository.save(cat);
        categorieRepository.flush();

        // Créer un médicament avec cette catégorie
        Medicament med = new Medicament();
        med.setNom("Medicament_" + System.currentTimeMillis());
        med.setPrixUnitaire(BigDecimal.TEN);
        med.setCategorie(savedCat);

        Medicament saved = medicamentRepository.save(med);
        medicamentRepository.flush();
        
        assertNotNull(saved.getReference());
        assertEquals(savedCat.getCode(), saved.getCategorie().getCode());
    }

    // ============================================================
    // CONTRAINTE 2: On peut supprimer une catégorie SANS médicaments
    // ============================================================

    /**
     * TEST 2: Une catégorie vide peut être supprimée
     */
    @Test
    @SuppressWarnings("null")
    public void testCanDeleteEmptyCategoryWithoutMedicaments() {
        // Créer une catégorie vide
        Categorie cat = new Categorie();
        cat.setLibelle("CategorieVide_" + System.currentTimeMillis());
        cat.setDescription("Catégorie sans médicaments");
        Categorie saved = categorieRepository.save(cat);
        categorieRepository.flush();
        
        Integer categoryCode = saved.getCode();
        assertTrue(categorieRepository.existsById(categoryCode));

        // Supprimer la catégorie (elle est vide, pas de FK constraint)
        categorieRepository.deleteById(categoryCode);
        categorieRepository.flush();

        // Vérifier qu'elle est bien supprimée
        assertFalse(categorieRepository.existsById(categoryCode));
    }

    // ============================================================
    // CONTRAINTE 3: On NE PEUT PAS supprimer une catégorie avec médicaments
    // ============================================================

    /**
     * TEST 3: Supprimer une catégorie qui a des médicaments lève une exception
     * La contrainte de clé étrangère (Foreign Key Constraint) empêche la suppression
     */
    @Test
    @SuppressWarnings("null")
    public void testCannotDeleteCategoryWithMedicaments() {
        // 1. Créer une catégorie
        Categorie cat = new Categorie();
        cat.setLibelle("CategorieAvecMed_" + System.currentTimeMillis());
        cat.setDescription("Catégorie avec médicaments");
        Categorie savedCat = categorieRepository.save(cat);
        
        // 2. Créer un médicament dans cette catégorie
        Medicament med = new Medicament();
        med.setNom("MedicamentFK_" + System.currentTimeMillis());
        med.setPrixUnitaire(BigDecimal.TEN);
        med.setCategorie(savedCat);
        medicamentRepository.save(med);
        medicamentRepository.flush();

        Integer categoryCode = savedCat.getCode();

        // 3. Essayer de supprimer la catégorie
        // Cela devrait lever DataIntegrityViolationException car la FK le refuse
        assertThrows(DataIntegrityViolationException.class, () -> {
            categorieRepository.deleteById(categoryCode);
            medicamentRepository.flush();  // Force le check de la contrainte FK
        });

        // 4. Vérifier que la catégorie existe toujours
        assertTrue(categorieRepository.existsById(categoryCode));
    }

    // ============================================================
    // CONTRAINTE 4: Supprimer une Commande → supprime ses Lignes (CASCADE DELETE)
    // ============================================================

    /**
     * TEST 4: Quand on supprime une Commande, ses Lignes sont supprimées par cascade
     */
    @Test
    @SuppressWarnings("null")
    public void testDeletingCommandeDeletesItsLignesByCascade() {
        // 1. Créer un Dispensaire
        Dispensaire dispensaire = new Dispensaire();
        dispensaire.setNom("Dispensaire_" + System.currentTimeMillis());
        dispensaire.setAdresse("123 Rue Test");
        dispensaire.setCodePostal("75000");
        dispensaire.setVille("Paris");
        dispensaire.setRegion("Île-de-France");
        dispensaire.setPays("France");
        Dispensaire savedDispensaire = dispensaireRepository.save(dispensaire);

        // 2. Créer une Catégorie
        Categorie cat = new Categorie();
        cat.setLibelle("Categorie_" + System.currentTimeMillis());
        cat.setDescription("Catégorie test");
        Categorie savedCat = categorieRepository.save(cat);

        // 3. Créer un Médicament
        Medicament med = new Medicament();
        med.setNom("Medicament_" + System.currentTimeMillis());
        med.setPrixUnitaire(BigDecimal.TEN);
        med.setCategorie(savedCat);
        Medicament savedMed = medicamentRepository.save(med);

        // 4. Créer une Commande
        Commande commande = new Commande();
        commande.setDispensaire(savedDispensaire);
        commande.setSaisieLe(LocalDate.now());
        commande.setEnvoyeeLe(null);  // Commande pas encore expédiée
        commande.setPort(BigDecimal.ZERO);
        commande.setRemise(BigDecimal.ZERO);
        commande.setCodePostale("75000");
        commande.setRegion("Île-de-France");
        commande.setVille("Paris");
        commande.setAdresse("123 Rue Destination");
        Commande savedCommande = commandeRepository.save(commande);
        commandeRepository.flush();

        // 5. Créer 2 Lignes de commande
        Ligne ligne1 = new Ligne();
        ligne1.setCommande(savedCommande);
        ligne1.setMedicament(savedMed);
        ligne1.setQuantite(10);
        Ligne savedLigne1 = ligneRepository.save(ligne1);

        Ligne ligne2 = new Ligne();
        ligne2.setCommande(savedCommande);
        ligne2.setMedicament(savedMed);
        ligne2.setQuantite(5);
        Ligne savedLigne2 = ligneRepository.save(ligne2);

        ligneRepository.flush();

        Integer commandeId = savedCommande.getNumero();
        Integer ligne1Id = savedLigne1.getId();
        Integer ligne2Id = savedLigne2.getId();

        // 6. Vérifier que tout existe
        assertTrue(commandeRepository.existsById(commandeId));
        assertTrue(ligneRepository.existsById(ligne1Id));
        assertTrue(ligneRepository.existsById(ligne2Id));

        // 7. Supprimer la commande → doit supprimer ses lignes par CASCADE
        commandeRepository.deleteById(commandeId);
        commandeRepository.flush();

        // 8. Vérifier que la commande ET ses lignes sont supprimées
        assertFalse(commandeRepository.existsById(commandeId));
        assertFalse(ligneRepository.existsById(ligne1Id));
        assertFalse(ligneRepository.existsById(ligne2Id));
    }

    // ============================================================
    // CONTRAINTE 5: Supprimer un Dispensaire → supprime ses Commandes ET Lignes (CASCADE DELETE)
    // ============================================================

    /**
     * TEST 5: Quand on supprime un Dispensaire, ses Commandes et leurs Lignes
     * sont supprimées par cascade (Dispensaire → Commande → Ligne)
     */
    @Test
    @SuppressWarnings("null")
    public void testDeletingDispensaireDeletesItsCommandesAndLignesByCascade() {
        // 1. Créer un Dispensaire
        Dispensaire dispensaire = new Dispensaire();
        dispensaire.setNom("DispensaireCascade_" + System.currentTimeMillis());
        dispensaire.setAdresse("456 Avenue Test");
        dispensaire.setCodePostal("75001");
        dispensaire.setVille("Paris");
        dispensaire.setRegion("Île-de-France");
        dispensaire.setPays("France");
        Dispensaire savedDispensaire = dispensaireRepository.save(dispensaire);

        // 2. Créer une Catégorie
        Categorie cat = new Categorie();
        cat.setLibelle("CategorieCascade_" + System.currentTimeMillis());
        cat.setDescription("Catégorie pour cascade test");
        Categorie savedCat = categorieRepository.save(cat);

        // 3. Créer un Médicament
        Medicament med = new Medicament();
        med.setNom("MedicamentCascade_" + System.currentTimeMillis());
        med.setPrixUnitaire(BigDecimal.valueOf(50));
        med.setCategorie(savedCat);
        Medicament savedMed = medicamentRepository.save(med);

        // 4. Créer 2 Commandes pour ce Dispensaire
        Commande commande1 = new Commande();
        commande1.setDispensaire(savedDispensaire);
        commande1.setSaisieLe(LocalDate.now());
        commande1.setEnvoyeeLe(null);
        commande1.setPort(BigDecimal.ZERO);
        commande1.setRemise(BigDecimal.ZERO);
        commande1.setCodePostale("75001");
        commande1.setRegion("Île-de-France");
        commande1.setVille("Paris");
        commande1.setAdresse("Adresse 1");
        Commande savedCommande1 = commandeRepository.save(commande1);

        Commande commande2 = new Commande();
        commande2.setDispensaire(savedDispensaire);
        commande2.setSaisieLe(LocalDate.now().minusDays(1));
        commande2.setEnvoyeeLe(LocalDate.now());  // Cette commande a été expédiée
        commande2.setPort(BigDecimal.ZERO);
        commande2.setRemise(BigDecimal.ZERO);
        commande2.setCodePostale("75001");
        commande2.setRegion("Île-de-France");
        commande2.setVille("Paris");
        commande2.setAdresse("Adresse 2");
        Commande savedCommande2 = commandeRepository.save(commande2);
        commandeRepository.flush();

        // 5. Créer 2 Lignes pour la première commande
        Ligne ligne1 = new Ligne();
        ligne1.setCommande(savedCommande1);
        ligne1.setMedicament(savedMed);
        ligne1.setQuantite(20);
        Ligne savedLigne1 = ligneRepository.save(ligne1);

        Ligne ligne2 = new Ligne();
        ligne2.setCommande(savedCommande1);
        ligne2.setMedicament(savedMed);
        ligne2.setQuantite(15);
        Ligne savedLigne2 = ligneRepository.save(ligne2);

        // 6. Créer 1 Ligne pour la deuxième commande
        Ligne ligne3 = new Ligne();
        ligne3.setCommande(savedCommande2);
        ligne3.setMedicament(savedMed);
        ligne3.setQuantite(10);
        Ligne savedLigne3 = ligneRepository.save(ligne3);

        ligneRepository.flush();

        Integer dispensaireId = savedDispensaire.getCode();
        Integer commande1Id = savedCommande1.getNumero();
        Integer commande2Id = savedCommande2.getNumero();
        Integer ligne1Id = savedLigne1.getId();
        Integer ligne2Id = savedLigne2.getId();
        Integer ligne3Id = savedLigne3.getId();

        // 7. Vérifier que tout existe
        assertTrue(dispensaireRepository.existsById(dispensaireId));
        assertTrue(commandeRepository.existsById(commande1Id));
        assertTrue(commandeRepository.existsById(commande2Id));
        assertTrue(ligneRepository.existsById(ligne1Id));
        assertTrue(ligneRepository.existsById(ligne2Id));
        assertTrue(ligneRepository.existsById(ligne3Id));

        // 8. Supprimer le dispensaire → doit supprimer ses commandes ET leurs lignes
        dispensaireRepository.deleteById(dispensaireId);
        dispensaireRepository.flush();

        // 9. Vérifier que le dispensaire ET ses commandes ET ses lignes sont supprimés
        assertFalse(dispensaireRepository.existsById(dispensaireId));
        assertFalse(commandeRepository.existsById(commande1Id));
        assertFalse(commandeRepository.existsById(commande2Id));
        assertFalse(ligneRepository.existsById(ligne1Id));
        assertFalse(ligneRepository.existsById(ligne2Id));
        assertFalse(ligneRepository.existsById(ligne3Id));
    }
}
