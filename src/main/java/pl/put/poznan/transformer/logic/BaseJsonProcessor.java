package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base JSON processor.
 * It parses the input string, checks if the JSON is valid, and returns it.
 *
 * @author Karol Glebocki
 * @version 1.3
 */
public class BaseJsonProcessor implements JsonProcessor
{
    /**
     * Logger for keeping track of the validation step.
     */
    private static final Logger logger = LoggerFactory.getLogger(BaseJsonProcessor.class);

    /**
     * Jackson mapper for parsing JSON trees.
     */
    private final ObjectMapper objectMapper;

    /**
     * Constructor that sets up the Jackson ObjectMapper.
     */
    public BaseJsonProcessor()
    {
        this.objectMapper = new ObjectMapper();
        logger.debug("BaseJsonProcessor initialized.");
    }

    /**
     * Checks if input JSON is valid. Throws an error if syntax is wrong.
     *
     * @param jsonText raw input string
     * @return clean serialized JSON string
     * @throws Exception if JSON syntax is incorrect
     */
    @Override
    public String process(String jsonText) throws Exception
    {
        logger.info("BaseJsonProcessor starting validation of input JSON.");
        if (jsonText == null || jsonText.trim().isEmpty())
        {
            logger.warn("Input JSON text is null or empty.");
            throw new IllegalArgumentException("Input JSON text cannot be empty.");
        }
        
        try
        {
            // Parse to validate JSON syntax
            JsonNode rootNode = objectMapper.readTree(jsonText);
            logger.debug("Successfully parsed JSON tree.");
            
            // Return serialized JSON string
            String serialized = objectMapper.writeValueAsString(rootNode);
            logger.info("BaseJsonProcessor successfully validated and serialized JSON.");
            return serialized;
        }
        catch (Exception e)
        {
            logger.error("JSON Parsing failed: {}", e.getMessage());
            throw e;
        }
    }
}
