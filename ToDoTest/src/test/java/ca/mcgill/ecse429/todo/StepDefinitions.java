package ca.mcgill.ecse429.todo;

import static org.junit.Assert.assertTrue;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepDefinitions {

	private String today;
	
	@Given("today is Sunday")
	public void today_is_Sunday() {
		today = "Sunday";
	}

	@When("I ask whether it is Friday yet")
	public void i_ask_whether_it_s_Friday_yet() {
	    
	}

	@Then("I should be told")
	public void i_should_be_told() {
	    assertTrue(today.equals("Sunday"));
	}
}
