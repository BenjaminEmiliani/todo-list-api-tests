package ca.mcgill.ecse429.todo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	ResponseEntity<String> temp;
	private String idString;
	private String projectId;
	private String categoryId;
	
	@Given("I am logged into the server")
	public void iAmLoggedIntoTheServer() {
		restTemplate = new TestRestTemplate();
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		urlPath = "http://localhost:4567/todos";
	}
	
	@When("I create a todo with title only")
	public void iCreateATodoWithTitleOnly() {
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
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals("200 OK", response.getStatusCode().toString());
		deleteTodoWithId(idString);
	}
	
	
	@When("I create a todo with title and description")
	public void iCreateATodoWithTitleAndDesc() {
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
	
	
	
	
	@And("There exists a todo with title \"(.*?)\"")
	public void thereExistsATodoWithTitle(String title) {
		urlPath = "http://localhost:4567/todos";
		String requestJson = "{\"title\":\""  + title   + "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		idString = parseJSON(response.getBody().toString())[0];
	}
	
	@When ("I delete the todo \"(.*?)\"")
	public void iDeleteTheTodo(String title) {
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
	}
	
	@Then("The todo no longer exists")
	public void theTodoNoLongerExists() {	
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals("404 NOT_FOUND", response.getStatusCode().toString());
	}
	
	
	@When("I delete the todo with invalid id (\\d+)")
	public void iDeleteTheTodoWithInvalidId(int id) {
		urlPath = urlPath.concat("/" + id);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
	}
	
	@Then("I am informed that the todo was not found")
	public void iAmInformedThatTheRequestWasBad() {
		assertEquals("404 NOT_FOUND", response.getStatusCode().toString());
	}
	

	
	
	
	@Given("There exists a project with title \"(.*?)\"")
	public void thereExistsAProjectWithTitle(String title) {
		urlPath = "http://localhost:4567/projects";
		String requestJson = "{\"title\":\""  + title   + "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		projectId = parseJSON(response.getBody().toString())[0];
	}
	
	@When("I create the relationship \"(.*?)\"")
	public void iCreateTheRelationship(String relation) {
		urlPath = "http://localhost:4567/todos";
		String param = (relation.equals("tasksof")) ? projectId : categoryId;
		urlPath = urlPath.concat("/" + idString + "/" + relation);
		String requestJson = "{\"id\":\""  + param   + "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		 response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		 temp = response;
	}
	
	@Then("^The project \"(.*?)\" will be related to \"(.*?)\"$")
	public void theProjectWillBeRelatedTo(String projectTitle, String todoTitle) {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString + "/tasksof");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals("200 OK", response.getStatusCode().toString());
		assertTrue(response.getBody().toString().contains("\"title\":\""  + projectTitle + "\""));
		deleteTodoWithId(idString);
		deleteProjectWithId(projectId);
	}
	
	
	
	@When ("I create the relationship with invalid project id")
	public void iCreateTheRelationshipWithInvalidProjectId() {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString + "/tasksof");
		String requestJson = "{\"id\":\""  + 1230293021   + "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		 response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
	}
	
	@Then("^The project \"(.*?)\" will not be related to \"(.*?)\"$")
	public void theProjectWillNotBeRelatedTo(String projectTitle, String todoTitle) {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString + "/tasksof");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertFalse(response.getBody().toString().contains("\"title\":\""  + projectTitle + "\""));
		deleteTodoWithId(idString);
		deleteProjectWithId(projectId);
	}
	
	
	
	@And("There exists a todo with doneStatus of false and title \"(.*?)\"")
	public void thereExistsATodoWithTitleAndFalse(String title) {
		String requestJson = "{\"title\":\""  + title   + "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		idString = parseJSON(response.getBody().toString())[0];
	}
	
	@When("I update the doneStatus to true")
	public void iUpdateTheDoneStatusToTrue() {
		urlPath = urlPath.concat("/" + idString);
		String requestJson = "{\"doneStatus\":true}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
	}
	
	@When("I update the description to \"(.*?)\"")
	public void iUpdateTheDoneStatusTo(String desc) {
		String requestJson = "{\"description\":\"" + desc +  "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
	}
	
	@Then("I am informed the change was okay")
	public void iAmInformedOfTheChange() {
		assertEquals("200 OK", response.getStatusCode().toString());
	}
	
	
	@And("todo \"Design New Logo\" will have doneStatus of true")
	public void todoWillHaveDoneStatusTrue() {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertTrue(response.getBody().contains("\"doneStatus\":\"true\""));
		deleteTodoWithId(idString);
	}
	
	@When("I update the title to empty string")
	public void updateTheTitleToNull() {
		String requestJson = "{\"title\":\"\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
	}
	
	@Then("I am informed the title cannot be empty")
	public void amInformedTheTitleCannotBeEmpty() {
		assertTrue(response.getBody().contains("Failed Validation: title : can not be empty"));
	}
	
	@And("Todo will have title \"(.*?)\"")
	public void todoWillHaveTitle(String title) {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertTrue(response.getBody().contains("\"title\":\"" + title + "\""));
		deleteTodoWithId(idString);
	}
	
	
	
	
	
	@Given("There exists a category with title \"(.*?)\"")
	public void thereExistsACategoryWithTitle(String title) {
		urlPath = "http://localhost:4567/categories";
		String requestJson = "{\"title\":\""  + title   + "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		categoryId = parseJSON(response.getBody().toString())[0];
	}
	
	
	@Then("^The category \"(.*?)\" will be related to the todo")
	public void theCategoryWillBeRelatedTo(String projectTitle) {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString + "/categories");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals("200 OK", response.getStatusCode().toString());
		assertTrue(response.getBody().toString().contains("\"title\":\""  + projectTitle + "\""));
		deleteTodoWithId(idString);
		deleteCategoryWithId(categoryId);
	}
	
	@And("I am notified the relationship is created")
	public void iAmNotifiedTheRelationshipIsCreated() {
		assertEquals("201 CREATED", temp.getStatusCode().toString());
	}
	
	
	@When ("I create the relationship with invalid category id")
	public void iCreateTheRelationshipWithInvalidCategoryId() {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString + "/category");
		String requestJson = "{\"id\":\""  + 1230293021   + "\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		 response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
	}
	
	
	@Then("^The todo will not be related to \"(.*?)\"$")
	public void theCategoryWillNotBeRelatedTo(String categoryTitle) {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + idString + "/categories");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertFalse(response.getBody().toString().contains("\"title\":\""  + categoryTitle + "\""));
		deleteTodoWithId(idString);
		deleteCategoryWithId(categoryId);
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
	
	public void deleteTodoWithId(String id) {
		urlPath = "http://localhost:4567/todos";
		urlPath = urlPath.concat("/" + id);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
	}
	
	public void deleteProjectWithId(String id) {
		urlPath = "http://localhost:4567/projects";
		urlPath = urlPath.concat("/" + id);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
	}
	
	public void deleteCategoryWithId(String id) {
		urlPath = "http://localhost:4567/categories";
		urlPath = urlPath.concat("/" + id);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
	}
}
