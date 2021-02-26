package ca.mcgill.ecse429.todo;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ToDoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class SpringBootBaseIntegrationTest {
 

}
