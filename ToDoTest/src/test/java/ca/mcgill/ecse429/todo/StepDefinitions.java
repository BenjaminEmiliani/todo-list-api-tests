package ca.mcgill.ecse429.todo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class StepDefinitions extends SpringBootBaseIntegrationTest {
	
	@Autowired
	private TestRestTemplate restTemplate;
	private HttpHeaders headers;
	private String urlPath;

	ResponseEntity<String> response;
	private String idString;
	
	@Given("I am logged into the server")
	public void iAmLoggedIntoTheServer() {
		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
		urlPath = "http://localhost:4567/todos";
	}
	
	@When("I create a todo with title only")
	public void iCreateATodoWithTitleOnly() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"title\":\"Interview\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		System.out.println(restTemplate);
		response = restTemplate.exchange(builder.toUriString(),HttpMethod.POST, 
				entity, String.class);
		idString = parseJSON(response.getBody().toString())[0];
	}
	
	@Then("I will be informed that the todo has been created")
	public void todoHasBeenCreated() {
		assertEquals(response.getStatusCode().toString(), "201 CREATED");
	}
	
	@And("The new todo will be in the system")
	public void theNewTodoWillBeInTheSystem() {
		headers.setContentType(MediaType.APPLICATION_JSON);	
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
	}
	
	
	@When("I create a todo with title and description")
	public void iCreateATodoWithTitleAndDesc() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"title\":\"Interview\",\"description\":\"For internship position\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		System.out.println(restTemplate);
		response = restTemplate.exchange(builder.toUriString(),HttpMethod.POST, 
				entity, String.class);
		idString = parseJSON(response.getBody().toString())[0];
	}
	
	@When("I create a todo without a title")
	public void iCreateATodoWithoutTitle() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"description\":\"For internship position\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		System.out.println(restTemplate);
		response = restTemplate.exchange(builder.toUriString(),HttpMethod.POST, 
				entity, String.class);
	}
	
	@Then("I will be informed that the todo was not created")
	public void todoWasNotCreated() {
		assertEquals(response.getStatusCode().toString(), "400 BAD_REQUEST");
	}
	
	@And("I will be informed that the todo requires a title field")
	public void todoRequiresTitleField() {
		assertTrue(response.getBody().contains("{\"errorMessages\":[\"title : field is mandatory\"]}"));
	}
	
	
	
	

	
	
	
	
	
	
	
	/**************HELPER METHODS*************/
	
	/*
	 Helper method to create a new todo and return its id
	 */
	public String getTodoId() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"title\":\"Make photocopies\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		return parseJSON(response.getBody().toString())[0];
	}
	
	/**
	 * @author benjaminemiliani
	 * @param json: string in json format
	 * @return array of strings containing each field consecutively
	 */
	public String[] parseJSON(String json) {
		String data[] = new String[8]; 
		boolean flag = false;
		int j = 0;
		StringBuilder str = null;
		for(int i = 0; i < json.length(); i++) {
			if(json.charAt(i) == ':') {
				i = i + 2;
				flag = true;
				str = new StringBuilder();
			}
			if(flag && json.charAt(i) != '"') {
				str.append(json.charAt(i));
			}
			if(flag && json.charAt(i) == '"') {
				flag = false;
				data[j] = str.toString();
				j++;
			}
			
		}
		return data;
	}
}
