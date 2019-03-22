package com.ipiecoles.java.java350.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.when;


@SpringBootTest
public class EmployeRepositoryIntegrationTest {

    @Autowired
    private EmployeRepository employeRepository;

    /**
     * Seuls le Commercial et le Manager peuvent avoir une performance
     *
     * Test le cas où la requête souhaite être lancé sur un technicien ("T")
     * Test le cas où la chaîne est vide ("")
     * Test le cas où la chaîne est nulle (null)
     */
    @Test
    public void integrationAvgPerformanceWhereMatriculeStartsWithNull() {
        //Given - When
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("T")).thenReturn(0.0);
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("")).thenReturn(0.0);
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith(null)).thenReturn(null);

    }

    /**
     * Seuls le Commercial et le Manager peuvent avoir une performance
     *
     */
    @Test
    public void integrationAvgPerformanceWhereMatriculeStartsWithCOrM() {
        //Given - When
        /* Après avoir lancé la requête suivante dans la bdd :
         * SELECT avg(c.performance) FROM Commercial c
         * JOIN Employe e ON e.id = c.id
         * WHERE SUBSTRING(e.matricule,0,1) = "C";
         * vérifions le résultat pour la performance des commerciaux :
         */
        Double avgPerformanceC = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        /* Après avoir lancé la requête suivante dans la bdd :
         * SELECT avg(m.performance) FROM Manager m
         * JOIN Employe e ON e.id = m.id
         * WHERE SUBSTRING(e.matricule,0,1) = "M";
         * vérifions le résultat pour la performance des managers :
         */
        Double avgPerformanceM = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");

        //Then
        Assertions.assertThat(avgPerformanceC).isEqualTo(null);
        Assertions.assertThat(avgPerformanceM).isEqualTo(null);
    }
}
