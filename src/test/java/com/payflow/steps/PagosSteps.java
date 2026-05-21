package com.payflow.steps;

import com.payflow.config.ConfigManager;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class PagosSteps {

    private Response response;
    private String cardToken;

    @Given("el cliente tiene una tarjeta de prueba válida")
    public void clienteConTarjetaValida() {
        cardToken = "tok_visa";
    }

    @When("realiza un pago de {int} con moneda {string}")
    public void realizaPago(int monto, String moneda) {
        response = given()
            .baseUri(ConfigManager.get("stripe.base.url"))
            .auth().preemptive().basic(ConfigManager.get("stripe.api.key"), "")
            .contentType("application/x-www-form-urlencoded")
            .formParam("amount", monto)
            .formParam("currency", moneda)
            .formParam("source", cardToken)
            .formParam("description", "PayFlow Test - Pago de prueba")
        .when()
            .post("/charges");
    }

    @When("realiza un pago con tarjeta declinada de {int} con moneda {string}")
    public void realizaPagoDeclinado(int monto, String moneda) {
        response = given()
            .baseUri(ConfigManager.get("stripe.base.url"))
            .auth().preemptive().basic(ConfigManager.get("stripe.api.key"), "")
            .contentType("application/x-www-form-urlencoded")
            .formParam("amount", monto)
            .formParam("currency", moneda)
            .formParam("source", "tok_chargeDeclined")
            .formParam("description", "PayFlow Test - Tarjeta declinada")
        .when()
            .post("/charges");
    }

    @Then("la transacción es aprobada")
    public void transaccionAprobada() {
        assertEquals(200, response.getStatusCode());
        assertEquals("succeeded", response.jsonPath().getString("status"));
    }

    @Then("el monto cobrado es {int}")
    public void montoAprobado(int monto) {
        assertEquals(monto, response.jsonPath().getInt("amount"));
    }

    @Then("la transacción es rechazada")
    public void transaccionRechazada() {
        assertNotEquals(200, response.getStatusCode());
    }

    @Then("el mensaje de error contiene {string}")
    public void mensajeDeError(String mensaje) {
        String body = response.getBody().asString();
        assertTrue(body.contains(mensaje),
            "Se esperaba el mensaje: " + mensaje + "\nRespuesta: " + body);
    }
}
