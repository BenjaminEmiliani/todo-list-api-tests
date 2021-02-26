
	Feature: Create a new todo 

  As a teacher
  I want to create a new todo item
  so my TAs can see the outstanding tasks

  Background: 
    Given I am logged into the server

  Scenario: Create a new todo with only a title field (Normal Flow)
  
    When I create a todo with title only
    Then I will be informed that the todo has been created
    And  The new todo will be in the system
    
	Scenario: Create a new todo with only a title and description (Alternate Flow)
  
    When I create a todo with title and description
    Then I will be informed that the todo has been created
    And  The new todo will be in the system
    
	Scenario: Create a new todo without a title (Error Flow)
  
    When I create a todo without a title 
    Then I will be informed that the todo was not created
    And  I will be informed that the todo requires a title field