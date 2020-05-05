Feature: Ajax tests

  Background:
    * url baseUrl
    * def util = Java.type('karate.KarateTests')

    Given path 'login'
    When method get
    Then status 200
    * string response = response
    * def csrf = util.selectAttribute(response, "input[name=_csrf]", "value");
    * print csrf

# selectores para util.select*: ver https://jsoup.org/cookbook/extracting-data/selector-syntax
# objetivo de la forma
# <html lang="en">...<body><div><form>
#   <input name="_csrf" type="hidden" value="..." />

Scenario: AJAX delete an appointment
  Given path 'login'
  And form field username = 'patient'
  And form field password = 'aa'
  And form field _csrf = csrf
  When method post
  Then status 200

  Given path 'patient/deleteappointment/'
  And header X-CSRF-TOKEN = csrf
  And request { id: '2' }
  When method post
  * string response = response
  * print response