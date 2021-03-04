	Feature: Create Tasksof Instance
 	
 	 As a Project Manager
 	 I want to create an instance of tasksof for a todo 
 	 So that it is done by the corresponding team project
 	 
 	 Background: 
    Given I am logged into the server
    Given There exists a project with title "Company Redesign"
    And There exists a todo with title "Design New Logo"

  Scenario: I create a tasksof instance (Normal Flow)
    When I create the relationship "tasksof"
    Then The project "Company Redesign" will be related to "Design New Logo"
    
  Scenario: I fail to create a tasksof instance (Error Flow)
  	When I create the relationship with invalid project id
  	 Then The project "Company Redesign" will not be related to "Design New Logo"