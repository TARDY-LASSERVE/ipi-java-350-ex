package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest extends EmployeService {

    @InjectMocks
    private EmployeService employeService;

    @Mock
    private EmployeRepository employeRepository;

    //Messages fréquemment utilisés dans les méthodes de tests
    private String MSG_ERROR_EXCEPTION = "La méthode aurait dû lancer une exception";
    private String MATRICULE_COMMERCIAL = "C00002";

    //Initialisation du jeu de données testant calculPerformanceCommercial (voir méthode initValueForTestingCalculPerformanceCommercial)
    private List<Integer> listResultatPerformanceCalculee = new ArrayList<>();
    private List<Integer> listForPerformanceBase = new ArrayList<>();
    private List<Long> listForCATraites = new ArrayList<>();

    //Réinitialise le jeu de données
    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    void testEmbaucheEmployeTechnicienPleinTpsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("T00001")).thenReturn(null);
        when(employeRepository.save(any())).thenAnswer(returnsFirstArg());

        //When Junit 5
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository).save(employeCaptor.capture()); // times(1) est une option du verify
        Employe e = employeCaptor.getValue(); //Récupère l'employe généré par la méthode save ci-dessus

        Assertions.assertThat(e.getNom()).isEqualTo(nom);
        Assertions.assertThat(e.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(e.getMatricule()).isEqualTo("T00001");
        Assertions.assertThat(e.getPerformance()).isEqualTo(1);
        //Salaire de base * Coefficient (=1521.22 * 1.2)
        Assertions.assertThat(e.getSalaire()).isEqualTo(1825.46);
        Assertions.assertThat(e.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(e.getTempsPartiel()).isEqualTo(tempsPartiel);

    }

    /**
     * Dernier matricule pour un employé : 00345
     * Vérifier avec la requête select max(substring(matricule,2)) from Employe
     *
     */
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
        when(employeRepository.save(any())).thenAnswer(returnsFirstArg());

        //When Junit 5
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository).save(employeCaptor.capture());

        Assertions.assertThat(employeCaptor.getValue().getNom()).isEqualTo(nom);
        Assertions.assertThat(employeCaptor.getValue().getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employeCaptor.getValue().getTempsPartiel()).isEqualTo(tempsPartiel);
        Assertions.assertThat(employeCaptor.getValue().getMatricule()).isEqualTo("M00346");
        Assertions.assertThat(employeCaptor.getValue().getSalaire()).isEqualTo(1064.85); //1521.22 * 1.4 * 0.5
        Assertions.assertThat(employeCaptor.getValue().getDateEmbauche()).isEqualTo(LocalDate.now());
    }

    @Test
    public void testEmbaucheEmployeLastMatricule99999() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;

        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
        }
        //Then
        catch (EmployeException employeException) {
            Assertions.assertThat(employeException.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
        }
    }


    @Test
    public void testEmbaucheEmployeExistingEmploye() throws EmployeException, EntityExistsException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;

        when(employeRepository.findByMatricule("M00002")).thenReturn(new Employe());

        //When
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
        }
        //Then
        catch (EntityExistsException entityExistsException) {
            Assertions.assertThat(entityExistsException.getMessage()).isEqualTo("L'employé de matricule M00002 existe déjà en BDD");
        }
    }

    /**
     * Méthode vérifiant la validité du paramètre caTraites
     * @throws EmployeException
     */
    @Test
    public void testCalculPerformanceCommercialCATraitesInvalides() throws EmployeException {
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
            Assertions.assertThat(employeException.getMessage()).isEqualTo(warnMsg);
        }
    }

    /**
     * Méthode vérifiant la validité du paramètre objectifCa
     * @throws EmployeException
     */
    @Test
    public void testCalculPerformanceCommercialObjectifCAInvalides() throws EmployeException {
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
            Assertions.assertThat(employeException.getMessage()).isEqualTo(warnMsg);
        }
    }

    /**
     * Méthode vérifiant la validité du matricule
     *
     * @throws EmployeException Si le matricule est null ou ne commence pas par un C
     */
    @Test
    public void testCalculPerformanceCommercialMatriculeInvalide() throws EmployeException {
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
            Assertions.assertThat(employeException.getMessage()).isEqualTo(warnMsg);
        }
    }

    @Test
    public void testCalculPerformanceEmployeIntrouvable() throws EmployeException {
        //Given - When
        String warnMsg = "Le matricule C99999 n'existe pas !";
        try {
            employeService.calculPerformanceCommercial("C99999", 0L, 0L);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
        }
        //Then
        catch (EmployeException employeException) {
            Assertions.assertThat(employeException.getMessage()).isEqualTo(warnMsg);
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
     * Pour information, performanceMoyenne = 5.2023
     * SELECT avg(c.performance) FROM commercial c JOIN employe e ON c.id = e.id
     * WHERE substring(e.matricule, 1, 1) = "C";
     */
    @Test
    public void testCalculPerformanceAllCases() throws EmployeException, Exception, EntityExistsException {
        //Given
        Long objectifCAForTest = 15000L; //Valeur constante pour le jeu de tests
        initValueForTestingCalculPerformanceCommercial();
        try {
            Employe employe = new Employe("Doe", "John", "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0);

        //When-Then
            employeRepository.save(employe);
            ArgumentCaptor<Employe> employeCaptor = ArgumentCaptor.forClass(Employe.class);
            verify(employeRepository).save(employeCaptor.capture());
            Employe employeCaptorValue = employeCaptor.getValue(); //Récupère l'employe généré par la méthode save ci-dessus
            Assertions.fail("L'employé est " + employeCaptorValue);
            // Initialisation de la performance de base de l'employé de ce test, à chaque itération
            for (Integer numTest = 0; numTest < listForCATraites.size(); numTest++) {
                employeCaptorValue.setPerformance(listForPerformanceBase.get(numTest).intValue());
                try{
                    employeService.calculPerformanceCommercial(employeCaptorValue.getMatricule(), listForCATraites.get(numTest).longValue(), objectifCAForTest);
                } catch(EmployeException e) {
                    Assertions.fail("EmployeException générée : caTraiteTest = " + listForCATraites.get(numTest).longValue() + "; objectifCAForTest = " + objectifCAForTest);
                }
                Assertions.assertThat(employeCaptorValue.getPerformance()).isEqualTo(listResultatPerformanceCalculee.get(numTest).intValue());
            }
            //Réinitialisation de la base de données à son état avant le lancement de cette méthode
            employeRepository.delete(employe);
        }
        catch (Exception e) {
            throw new Exception(this.MSG_ERROR_EXCEPTION);
        }
    }

    /**
     * Initialisation du jeu de données testant calculPerformanceCommercial
     *
     * Récapitulatif du jeu de tests (caTraite, objectifCa, performance de base, performance calculée) :
     * Test 1 : "15000, 15000, 1, 1",
     * Test 2 : "15000, 15000, 2, 2",
     * Test 3 : "12000, 15000, 4, 2",
     * Test 4 : "16000, 15000, 3, 4",
     * Test 5 : "18001, 15000, 1, 5",
     * Test 6 : "19000, 15000, 5, 8",
     * Test 7 : "10000, 15000, 2, 2",
     * Test 8 : "10000, 15000, 6, 7"
     */
    public void initValueForTestingCalculPerformanceCommercial(){
        listForCATraites.add(15000L);
        /*listForCATraites.add(15000L);
        listForCATraites.add(15000L);
        listForCATraites.add(12000L);
        listForCATraites.add(16000L);
        listForCATraites.add(18001L);
        listForCATraites.add(19000L);
        listForCATraites.add(10000L);
*/
        listForPerformanceBase.add(1);
        listForPerformanceBase.add(1);
        listForPerformanceBase.add(2);
        listForPerformanceBase.add(4);
        listForPerformanceBase.add(3);
        listForPerformanceBase.add(1);
        listForPerformanceBase.add(5);
        listForPerformanceBase.add(2);

        listResultatPerformanceCalculee.add(1);
        listResultatPerformanceCalculee.add(1);
        listResultatPerformanceCalculee.add(2);
        listResultatPerformanceCalculee.add(2);
        listResultatPerformanceCalculee.add(4);
        listResultatPerformanceCalculee.add(5);
        listResultatPerformanceCalculee.add(8);
        listResultatPerformanceCalculee.add(2);
    }

    public Employe createCommercialForTest() throws EntityExistsException, EmployeException{
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 1.0;
        String errorMsg = "L'employé " + nom + " " + prenom + " existe déjà en BDD";

        //When
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail(this.MSG_ERROR_EXCEPTION);
            return null;
        }
        //Then
        catch (EntityExistsException entityExistsException) {
            Assertions.assertThat(entityExistsException.getMessage()).isEqualTo(errorMsg);
        }
        catch (EmployeException employeException) {
            Assertions.assertThat(employeException.getMessage()).isEqualTo(errorMsg);
        }
        return employeRepository.findByMatricule(employeRepository.findLastMatricule());
    }
}