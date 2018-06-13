package server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
	@Autowired
	private WebTestClient webClient;

	private String readResource(String name) {
		InputStream inputStream = getClass().getResourceAsStream(name);
		return new BufferedReader(new InputStreamReader(inputStream))
		  .lines().collect(Collectors.joining("\n"));
	}

	@Test
	public void exampleTest() {
    	String body = readResource("/request.xml");
    	this.webClient.post().uri("/ws").header("content-type", "text/xml")
    		.body(BodyInserters.fromObject(body)).exchange().expectStatus().isOk();
	}
}