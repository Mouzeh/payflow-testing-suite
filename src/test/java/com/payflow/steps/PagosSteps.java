package com.payflow.steps;

import com.payflow.config.ConfigManager;
import com.payflow.config.DatabaseManager;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import io.qameta.allure.*;
import io.restassured.response.Response;

import java.sql.ResultSet;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class PagosSteps {

    private Response response;
    private String cardToken;
    static String currentTestCase;

    @Given("el cliente tiene una tarjeta de prueba válida")
    @Step("Configurar tarjeta de prueba Visa")
    public void clienteConTarjetaValida() {
        cardToken = "tok_visa";
        Allure.addAttachment("Tarjeta", "tok_visa (Visa test card)");
    }

    @When("realiza un pago de {int} con moneda {string}")
    @Step("POST /charges - monto: {0} {1}")
    public void realizaPago(int monto, String moneda) {
        Allure.addAttachment("Request",
            "POST /charges | amount=" + monto + " | currency=" + moneda + " | source=" + cardToken);
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
        Allure.addAttachment("Response", response.getBody().asPrettyString());
    }

    @When("realiza un pago con tarjeta declinada de {int} con moneda {string}")
    @Step("POST /charges - tarjeta declinada")
    public void realizaPagoDeclinado(int monto, String moneda) {
        Allure.addAttachment("Request",
            "POST /charges | amount=" + monto + " | source=tok_chargeDeclined");
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
        Allure.addAttachment("Response", response.getBody().asPrettyString());
    }

    @When("realiza un pago con tarjeta de fondos insuficientes de {int} con moneda {string}")
    @Step("POST /charges - fondos insuficientes")
    public void realizaPagoFondosInsuficientes(int monto, String moneda) {
        response = given()
            .baseUri(ConfigManager.get("stripe.base.url"))
            .auth().preemptive().basic(ConfigManager.get("stripe.api.key"), "")
            .contentType("application/x-www-form-urlencoded")
            .formParam("amount", monto)
            .formParam("currency", moneda)
            .formParam("source", "tok_chargeDeclinedInsufficientFunds")
            .formParam("description", "PayFlow Test - Fondos insuficientes")
        .when()
            .post("/charges");
        Allure.addAttachment("Response", response.getBody().asPrettyString());
    }

    @When("realiza un pago con tarjeta vencida de {int} con moneda {string}")
    @Step("POST /charges - tarjeta vencida")
    public void realizaPagoTarjetaVencida(int monto, String moneda) {
        response = given()
            .baseUri(ConfigManager.get("stripe.base.url"))
            .auth().preemptive().basic(ConfigManager.get("stripe.api.key"), "")
            .contentType("application/x-www-form-urlencoded")
            .formParam("amount", monto)
            .formParam("currency", moneda)
            .formParam("source", "tok_chargeDeclinedExpiredCard")
            .formParam("description", "PayFlow Test - Tarjeta vencida")
        .when()
            .post("/charges");
        Allure.addAttachment("Response", response.getBody().asPrettyString());
    }

    @Then("la transacción es aprobada")
    @Step("Verificar transacción aprobada")
    public void transaccionAprobada() throws Exception {
        assertEquals(200, response.getStatusCode(),
            "Se esperaba HTTP 200 pero llegó: " + response.getStatusCode());
        assertEquals("succeeded", response.jsonPath().getString("status"),
            "Se esperaba status 'succeeded'");

        String chargeId = response.jsonPath().getString("id");
        int monto = response.jsonPath().getInt("amount");
        String moneda = response.jsonPath().getString("currency");

        DatabaseManager.insertarTransaccion(
            currentTestCase, chargeId, monto, moneda, "succeeded", "Pago aprobado"
        );
        Allure.addAttachment("DB Insert", "Transacción guardada: " + chargeId);

        ResultSet rs = DatabaseManager.buscarTransaccion(chargeId);
        assertTrue(rs.next(), "La transacción no se encontró en la BD");
        assertEquals("succeeded", rs.getString("estado"));
        assertEquals(monto, rs.getInt("monto"));
        Allure.addAttachment("DB Validation", "BD verificada OK - charge_id: " + chargeId);
    }

    @Then("el monto cobrado es {int}")
    @Step("Verificar monto: {0}")
    public void montoAprobado(int monto) {
        assertEquals(monto, response.jsonPath().getInt("amount"),
            "El monto no coincide");
    }

    @Then("la transacción es rechazada")
    @Step("Verificar transacción rechazada")
    public void transaccionRechazada() {
        assertNotEquals(200, response.getStatusCode(),
            "Se esperaba un error pero llegó HTTP 200");
    }

    @Then("el mensaje de error contiene {string}")
    @Step("Verificar mensaje de error: {0}")
    public void mensajeDeError(String mensaje) {
        String body = response.getBody().asString();
        assertTrue(body.contains(mensaje),
            "Se esperaba: " + mensaje + "\nRespuesta: " + body);
    }

    @Then("el código de error es {string}")
    @Step("Verificar código de error: {0}")
    public void codigoDeError(String codigo) {
        String errorCode = response.jsonPath().getString("error.decline_code");
        if (errorCode == null) {
            errorCode = response.jsonPath().getString("error.code");
        }
        assertEquals(codigo, errorCode, "Código de error incorrecto");
    }

    @After
    public void cerrarConexion() throws Exception {
        DatabaseManager.cerrarConexion();
    }
}
