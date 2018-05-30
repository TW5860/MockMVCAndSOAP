package server;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
@ContextConfiguration(classes = WebServiceConfig.class)
@WebAppConfiguration("server")
@WebMvcTest
public class ApplicationTest {
	@Autowired
	private MockMvc mockMvc;
	
	private String readResource(String name) {
		InputStream inputStream = getClass().getResourceAsStream(name);
		return new BufferedReader(new InputStreamReader(inputStream))
		  .lines().collect(Collectors.joining("\n"));
	}

    @Test
    public void canReadCountry() throws Exception {
    	String body = readResource("/request.xml");
    	//System.out.println(body);
        this.mockMvc.perform(post("/ws").header("content-type", "text/xml").content(body))
        	.andExpect(status().isOk());
            //.andExpect(content().string(containsString("Hello World")));
    }
}