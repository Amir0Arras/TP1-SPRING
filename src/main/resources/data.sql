-- Données de base pour le projet Pharmacie
-- Dispensaire (Etablissements de santé qui passent commande de médicaments)
-- Le fichier est chargé au démarrage de l''application

-- Insertion des catégories de médicaments
INSERT INTO CATEGORIE (CODE, LIBELLE, DESCRIPTION) VALUES
(DEFAULT, 'Antalgiques et Antipyrétiques', 'Médicaments contre la douleur et la fièvre'), -- code : 1
(DEFAULT, 'Anti-inflammatoires', 'Médicaments réduisant l''inflammation'), -- code : 2
(DEFAULT, 'Antibiotiques', 'Médicaments pour traiter les infections bactériennes'),
(DEFAULT, 'Antihypertenseurs', 'Médicaments pour traiter l''hypertension artérielle'),
(DEFAULT, 'Antidiabétiques', 'Médicaments pour traiter le diabète'),
(DEFAULT, 'Antihistaminiques', 'Médicaments pour traiter les allergies'),
(DEFAULT, 'Vitamines et Compléments', 'Suppléments nutritionnels'),
(DEFAULT, 'Médicaments Cardiovasculaires', 'Médicaments pour le cœur et la circulation'),
(DEFAULT, 'Médicaments Gastro-intestinaux', 'Médicaments pour les troubles digestifs'),
(DEFAULT, 'Médicaments Respiratoires', 'Médicaments pour les troubles respiratoires');


-- Catégorie 1: Antalgiques et Antipyrétiques
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Morphine 10mg', 1, 'Boîte de 14 comprimés', 25.80, 80, 0, 15, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400'),
('Doliprane Effervescent 1g', 1, 'Boîte de 8 comprimés', 3.50, 280, 0, 30, false, 'https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=400'),
('Efferalgan Vitamine C', 1, 'Boîte de 16 comprimés', 4.20, 220, 0, 25, false, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400');

-- Catégorie 2: Anti-inflammatoires
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Étodolac 400mg', 2, 'Boîte de 14 comprimés', 12.50, 110, 0, 15, false, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400'),
('Flurbiprofène 100mg', 2, 'Boîte de 30 comprimés', 10.80, 130, 0, 16, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400');

-- Catégorie 3: Antibiotiques (2 médicaments indisponbibles)
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Lévofloxacine 500mg', 3, 'Boîte de 7 comprimés', 15.80, 160, 0, 18, true, 'https://images.unsplash.com/photo-1628771065518-0d82f1938462?w=400'),
('Clindamycine 300mg', 3, 'Boîte de 16 gélules', 13.20, 140, 0, 16, true, 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400');
-- ==============================
-- Dispensaires
-- ==============================
INSERT INTO DISPENSAIRE (CODE, NOM, ADRESSE, CODE_POSTAL, VILLE, REGION, PAYS, TELEPHONE, FAX, CONTACT, FONCTION) VALUES
(DEFAULT, 'Dispensaire Central', '12 Rue Principale', '75001', 'Paris', 'Île-de-France', 'France', '+33123456789', '+33123456780', 'Dr. Dupont', 'Directeur'),
(DEFAULT, 'Dispensaire Nord', '45 Avenue du Nord', '59000', 'Lille', 'Hauts-de-France', 'France', '+33322334455', '+33322334456', 'Dr. Martin', 'Chef de service'),
(DEFAULT, 'Dispensaire Sud', '78 Boulevard Sud', '13000', 'Marseille', 'Provence-Alpes-Côte d''Azur', 'France', '+33455667788', '+33455667789', 'Dr. Bernard', 'Directeur');

-- ==============================
-- Commandes
-- ==============================
INSERT INTO COMMANDE (NUMERO, ENVOYEE_LE, SAISIE_LE, PORT, REMISE, DISPENSAIRE_CODE, CODE_POSTALE, REGION, VILLE, ADRESSE) VALUES
(DEFAULT, '2026-01-10', '2026-01-08', 50.00, 5.00, 1, '75001', 'Île-de-France', 'Paris', '12 Rue Principale'),
(DEFAULT, '2026-01-11', '2026-01-09', 30.00, 0.00, 2, '59000', 'Hauts-de-France', 'Lille', '45 Avenue du Nord'),
(DEFAULT, '2026-01-12', '2026-01-10', 40.00, 10.00, 3, '13000', 'Provence-Alpes-Côte d''Azur', 'Marseille', '78 Boulevard Sud');

-- ==============================
-- Lignes de commande
-- ==============================
INSERT INTO LIGNE (ID, MEDICAMENT_REFERENCE, COMMANDE_NUMERO, QUANTITE) VALUES
(DEFAULT, 1, 1, 10), -- Morphine 10mg pour commande 1
(DEFAULT, 2, 1, 20), -- Doliprane Effervescent 1g pour commande 1
(DEFAULT, 3, 2, 15), -- Efferalgan Vitamine C pour commande 2
(DEFAULT, 4, 2, 5),  -- Étodolac 400mg pour commande 2
(DEFAULT, 5, 3, 25), -- Flurbiprofène 100mg pour commande 3
(DEFAULT, 6, 3, 10); -- Lévofloxacine 500mg pour commande 3