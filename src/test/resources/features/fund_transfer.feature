Feature: Fund Transfer Execution
  This feature covers all fund transfer scenarios including validation
  and multi-layer transaction confirmation.

  Background:
    Given FLEXCUBE application is available
    And Customer is authenticated

  @smoke @fund-transfer
  Scenario: Execute domestic fund transfer
    Given Customer is logged in
    When Customer navigates to Fund Transfer module
    And Customer selects transfer type "Domestic"
    And Customer selects beneficiary account
    And Customer enters transfer amount "50000 AED"
    And Customer reviews transfer details
    And Customer confirms the transfer
    Then Transfer should be in "PROCESSING" status
    And Confirmation SMS should be sent

  @regression @fund-transfer @database
  Scenario: Validate fund transfer database records
    Given Fund transfer is initiated
    When Transaction is confirmed
    Then Database record should exist in FT_MASTER table
    And Transaction status should be "SUBMITTED"
    And Debit account should be debited in GL_MASTER
