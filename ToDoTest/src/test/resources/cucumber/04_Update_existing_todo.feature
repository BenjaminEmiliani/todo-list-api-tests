	Feature: Update Existing Todo
 	
 	 As a Project Manager
 	 I want to update a todo entity
 	 So that my team can see the progress
 	 
 	 Background: 
    Given I am logged into the server
    And There exists a todo with doneStatus of false and title "Design New Logo"

  Scenario: I update doneStatus of a todo (Normal Flow)
    When I update the doneStatus to true
    Then I am informed the change was okay
    And todo "Design New Logo" will have doneStatus of true

    
  Scenario: I update doneStatus and Description of a todo (Alternate Flow)
    When I update the doneStatus to true
    And I update the description to "Minimalistic"
    Then I am informed the change was okay
    And todo "Design New Logo" will have doneStatus of true
    
  Scenario: I fail to update a todo (Error Flow)
    When I update the title to empty string
    Then I am informed the title cannot be empty
    And Todo will have title "Design New Logo"