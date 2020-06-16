Feature: PatientController tests

  Background:
    * configure driver = driverConfig

  Scenario: Patient typical app interaction
    Given driver baseUrl+'/login'
    And input('#username', 'patient')
    And input('#password', 'aa')

  # Patient can see pending appointments
    When submit().click("button[type=submit]")
    Then match html('title') contains 'Physionet - Citas pendientes'

  # Patient can see one appointment
    When click("a[href='/patient/appointment?id=4&today=false']")
    Then match html('body') contains 'Masaje zona lummbar leve'

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

