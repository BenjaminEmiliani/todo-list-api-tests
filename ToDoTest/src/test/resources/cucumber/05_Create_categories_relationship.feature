	Feature: Create Categories Instance
 	
 	 As an Event Planner
 	 I want to create an instance of categories for a todo 
 	 So that it is done on the correct day
 	 
 	Background: 
    Given I am logged into the server
    Given There exists a category with title "Wedding"
    And There exists a todo with title "Hire Officiant"

  Scenario: I create a categories instance (Normal Flow)
    When I create the relationship "categories"
    Then The category "Wedding" will be related to the todo
    And I am notified the relationship is created
    
  Scenario: I fail to create a categories instance (Error Flow)
		When I create the relationship with invalid category id
    Then The todo will not be related to "Wedding"
