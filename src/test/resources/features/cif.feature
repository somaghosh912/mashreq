Feature: CIF Master Creation with Maker-Checker Workflow
  This feature describes the complete CIF (Customer Information File) creation process
  with maker-checker approval pattern in FLEXCUBE banking system.

  Background:
    Given FLEXCUBE application is available
    And Test data is loaded for users

  @smoke @maker-checker
  Scenario: Create new CIF with complete approval workflow
    Given User "maker1" is logged in with "ROLE_MAKER"
    When User initiates CIF creation for customer "CUST001"
    And User fills CIF details:
      | Field         | Value              |
      | Customer Name | Test Customer One  |
      | Customer Type | Individual         |
      | Segment       | Mass               |
    And User saves the CIF
    And User submits CIF for approval
    Then CIF should be in "PENDING_APPROVAL" status

    When User "checker1" is logged in with "ROLE_CHECKER"
    And User navigates to CIF approval list
    And User searches and opens CIF "CUST001"
    And User reviews the CIF details
    And User approves the CIF with authorization
    Then CIF should be in "APPROVED" status
    And CIF should be available in the Dashboard

  @regression @maker-checker
  Scenario: Reject CIF during checker approval
    Given User "maker1" is logged in with "ROLE_MAKER"
    When User creates and submits CIF for "CUST002"
    Then CIF status should be "PENDING_APPROVAL"

    When User "checker1" is logged in with "ROLE_CHECKER"
    And User navigates to CIF approval list
    And User opens CIF "CUST002" for review
    And User rejects the CIF with reason "Incomplete Documentation"
    Then CIF should be in "REJECTED" status
    And Rejection notification should be sent to maker

  @critical
  Scenario: CIF creation with validation failures
    Given User "maker1" is logged in with "ROLE_MAKER"
    When User navigates to CIF creation
    And User attempts to save CIF without mandatory fields
    Then Error message should display "Please fill all mandatory fields"
    And CIF should not be saved
