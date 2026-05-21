package com.payflow.steps;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class TestCaseSteps {

    @Before
    public void capturarTestCase(Scenario scenario) {
        String nombre = scenario.getName();
        // Extrae "TC001" del nombre "TC001 - Pago aprobado..."
        String testCase = nombre.split(" ")[0];
        PagosSteps.currentTestCase = testCase;
    }
}
