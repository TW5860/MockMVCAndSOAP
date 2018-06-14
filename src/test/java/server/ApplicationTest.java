package server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlunit.util.Nodes;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
	class SOAPNamespaceContext implements NamespaceContext {
	    @Override
	    public Iterator<?> getPrefixes(String arg0) {
	        return null;
	    }
	    @Override
	    public String getPrefix(String arg0) {
	        return null;
	    }
	    @Override
	    public String getNamespaceURI(String prefix) {
	        if (prefix.equals("env")) {
	            return "http://schemas.xmlsoap.org/soap/envelope/";
	        } else if (prefix.equals("ctn")) {
	            return "http://spring.io/guides/gs-producing-web-service";
	        }
	        return null;
	    }
	}
	
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
    		.body(BodyInserters.fromObject(body)).exchange().expectStatus().isOk()
    		.expectBody()
    		.consumeWith(bodyTextWrapper -> {
    			String bodyText;
    			NodeList nodes;
   
    			try {
					bodyText = new String(bodyTextWrapper.getResponseBodyContent(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					fail();
					return;
				}
    			
    			XPath xpath = XPathFactory.newInstance().newXPath();
    			xpath.setNamespaceContext(new SOAPNamespaceContext());

    			String expression = "/env:Envelope/env:Body/ctn:getCountryResponse/ctn:country/ctn:capital";
    			InputSource inputSource = new InputSource(new StringReader(bodyText));
    			try {
					nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
				} catch (XPathExpressionException e) {
					fail();
					return;
				}
    			
    			assertEquals(1, nodes.getLength());
    			assertEquals("Madrid", nodes.item(0).getTextContent());
    		});
	}
}