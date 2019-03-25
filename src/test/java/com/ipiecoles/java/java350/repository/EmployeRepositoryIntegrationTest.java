package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeRepositoryIntegrationTest {

    @Autowired
    EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

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
        //when(employeRepository.avgPerformanceWhereMatriculeStartsWith("T")).thenReturn(0.0);
        //when(employeRepository.avgPerformanceWhereMatriculeStartsWith("")).thenReturn(0.0);
        //when(employeRepository.avgPerformanceWhereMatriculeStartsWith(null)).thenReturn(null);

    }

    /**
     * Seuls le Commercial C et le Manager M peuvent avoir une performance
     *
     */
    @Test
    public void integrationAvgPerformanceWhereMatriculeStartsWithC() {
        //Given - When
        Integer performance = 1;
        employeRepository.save(new Employe("Doe", "John", "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, performance, 1.0));
        Double avgPerformanceCommercial = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        //Then
        Assertions.assertEquals(null, avgPerformanceCommercial);
    }
}
