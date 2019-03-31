package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    //Messages fréquemment utilisés dans les méthodes de tests
    private String MSG_ERROR_EXCEPTION = "La méthode aurait dû lancer une exception";
    private String MATRICULE_COMMERCIAL = "C00002";

    //Constante servant au calcul de la performance d'un Commercial
    private Long OBJECTIF_CA_FOR_TEST = 15000L;
    private String NOM = "Doe";
    private String PRENOM = "John";
    private LocalDate DATE_FOR_TEST = LocalDate.now();
    private Double TEMPS_PARTIEL = 1.0;
    private Double AVG_PERFORMANCE_WHICH_START_WITH_C = 1.0; //Moyenne pour les commerciaux (C)

    //Réinitialise le jeu de données
    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("T00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(this.DATE_FOR_TEST.format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("M00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(this.DATE_FOR_TEST.format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("M00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.85, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterNoLastMatricule() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals("M00001", employeArgumentCaptor.getValue().getMatricule());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterExistingEmploye() throws EmployeException {
        //Given
        when(employeRepository.findLastMatricule()).thenReturn("00000");
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());

        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;

        Employe employe = new Employe();
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setMatricule("M00001");

        //When
        try {
            employeRepository.save(employe);
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        }
        //Then
        catch (EntityExistsException e) {
            Assertions.assertEquals("L'employé de matricule M00001 existe déjà en BDD", e.getMessage());
        }
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster99999() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When - Then
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
        }
        catch (EmployeException employeException) {
            Assertions.assertEquals("Limite des 100000 matricules atteinte !", employeException.getMessage());
        }
    }

    /**
     * Méthode vérifiant la validité du paramètre caTraites
     * @throws EmployeException
     */
    @Test
    public void calculPerformanceCommercialCATraitesInvalides() throws EmployeException {
        //Given - When
        String warnMsg = "Le chiffre d'affaire traité ne peut être négatif ou null !";
        try {
            employeService.calculPerformanceCommercial(this.MATRICULE_COMMERCIAL, null, 0L);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);

            employeService.calculPerformanceCommercial(this.MATRICULE_COMMERCIAL, -1L, 0L);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
        }
        //Then
        catch (EmployeException employeException) {
            Assertions.assertEquals(warnMsg, employeException.getMessage());
        }
    }

    /**
     * Méthode vérifiant la validité du paramètre objectifCa
     * @throws EmployeException
     */
    @Test
    public void calculPerformanceCommercialObjectifCAInvalides() throws EmployeException {
        //Given - When
        String warnMsg = "L'objectif de chiffre d'affaire ne peut être négatif ou null !";
        try {
            employeService.calculPerformanceCommercial(this.MATRICULE_COMMERCIAL, 0L, null);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);

            employeService.calculPerformanceCommercial(this.MATRICULE_COMMERCIAL, 0L, -1L);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
        }
        //Then
        catch (EmployeException employeException) {
            Assertions.assertEquals(warnMsg, employeException.getMessage());
        }
    }

    /**
     * Méthode vérifiant la validité du matricule
     *
     * @throws EmployeException Si le matricule est null ou ne commence pas par un C
     */
    @Test
    public void calculPerformanceCommercialMatriculeInvalide() throws EmployeException {
        //Given - When
        String warnMsg = "Le matricule ne peut être null et doit commencer par un C !";
        try {
            employeService.calculPerformanceCommercial(null, 0L, 0L);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);

            employeService.calculPerformanceCommercial("M00001", 0L, 0L);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
        }
        //Then
        catch (EmployeException employeException) {
            Assertions.assertEquals(warnMsg, employeException.getMessage());
        }
    }

    @Test
    public void calculPerformanceEmployeIntrouvable() throws EmployeException {
        //Given - When
        String warnMsg = "Le matricule C99999 n'existe pas !";
        try {
            employeService.calculPerformanceCommercial("C99999", 0L, 0L);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
        }
        //Then
        catch (EmployeException employeException) {
            Assertions.assertEquals(warnMsg, employeException.getMessage());
        }
    }

    /**
     * Méthode vérifiant le calcul de la performance d'un commercial en fonction de ses objectifs CA et du CA traité dans l'année.
     *
     * Test 1 : Si le CA est inférieur de plus de 20% à l'objectif fixé, le commercial retombe à la performance de base
     * Test 2 : Si le CA est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (ds limite de la performance de base)
     * Test 3 : Si le CA est entre -5% et +5% de l'objectif fixé, la performance reste la même.
     * Test 4 : Si le CA supérieur est compris entre 5 et 20%, il gagne 1 de performance
     * Test 5 : Si le CA est supérieur de plus de 20%, il gagne 4 de performance
     * Test 6 : Si performance calculée > moyenne des performances des commerciaux, il reçoit + 1 de performance.
     * Test 7&8 : (Performance calculée = Performance de base) Si performance calculée < moyenne des performances des commerciaux, alors la performance calculée ne change pas
     *
     *   * Initialisation du jeu de données des tests listés ci-dessus
     *   *
     *   * Récapitulatif des valeurs dans l'ordre suivant : caTraite, performance de l'employé, performance calculée :
     *   * Test 1 : "15000, 1, 1",
     *   * Test 2 : "15000, 2, 3",
     *   * Test 3 : "12000, 4, 3",
     *   * Test 4 : "16000, 3, 5",
     *   * Test 5 : "18001, 1, 6",
     *   * Test 6 : "19000, 5, 10",
     *   * Test 7 : "10000, 2, 1",
     *   * Test 8 : "10000, 6, 1"
     */
    @Test
    public void calculPerformanceTest1() throws EmployeException {
        //Given
        Employe employeTest1 = new Employe(this.NOM, this.PRENOM, "C00001", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 1, 1.0);
        
        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00001")).thenReturn(employeTest1);
        
        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00001", 15000L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(Entreprise.PERFORMANCE_BASE, employeArgumentCaptor.getValue().getPerformance());
    }

    @Test
    public void calculPerformanceTest2() throws EmployeException {
        //Given
        Employe employeTest2 = new Employe(this.NOM, this.PRENOM, "C00002", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 2, this.TEMPS_PARTIEL);

        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00002")).thenReturn(employeTest2);

        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00002", 15000L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(3, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void calculPerformanceTest3() throws EmployeException {
        //Given
        Employe employeTest3 = new Employe(this.NOM, this.PRENOM, "C00003", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 4, this.TEMPS_PARTIEL);
        
        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00003")).thenReturn(employeTest3);
        
        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00003", 12000L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(3, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void calculPerformanceTest4() throws EmployeException {
        //Given
        Employe employeTest4 = new Employe(this.NOM, this.PRENOM, "C00004", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 3, this.TEMPS_PARTIEL);
        
        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00004")).thenReturn(employeTest4);

        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00004", 16000L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(5, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void calculPerformanceTest5() throws EmployeException {
        //Given
        Employe employeTest5 = new Employe(this.NOM, this.PRENOM, "C00005", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 1, this.TEMPS_PARTIEL);

        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00005")).thenReturn(employeTest5);

        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00005", 18001L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(6, employeArgumentCaptor.getValue().getPerformance().intValue());
    }

    @Test
    public void calculPerformanceTest6() throws EmployeException {
        //Given
        Employe employeTest6 = new Employe(this.NOM, this.PRENOM, "C00006", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 5, this.TEMPS_PARTIEL);

        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00006")).thenReturn(employeTest6);

        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00006", 19000L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(10, employeArgumentCaptor.getValue().getPerformance().intValue());

    }

    @Test
    public void calculPerformanceTest7() throws EmployeException {
        //Given
        Employe employeTest7 = new Employe(this.NOM, this.PRENOM, "C00007", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 2, this.TEMPS_PARTIEL);

        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00007")).thenReturn(employeTest7);

        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00007", 10000L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(1, employeArgumentCaptor.getValue().getPerformance().intValue());

    }

    @Test
    public void calculPerformanceTest8() throws EmployeException {
        //Given
        Employe employeTest8 = new Employe(this.NOM, this.PRENOM, "C00008", this.DATE_FOR_TEST, Entreprise.SALAIRE_BASE, 6, this.TEMPS_PARTIEL);

        //Assignation des employés servant aux tests
        when(employeRepository.findByMatricule("C00008")).thenReturn(employeTest8);

        //Assignation de la performance moyenne à la requête
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(AVG_PERFORMANCE_WHICH_START_WITH_C);

        //WHEN
        employeService.calculPerformanceCommercial("C00008", 10000L, this.OBJECTIF_CA_FOR_TEST);

        // THEN
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());

        Assertions.assertEquals(1, employeArgumentCaptor.getValue().getPerformance().intValue());

    }
}