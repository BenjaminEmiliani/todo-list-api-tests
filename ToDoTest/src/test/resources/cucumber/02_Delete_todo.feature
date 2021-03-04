
	Feature: Delete a todo instance
 	
 	 As a TA
 	 I want to delete a todo task
 	 So that it is not done again
 	 
 	 Background: 
    Given I am logged into the server
    And There exists a todo with title "Grade Papers"

  Scenario: I delete a todo with its id (Normal Flow)
    When I delete the todo "Grade Papers"
    Then The todo no longer exists
    
  Scenario: I fail to delete a todo with an invalid id (Error Flow)
    When I delete the todo with invalid id 10220210
    Then I am informed that the todo was not found
    And The new todo will be in the system