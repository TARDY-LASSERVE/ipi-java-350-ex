package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;

public class EmployeTest {

    @Test
    public void getNombreAnneeAncienneteNow() {
        //Given = Initialisation des données d'entrée
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());


        //When = Exécution de la méthode à tester
        //Toujours mettre 1 seul test
        Integer nbAnnee = employe.getNombreAnneeAnciennete();


        //Then = Vérifications de ce qu'a fait la méthode
        Assertions.assertThat(nbAnnee).isEqualTo(0);

    }

    @Test
    public void testGetNombreAnneeAncienneteNull() {
        //Given = Initialisation des données d'entrée
        Employe employe = new Employe();
        employe.setDateEmbauche(null);


        //When = Exécution de la méthode à tester
        Integer nbAnnee = employe.getNombreAnneeAnciennete();


        //Then = Vérifications de ce qu'a fait la méthode
        Assertions.assertThat(nbAnnee).isEqualTo(0);

    }

    @Test
    public void getNombreAnneeAncienneteNminus2() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(2L));


        //When
        Integer nbAnnee = employe.getNombreAnneeAnciennete();


        //Then
        Assertions.assertThat(nbAnnee).isGreaterThanOrEqualTo(2);

    }

    @Test
    public void testGetNombreAnneeAncienneteNplus2() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));


        //When
        Integer nbAnnee = employe.getNombreAnneeAnciennete();


        //Then
        Assertions.assertThat(nbAnnee).isGreaterThanOrEqualTo(0);

    }

    @ParameterizedTest()
    @CsvSource({
            "1700.0, 4.1, 1769.7",
            "1800.0, 0.0, 1800.0",
            "1200.0, 6.0, 1272.0",
            "600.0, 3.0, 618.0",
            "2300.0, 40.0, 3000.0"
    })
    public void testAugmentationSalaire(Double salaire, Double pourcentage, Double newSalaire) throws IllegalArgumentException{
        //Given
        Employe e = new Employe();
        e.setSalaire(salaire);

        //When
        e.augmenterSalaire(pourcentage);

        //Then
        Assertions.assertThat(pourcentage).isGreaterThanOrEqualTo(0.0);
        Assertions.assertThat(e.getSalaire()).isEqualTo(newSalaire);
        Assertions.assertThat(e.getSalaire()).isLessThan(3000.01); //Salaire maximum
    }


    @Test
    public void testAugmentationSalaireNull() throws IllegalArgumentException {
        //Given
        Boolean thrown = false;
        Double salaire = 1000.0;
        Employe employe = new Employe();
        employe.setSalaire(salaire);

        //When - Then
        try {
            employe.augmenterSalaire(null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        Assertions.assertThat(thrown).isEqualTo(true);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaire);

    }

    @Test
    public void testAugmentationSalaireNegatif() throws IllegalArgumentException {
        //Given
        Double salaire = 1000.0;
        Employe employe = new Employe();
        employe.setSalaire(salaire);

        //When
        try {
            employe.augmenterSalaire(-5.0);
            Assertions.fail("La méthode aurait dû lancer une exception");
        }
        //Then
        catch (IllegalArgumentException illegalArgumentException) {
            Assertions.assertThat(illegalArgumentException.getMessage()).isEqualTo("Le pourcentage d'augmentation du salaire est null ou négatif !");
        }
        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaire);

    }

    @Test
    public void augmentationSalaireSuperieureALimite() throws IllegalArgumentException {
        //Given
        Double salaire = 1000.0;
        Employe employe = new Employe();
        employe.setSalaire(salaire);

        //When - Then
        try {
            employe.augmenterSalaire(50.0);
            Assertions.fail("La méthode aurait dû lancer une exception");
        }
        //Then
        catch (IllegalArgumentException illegalArgumentException) {
            Assertions.assertThat(illegalArgumentException.getMessage()).isEqualTo("L'employé ne peut pas avoir un trop gros salaire !!");
        }
        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaire);

    }


    /**
     * Vérifie le calcul du nombre de RTTs d'un employé à TEMPS PLEIN
     * en fonction de la date passée en paramètre
     *
     * Test Ligne 1 : En 2019, il y a 104 NbJrsWeekEnd, 10 NbJrsFeriesHorsWeekEnd
     * Test Ligne 2 : En 2021, il y a 104 NbJrsWeekEnd, 7 NbJrsFeriesHorsWeekEnd
     * Test Ligne 3 : 105 NbJrsWeekEnd, 7 NbJrsFeriesHorsWeekEnd
     * Test Ligne 4 : 104 NbJrsWeekEnd, 7 NbJrsFeriesHorsWeekEnd, Année Bissextile
     */
    @ParameterizedTest(name = "Nb Jours RTT.")
    @CsvSource({
            "2019, 8.0",
            "2021, 10.0",
            "2022, 10.0",
            "2032, 11.0"
    })
    public void testNbJoursRttTempsPlein(Integer anneeCalcul, Double nbJrsRTT){
        //Given
        Employe e = new Employe();
        e.setTempsPartiel(1.0);
        LocalDate dateCalcul = LocalDate.of(anneeCalcul, 1, 1);

        //When
        Double nbRtt = e.getNbRtt(dateCalcul);

        //Then
        Assertions.assertThat(nbRtt).isEqualTo(nbJrsRTT);
    }
    /**
     * Récupération des valeurs des constantes déjà initialisés ailleurs :
     *
*/
    @ParameterizedTest()
    @CsvSource({
            "1, 'M00001', 0, 1.0, 1700.0",
            "1, 'M00001', 1, 1.0, 1800.0",
            "1, 'T12345', 2, 1.0, 1200.0",
            "1, 'T12345', 2, 0.5, 600.0",
            "2, 'T12345', 0, 1.0, 2300.0,",
            "2, 'T12345', 3, 1.0, 2600.0"
    })
    public void getPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete,
                                     Double tpsPartiel, Double primeAnnuelle){
        //Given
        Employe e = new Employe();
        e.setPerformance(performance);
        e.setMatricule(matricule);
        e.setDateEmbauche(LocalDate.now().minusYears(nbYearsAnciennete));
        e.setTempsPartiel(tpsPartiel);


        //When
        Double prime = e.getPrimeAnnuelle();


        //Then
        Assertions.assertThat(prime).isEqualTo(primeAnnuelle);
    }

}