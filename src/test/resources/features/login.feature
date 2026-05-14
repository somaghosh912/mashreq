Feature: Login and Authentication
  This feature covers FLEXCUBE user authentication scenarios

  @smoke @critical
  Scenario: Successful user login
    Given User navigates to login page
    When User enters valid credentials
      | username | maker1@mashreq.ae      |
      | password | MakerPassword@123      |
    And User clicks login button
    Then Dashboard should be displayed
    And Welcome message should show user name

  @smoke @critical
  Scenario: Login with invalid credentials
    Given User navigates to login page
    When User enters invalid credentials
      | username | invalid@mashreq.ae     |
      | password | InvalidPassword        |
    And User clicks login button
    Then Error message "Invalid credentials" should be displayed
    And User should remain on login page

  @regression
  Scenario: Remember me functionality
    Given User is on login page
    When User enters valid credentials
    And User checks "Remember Me" checkbox
    And User clicks login button
    Then User should be logged in successfully
    And User credentials should be remembered
