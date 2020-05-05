Feature: AdminController tests

Background:
# chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }

Scenario: Admin can see patients correctly
  Given driver 'http://localhost:8080/login'
  And input('#username', 'admin')
  And input('#password', 'aa')
  When submit().click("button[type=submit]")
  Then match html('title') contains 'Physionet - Pacientes'
  And match html('body') contains 'Arturo Martínez'

Scenario: Admin can see doctors correctly
  Given driver 'http://localhost:8080/login'
  And input('#username', 'admin')
  And input('#password', 'aa')
  When submit().click("button[type=submit]")
  And click("a[href='/admin/doctors']")
  Then match html('title') contains 'Physionet - Médicos'
  And match html('body') contains 'Fernando Martínez'

Scenario: Admin can see absences correctly
  Given driver 'http://localhost:8080/login'
  And input('#username', 'admin')
  And input('#password', 'aa')
  When submit().click("button[type=submit]")
  And click("a[href='/admin/absences']")
  Then match html('title') contains 'Physionet - Ausencias'
  And match html('body') contains 'Fernando'