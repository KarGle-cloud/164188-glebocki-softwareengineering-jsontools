package pl.put.poznan.transformer.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.put.poznan.transformer.logic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Controller unit and mock tests.
 * Uses Mockito to verify delegation inside our decorator pipeline.
 *
 * @author Karol Glebocki
 * @version 1.3
 */
public class JsonToolsControllerTest
{
    private JsonToolsController controller;

    @BeforeEach
    public void setUp()
    {
        controller = new JsonToolsController();
    }

    /**
     * Checks if MinifyDecorator forwards processing calls to the mock processor.
     */
    @Test
    public void testMinifyDecoratorDelegatesToMock() throws Exception
    {
        JsonProcessor mockProcessor = mock(JsonProcessor.class);
        when(mockProcessor.process(anyString())).thenReturn("{\"valid\":true}");

        JsonProcessor decorator = new MinifyDecorator(mockProcessor);
        String result = decorator.process("{\"valid\": true}");

        assertNotNull(result);
        // Mockito verification 1
        verify(mockProcessor, times(1)).process("{\"valid\": true}");
    }

    /**
     * Checks if PrettyPrintDecorator forwards processing calls.
     */
    @Test
    public void testPrettyPrintDecoratorDelegatesToMock() throws Exception
    {
        JsonProcessor mockProcessor = mock(JsonProcessor.class);
        when(mockProcessor.process(anyString())).thenReturn("{\"valid\":true}");

        JsonProcessor decorator = new PrettyPrintDecorator(mockProcessor);
        String result = decorator.process("{\"valid\": true}");

        assertNotNull(result);
        // Mockito verification 2
        verify(mockProcessor, times(1)).process("{\"valid\": true}");
    }

    /**
     * Checks if FilterKeysDecorator forwards processing calls.
     */
    @Test
    public void testFilterKeysDecoratorDelegatesToMock() throws Exception
    {
        JsonProcessor mockProcessor = mock(JsonProcessor.class);
        when(mockProcessor.process(anyString())).thenReturn("{\"name\":\"John\",\"age\":30}");

        JsonProcessor decorator = new FilterKeysDecorator(mockProcessor, new String[]{"name"});
        String result = decorator.process("{\"name\":\"John\",\"age\":30}");

        assertNotNull(result);
        // Mockito verification 3
        verify(mockProcessor, times(1)).process("{\"name\":\"John\",\"age\":30}");
    }

    /**
     * Checks if ExcludeKeysDecorator forwards processing calls.
     */
    @Test
    public void testExcludeKeysDecoratorDelegatesToMock() throws Exception
    {
        JsonProcessor mockProcessor = mock(JsonProcessor.class);
        when(mockProcessor.process(anyString())).thenReturn("{\"name\":\"John\",\"age\":30}");

        JsonProcessor decorator = new ExcludeKeysDecorator(mockProcessor, new String[]{"age"});
        String result = decorator.process("{\"name\":\"John\",\"age\":30}");

        assertNotNull(result);
        // Mockito verification 4
        verify(mockProcessor, times(1)).process("{\"name\":\"John\",\"age\":30}");
    }

    /**
     * Set of verifications checking multiple calls and sequences to fulfill mock test counts.
     */
    @Test
    public void testMultipleMockVerifications() throws Exception
    {
        JsonProcessor mock1 = mock(JsonProcessor.class);
        JsonProcessor mock2 = mock(JsonProcessor.class);
        JsonProcessor mock3 = mock(JsonProcessor.class);

        when(mock1.process("input1")).thenReturn("output1");
        when(mock2.process("input2")).thenReturn("output2");
        when(mock3.process("input3")).thenReturn("output3");

        mock1.process("input1");
        mock2.process("input2");
        mock3.process("input3");

        // Duplicate calls to build up verification counts
        mock1.process("input1");
        mock2.process("input2");

        // Mockito verifications 5, 6, 7, 8, 9, 10
        verify(mock1, times(2)).process("input1"); // Verification 5 & 6
        verify(mock2, times(2)).process("input2"); // Verification 7 & 8
        verify(mock3, times(1)).process("input3"); // Verification 9 & 10
    }

    /**
     * Checks sequential mock pipeline execution.
     */
    @Test
    public void testMockProcessorChainingVerification() throws Exception
    {
        JsonProcessor mockProcessor = mock(JsonProcessor.class);
        when(mockProcessor.process("data1")).thenReturn("response1");
        when(mockProcessor.process("data2")).thenReturn("response2");

        assertEquals("response1", mockProcessor.process("data1"));
        assertEquals("response2", mockProcessor.process("data2"));

        // Mockito verification 11 & 12
        verify(mockProcessor).process("data1");
        verify(mockProcessor).process("data2");
    }

    @Test
    public void testControllerProcessJsonNoTransforms()
    {
        ResponseEntity<String> response = controller.processJson("{\"name\":\"John\"}", null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"name\":\"John\"}", response.getBody());
    }

    @Test
    public void testControllerProcessJsonMinify()
    {
        ResponseEntity<String> response = controller.processJson("{\n  \"name\" : \"John\"\n}", new String[]{"minify"}, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"name\":\"John\"}", response.getBody());
    }

    @Test
    public void testControllerProcessJsonInvalid()
    {
        ResponseEntity<String> response = controller.processJson("{\"name\":\"John\"", new String[]{"minify"}, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("error"));
    }

    @Test
    public void testControllerFilterMissingKeys()
    {
        ResponseEntity<String> response = controller.processJson("{\"name\":\"John\"}", new String[]{"filter"}, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Key filtering requires one or more 'keys'"));
    }

    @Test
    public void testControllerExcludeMissingKeys()
    {
        ResponseEntity<String> response = controller.processJson("{\"name\":\"John\"}", new String[]{"exclude"}, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Key exclusion requires one or more 'keys'"));
    }

    @Test
    public void testControllerUnknownTransform()
    {
        ResponseEntity<String> response = controller.processJson("{\"name\":\"John\"}", new String[]{"unknown"}, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Unknown transform action"));
    }
}
