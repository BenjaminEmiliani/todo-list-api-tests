package ca.mcgill.ecse429.todo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.Assert.assertEquals;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

	// GROUP 44
	@Test
	public void testGetAllTodos() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "http://localhost:4567/todos";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
		HttpEntity<?> entity = new HttpEntity<Object>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(),HttpMethod.GET, entity, String.class);
		//String result = response.getBody().toString(); 
		assertEquals(response.getStatusCode().toString(), "200 OK");
		
	}

}