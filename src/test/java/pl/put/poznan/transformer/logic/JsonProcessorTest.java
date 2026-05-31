package pl.put.poznan.transformer.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JSON processors and decorators.
 * Checks minification, formatting, key filtering, exclusion, and exceptions.
 *
 * @author Karol Glebocki
 * @version 2.1
 */
public class JsonProcessorTest
{
    private JsonProcessor baseProcessor;

    @BeforeEach
    public void setUp()
    {
        baseProcessor = new BaseJsonProcessor();
    }

    @Test
    public void testBaseProcessorValidJson() throws Exception
    {
        String input = "{\"name\":\"John\",\"age\":30}";
        String result = baseProcessor.process(input);
        assertEquals("{\"name\":\"John\",\"age\":30}", result);
    }

    @Test
    public void testBaseProcessorInvalidJson()
    {
        String input = "{\"name\":\"John\",age:30"; // missing closing brace and quote
        assertThrows(Exception.class, () -> baseProcessor.process(input));
    }

    @Test
    public void testBaseProcessorEmptyInput()
    {
        assertThrows(IllegalArgumentException.class, () -> baseProcessor.process(""));
    }

    @Test
    public void testMinifySimpleObject() throws Exception
    {
        JsonProcessor minifier = new MinifyDecorator(baseProcessor);
        String input = "{\n  \"name\" : \"John\",\n  \"age\" : 30\n}";
        String result = minifier.process(input);
        assertEquals("{\"name\":\"John\",\"age\":30}", result);
    }

    @Test
    public void testMinifyNestedObject() throws Exception
    {
        JsonProcessor minifier = new MinifyDecorator(baseProcessor);
        String input = "{\n  \"person\" : {\n    \"name\" : \"Alice\"\n  }\n}";
        String result = minifier.process(input);
        assertEquals("{\"person\":{\"name\":\"Alice\"}}", result);
    }

    @Test
    public void testMinifyArray() throws Exception
    {
        JsonProcessor minifier = new MinifyDecorator(baseProcessor);
        String input = "[ 1, 2,   3 ]";
        String result = minifier.process(input);
        assertEquals("[1,2,3]", result);
    }

    @Test
    public void testMinifyInvalidJson()
    {
        JsonProcessor minifier = new MinifyDecorator(baseProcessor);
        assertThrows(Exception.class, () -> minifier.process("{invalid}"));
    }

    @Test
    public void testPrettyPrintSimple() throws Exception
    {
        JsonProcessor pretty = new PrettyPrintDecorator(baseProcessor);
        String input = "{\"name\":\"John\",\"age\":30}";
        String result = pretty.process(input);
        // Assert that newlines and indentations were added
        assertTrue(result.contains("\n"));
        assertTrue(result.contains("John"));
    }

    @Test
    public void testPrettyPrintNested() throws Exception
    {
        JsonProcessor pretty = new PrettyPrintDecorator(baseProcessor);
        String input = "{\"person\":{\"name\":\"Alice\"}}";
        String result = pretty.process(input);
        assertTrue(result.contains("\n"));
        assertTrue(result.contains("Alice"));
    }

    @Test
    public void testPrettyPrintInvalidJson()
    {
        JsonProcessor pretty = new PrettyPrintDecorator(baseProcessor);
        assertThrows(Exception.class, () -> pretty.process("{invalid}"));
    }

    @Test
    public void testFilterKeysSimple() throws Exception
    {
        String[] keysToKeep = {"name"};
        JsonProcessor filter = new FilterKeysDecorator(baseProcessor, keysToKeep);
        String input = "{\"name\":\"John\",\"age\":30,\"city\":\"NY\"}";
        String result = filter.process(input);
        assertEquals("{\"name\":\"John\"}", result);
    }

    @Test
    public void testFilterKeysNested() throws Exception
    {
        String[] keysToKeep = {"name", "person"};
        JsonProcessor filter = new FilterKeysDecorator(baseProcessor, keysToKeep);
        String input = "{\"person\":{\"name\":\"Alice\",\"age\":25},\"city\":\"NY\"}";
        String result = filter.process(input);
        assertEquals("{\"person\":{\"name\":\"Alice\"}}", result);
    }

    @Test
    public void testFilterKeysKeepNonExistent() throws Exception
    {
        String[] keysToKeep = {"salary"};
        JsonProcessor filter = new FilterKeysDecorator(baseProcessor, keysToKeep);
        String input = "{\"name\":\"John\",\"age\":30}";
        String result = filter.process(input);
        assertEquals("{}", result);
    }

    @Test
    public void testExcludeKeysSimple() throws Exception
    {
        String[] keysToExclude = {"age", "city"};
        JsonProcessor exclude = new ExcludeKeysDecorator(baseProcessor, keysToExclude);
        String input = "{\"name\":\"John\",\"age\":30,\"city\":\"NY\"}";
        String result = exclude.process(input);
        assertEquals("{\"name\":\"John\"}", result);
    }

    @Test
    public void testExcludeKeysNested() throws Exception
    {
        String[] keysToExclude = {"age", "city"};
        JsonProcessor exclude = new ExcludeKeysDecorator(baseProcessor, keysToExclude);
        String input = "{\"person\":{\"name\":\"Alice\",\"age\":25},\"city\":\"NY\"}";
        String result = exclude.process(input);
        assertEquals("{\"person\":{\"name\":\"Alice\"}}", result);
    }

    @Test
    public void testExcludeKeysRemoveNonExistent() throws Exception
    {
        String[] keysToExclude = {"salary"};
        JsonProcessor exclude = new ExcludeKeysDecorator(baseProcessor, keysToExclude);
        String input = "{\"name\":\"John\",\"age\":30}";
        String result = exclude.process(input);
        assertEquals("{\"name\":\"John\",\"age\":30}", result);
    }

    @Test
    public void testChainedMinifyAndFilter() throws Exception
    {
        String[] keysToKeep = {"name"};
        JsonProcessor pipeline = new MinifyDecorator(new FilterKeysDecorator(baseProcessor, keysToKeep));
        String input = "{\n  \"name\" : \"John\",\n  \"age\" : 30\n}";
        String result = pipeline.process(input);
        assertEquals("{\"name\":\"John\"}", result);
    }

    @Test
    public void testChainedPrettyAndExclude() throws Exception
    {
        String[] keysToExclude = {"age"};
        JsonProcessor pipeline = new PrettyPrintDecorator(new ExcludeKeysDecorator(baseProcessor, keysToExclude));
        String input = "{\"name\":\"John\",\"age\":30}";
        String result = pipeline.process(input);
        assertTrue(result.contains("\n"));
        assertTrue(result.contains("John"));
        assertFalse(result.contains("age"));
    }
}
