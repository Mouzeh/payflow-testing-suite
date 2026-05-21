Feature: Procesamiento de pagos con tarjeta

  Background:
    Given el cliente tiene una tarjeta de prueba válida

  Scenario: Pago aprobado con monto válido
    When realiza un pago de 5000 con moneda "usd"
    Then la transacción es aprobada
    And el monto cobrado es 5000

  Scenario: Pago rechazado con tarjeta declinada
    When realiza un pago con tarjeta declinada de 5000 con moneda "usd"
    Then la transacción es rechazada
    And el mensaje de error contiene "Your card was declined"

  Scenario: Pago rechazado con monto cero
    When realiza un pago de 0 con moneda "usd"
    Then la transacción es rechazada
    And el mensaje de error contiene "invalid_request_error"
