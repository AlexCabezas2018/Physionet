Feature: PatientController tests

  Background:
  # chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
    * configure driver = { type: 'chrome', showDriverLog: true }
    * url baseUrl
    * def util = Java.type('karate.KarateTests')

  Scenario: Patient typical app interaction
    Given driver 'http://localhost:8080/login'
    And input('#username', 'patient')
    And input('#password', 'aa')

  # Patient can see pending appointments
    When submit().click("button[type=submit]")
    Then match html('title') contains 'Physionet - Citas pendientes'

  # Patient can see one appointment
    When click("a[href='/patient/appointment?id=5&today=false']")
    Then match html('body') contains 'Dolor intenso en la zona del gemelo'

  # Patient can see messages
    When click("a[href='/patient/messages'")
    Then match html('title') contains 'Physionet - Mis Mensajes'

  # Patient can send a message
    When input('#textoMensaje', 'test')
    And submit().click("button[type=submit]")
    Then match html('body') contains 'test'

  # Patient can see today appointments
    When click("a[href='/patient/todayapp'")
    Then match html('title') contains 'Physionet - Citas pendientes'

