package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    /**
     * Récupération des valeurs des constantes déjà initialisés ailleurs :
     *
     *     public static final Double SALAIRE_BASE = 1521.22;
     *     public static final Integer NB_CONGES_BASE = 25;
     *     public static final Double INDICE_PRIME_BASE = 0.3;
     *     public static final Double INDICE_PRIME_MANAGER = 1.7;
     *     public static final Double PRIME_ANCIENNETE = 100d;
     *     public static final Integer PERFORMANCE_BASE = 1;
     *     public static final Integer NB_JOURS_MAX_FORFAIT = 218;
     *     private static final double PRIME_BASE = 1000d;
*/
    @ParameterizedTest(name = "La prime annuelle du matricule {1} est valide")
    @CsvSource({
            "1, 'M00001', 0, 1.0, 1700.0",
            "1, 'M00001', 1, 1.0, 1800.0",
            "1, 'T12345', 2, 1.0, 1200.0",
            "1, 'T12345', 2, 0.5, 600.0",
            "2, 'T12345', 0, 1.0, 2300.0,",
            "2, 'T12345', 3, 1.0, 2600.0"
    })
    public void testGetPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete,
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

    @Test
    public void testGetNombreAnneeAncienneteNow() {
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
    public void testGetNombreAnneeAncienneteNmoins2() {
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(2));


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

    /*
    @ParameterizedTest(name = "L'augmentation du salaire du matricule {1} est valide.")
    @CsvSource({
            "'M00001', 1700.0, 4.1, 2210.0",
            "'M00001', 1800.0, -5, 2700.0",
            "'T12345', 1200.0, 6.0, 1680.0",
            "'T12345', 600.0, 3.0, 780.0",
            "'T12345', 2300.0, 4.0, 3220.0"
    })
    public void testAugmentationSalaire(String matricule, Double salaire, Double pourcentage, Double salaireAugmente){
        //Given
        Employe e = new Employe();
        e.setMatricule(matricule);
        e.setSalaire(salaire);

        //When
        e.augmenterSalaire(pourcentage);

        //Then
        Assertions.assertThat(pourcentage).isLessThanOrEqualTo(100.0);
        Assertions.assertThat(pourcentage).isGreaterThan(0.0);
        Assertions.assertThat(e.getSalaire()).isEqualTo(salaireAugmente);
        Assertions.assertThat(e.getSalaire()).isLessThan(3000.01); //Salaire maximum
    }
    */
}