package ca.mcgill.ecse429.todo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.util.StringBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TodoManagerTests {
	
	@Autowired
	private TestRestTemplate restTemplate;
	private HttpHeaders headers = new HttpHeaders();
	private String urlPath = "http://localhost:4567/todos";

	/**
	 * Type: Capability
	 * For GET /todos
	 * Get all todos in the system
	 * @author benjaminemiliani
	 */
	@Test
	public void test_01GetAllTodos() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		
		assertEquals(response.getStatusCode().toString(), "200 OK");
		assertTrue(response.getBody().contains("todos"));
	}
	
	/**
	 * Type: Capability
	 * For POST /todos with message body containing "title" : ""
	 * Create a new todo entity with the minimum requirement
	 * It is then delete to not alter state of the system after execution
	 * @author benjaminemiliani
	 */
	@Test
	public void test_02CreateTodo() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"title\":\"Interview\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "201 CREATED");
		String idString = parseJSON(response.getBody().toString())[0];
		deleteTodoWithId(idString);
	}
	
	/**
	 * Type: Wrong body format
	 * For POST /todos with XML message body mal formatted
	 * Create a new todo entity not successful
	 * @author benjaminemiliani
	 */
	@Test
	public void test_03createTodoWithWrongXML() {
		headers.setContentType(MediaType.APPLICATION_XML);
		String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<todo>\n" + 
				"  <doneStatus>true</doneStatus>\n" + 
				"  <description>veniam, quis nostrua</description>\n" + 
				"  <id>null</id>\n" + 
				"</todo>";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(xmlString, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "400 BAD_REQUEST");
		
	}
	
	/**
	 * Type: Bug/Issue
	 * For POST /todos using query parameters
	 * Create a new todo entity not successful
	 * @author benjaminemiliani
	 */
	@Test
	public void test_04createTodoWithUrlQueryParams() {
		headers.setContentType(MediaType.APPLICATION_XML);
		urlPath.concat("?title=interview");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "400 BAD_REQUEST");
	}
	
	/**
	 * Type: wrong format of JSON payload
	 * For POST /todos without message body containing "title" : ""
	 * Create a new todo entity without the minimum requirement
	 * @author benjaminemiliani
	 */
	@Test
	public void test_05createTodoWithNullTitle() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"description\":\"Interview John Doe\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "400 BAD_REQUEST");
		assertTrue(response.getBody().contains("{\"errorMessages\":[\"title : field is mandatory\"]}"));
	}
	
	/**
	 * Type: Capability
	 * For GET /todos/:id 
	 * Get a todo entity with "id": id
	 * @author benjaminemiliani
	 */
	@Test
	public void test_06getTodoWithId() {
		headers.setContentType(MediaType.APPLICATION_JSON);	
		urlPath = urlPath.concat("/1");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		assertTrue(response.getBody().toString().contains("{\"id\":\"1\"}"));
	}
	
	
	/**
	 * Type: capability
	 * For POST /todos/:id 
	 * Update a specific instances of todo using a id with a body containing the fields to amend
	 * @author benjaminemiliani
	 */
	@Test
	public void test_07updateTodo() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		urlPath = urlPath.concat("/1");
		String requestJson = "{\"doneStatus\":true}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		
		//Undo the change
		String requestJson2 = "{\"doneStatus\":false}";
		entity = new HttpEntity<Object>(requestJson2, headers);
		 response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
	}
	
	/**
	 * Type: Capability
	 * For DELETE /todos/:id 
	 * Get a todo entity with "id": id
	 * @author benjaminemiliani
	 */
	@Test
	public void test_08deleteTodoWithId() {
		String idString = getTodoId();
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
	}
	
	/**
	 * Type: Capability
	 * For GET /todos/:id/tasksof
	 * Get all the project entities related to todo, 
	 * with given id, by the relationship named tasksof
	 * @author benjaminemiliani
	 */
	@Test
	public void test_09getProjectsWithTodoId() {
		headers.setContentType(MediaType.APPLICATION_JSON);	
		urlPath = urlPath.concat("/1/tasksof");
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		assertTrue(response.getBody().toString().contains("{\"id\":\"1\"}"));
	}
	

	/**
	 * Type: Bug
	 * For GET /todos/:id/tasksof
	 * Get all the project entities related to todo, 
	 * with a non-existing id, by the relationship named tasksof
	 * @author benjaminemiliani
	 */
	@Test
	public void test_10getProjectsWithInvalidTodoId() {
		headers.setContentType(MediaType.APPLICATION_JSON);	
		//todo with id = 21212921 is never creatd during tests nor in intial setup 
		urlPath = urlPath.concat("/21212921/tasksof");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		//The resonse status should be 404 Not Found
		assertEquals(response.getStatusCode().toString(), "200 OK");
		assertTrue(response.getBody().toString().contains("{\"id\":\"1\"}"));
	}
	
	/**
	 * Type: Capability
	 * For POST /todos/:id/tasksof
	 * Make a specific todo a tasksof a specific project using JSON payload
	 * @author benjaminemiliani
	 */
	@Test
	public void test_11makeTasksofWithTodoId() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		// Create a new todo for testing
		String idString = getTodoId();
		urlPath = urlPath.concat("/" + idString + "/tasksof");
		String requestJson = "{\"id\":\"1\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "201 CREATED");
		// Delete newly created todo after test assertion
		deleteTodoWithId(idString);
	}
	
	/**
	 * Type: Capability
	 * For DELETE /todos/:id/tasksof
	 * Delete the instance of the relationship named tasksof 
	 * between todo and project using the :id
	 * @author benjaminemiliani
	 */
	@Test
	public void test_12deleteTasksofWithId() {
		String idString = makeTasksof();
		urlPath = urlPath.concat("/1");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		deleteTodoWithId(idString);
	}
	
	/**
	 * Type: Capability
	 * For GET /todos/:id/categories
	 * Get all the category items related to todo, with given id, by the relationship named categories
	 * @author benjaminemiliani
	 */
	@Test
	public void test_13getCategoriesWithTodoId() {
		headers.setContentType(MediaType.APPLICATION_JSON);	
		urlPath = urlPath.concat("/1/categories");
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		assertTrue(response.getBody().toString().contains("{\"categories\":"));
	}
	
	/**
	 * Type: Capability
	 * For POST /todos/:id/categories
	 * Create an create an instance of a relationship named categories between todo instance :id and the category instance
	 * @author benjaminemiliani
	 */
	@Test
	public void test_14makeCategoriesWithTodoId() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		// Create a new todo for testing
		String idString = getTodoId();
		urlPath = urlPath.concat("/" + idString + "/categories");
		String requestJson = "{\"id\":\"1\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "201 CREATED");
		// Delete newly created todo after test assertion
		deleteTodoWithId(idString);
	}
	

	
	
	
		/******************HELPER METHODS*********************/
	
	public String makeTasksof() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		// Create a new todo for testing
		String idString = getTodoId();
		urlPath = urlPath.concat("/" + idString + "/tasksof");
		String requestJson = "{\"id\":\"1\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		return idString;
	}
	
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
	 * Helper method to delete a todo created for testing
	 * @author benjaminemiliani
	 * @param id: id of todo to be deleted
	 */
	public void deleteTodoWithId(String id) {
		urlPath = "http://localhost:4567/todos/" + id ;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
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