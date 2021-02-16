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
	 * For GET /todos
	 * Get all todos in the system
	 */
	@Test
	public void testGetAllTodos() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		
		assertEquals(response.getStatusCode().toString(), "200 OK");
		assertTrue(response.getBody().contains("todos"));
	}
	
	/*
	 * For POST /todos with message body containing "title" : ""
	 * Create a new todo entity with the minimum requirement
	 * It is then delete to not alter state of the system after execution
	 */
	@Test
	public void createTodo() {
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
	
	/*
	 * For POST /todos without message body containing "title" : ""
	 * Create a new todo entity without the minimum requirement
	 */
	@Test
	public void createTodoWithNullTitle() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String requestJson = "{\"description\":\"Interview John Doe\"}";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(requestJson, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.POST, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "400 BAD_REQUEST");
		assertTrue(response.getBody().contains("{\"errorMessages\":[\"title : field is mandatory\"]}"));
	}
	
	/*
	 * For GET /todos/:id 
	 * Get a todo entity with "id": id
	 */
	@Test
	public void getTodoWithId() {
		headers.setContentType(MediaType.APPLICATION_JSON);	
		urlPath = urlPath.concat("/1");
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		assertTrue(response.getBody().toString().contains("{\"id\":\"1\"}"));
	}
	
	/*
	 * For GET /todos/:id 
	 * Get a todo entity with "id": id
	 */
	@Test
	public void deleteTodoWithId() {
		String idString = getTodoId();
		urlPath = urlPath.concat("/" + idString);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		
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
	 * @param id: id of todo to be deleted
	 */
	public void deleteTodoWithId(String id) {
		urlPath = urlPath.concat("/" + id);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlPath);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.DELETE, entity, String.class); 
	}
	
	/**
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