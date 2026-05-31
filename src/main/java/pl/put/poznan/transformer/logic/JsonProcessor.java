package pl.put.poznan.transformer.logic;

/**
 * Base interface for processing JSON strings.
 * This is the component interface in our Decorator design pattern.
 *
 * @author Karol Glebocki
 * @version 1.0
 */
public interface JsonProcessor
{
    /**
     * Takes a JSON string and applies a transformation.
     *
     * @param jsonText input JSON string
     * @return the transformed JSON string
     * @throws Exception if something goes wrong (like invalid JSON)
     */
    String process(String jsonText) throws Exception;
}
