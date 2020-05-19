Feature: DoctorController tests

Background:
  * configure driver = driverConfig

Scenario: Doctor typical app interaction
  Given driver baseUrl+'/login'
  And input('#username', 'doctor')
  And input('#password', 'aa')

  # Doctor can see appointments
  When submit().click("button[type=submit]")
  Then match html('title') contains 'Physionet - Mis citas'

  # Doctor can see patient 2 appointment
  When click("a[href='/doctor/appointment?id=2']")
  Then match html('body') contains 'Dolor de espalda 2'

  # Doctor can see messages
  When click("a[href='/doctor/messages'")
  Then match html('title') contains 'Physionet - Mis Mensajes'

  # Doctor can send a message
  When input('#textoMensaje', 'test')
  And submit().click("button[type=submit]")
  Then match html('body') contains 'test'

  # Doctor can see absences
  When click("a[href='/doctor/absences'")
  Then match html('title') contains 'Physionet - Ausencias'