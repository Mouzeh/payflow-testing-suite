Feature: Procesamiento de pagos con tarjeta

  Background:
    Given el cliente tiene una tarjeta de prueba válida

  @Critical
  Scenario: TC001 - Pago aprobado con monto válido
    When realiza un pago de 5000 con moneda "usd"
    Then la transacción es aprobada
    And el monto cobrado es 5000

  @Critical
  Scenario: TC002 - Pago aprobado en euros
    When realiza un pago de 10000 con moneda "eur"
    Then la transacción es aprobada
    And el monto cobrado es 10000

  @Critical
  Scenario: TC003 - Pago aprobado monto mínimo
    When realiza un pago de 50 con moneda "usd"
    Then la transacción es aprobada
    And el monto cobrado es 50

  @Critical
  Scenario: TC004 - Pago rechazado con tarjeta declinada
    When realiza un pago con tarjeta declinada de 5000 con moneda "usd"
    Then la transacción es rechazada
    And el mensaje de error contiene "Your card was declined"

  @Critical
  Scenario: TC005 - Pago rechazado por fondos insuficientes
    When realiza un pago con tarjeta de fondos insuficientes de 5000 con moneda "usd"
    Then la transacción es rechazada
    And el código de error es "insufficient_funds"

  @Normal
  Scenario: TC006 - Pago rechazado con tarjeta vencida
    When realiza un pago con tarjeta vencida de 5000 con moneda "usd"
    Then la transacción es rechazada
    And el código de error es "expired_card"

  @Normal
  Scenario: TC007 - Pago rechazado con monto cero
    When realiza un pago de 0 con moneda "usd"
    Then la transacción es rechazada
    And el mensaje de error contiene "invalid_request_error"

  @Normal
  Scenario: TC008 - Pago aprobado en pesos chilenos
    When realiza un pago de 5000 con moneda "clp"
    Then la transacción es aprobada
    And el monto cobrado es 5000

  @Normal
  Scenario: TC009 - Pago aprobado monto alto
    When realiza un pago de 999999 con moneda "usd"
    Then la transacción es aprobada
    And el monto cobrado es 999999

  @Normal
  Scenario: TC010 - Pago aprobado en pesos mexicanos
    When realiza un pago de 50000 con moneda "mxn"
    Then la transacción es aprobada
    And el monto cobrado es 50000
