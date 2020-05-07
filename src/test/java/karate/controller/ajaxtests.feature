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

Scenario: AJAX login credentials
  Given header X-CSRF-TOKEN = csrf
  Given path 'checkCredentials'
  Given param username = "patient"
  Given param password = "aa"
  Given request {}
  When method POST
  * string response = response
  * print response
  Then status 200