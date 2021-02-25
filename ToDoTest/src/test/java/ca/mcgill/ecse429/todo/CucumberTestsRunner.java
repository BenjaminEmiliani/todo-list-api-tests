package ca.mcgill.ecse429.todo;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;


@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/resources",
		glue = "ca.mcgill.ecse429.todo")
public class CucumberTestsRunner {
}