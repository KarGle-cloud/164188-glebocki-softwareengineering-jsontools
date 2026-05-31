package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator that minifies JSON.
 * It removes all spaces, tabs, and newlines so the output fits in one line.
 *
 * @author Karol Glebocki
 * @version 1.1
 */
public class MinifyDecorator extends JsonDecorator
{
    /**
     * Logger utility for tracking Minify operation.
     */
    private static final Logger logger = LoggerFactory.getLogger(MinifyDecorator.class);

    /**
     * Jackson mapper for minified writes.
     */
    private final ObjectMapper mapper;

    /**
     * Wraps a processor with the minification decorator.
     *
     * @param processor next processor in our chain
     */
    public MinifyDecorator(JsonProcessor processor)
    {
        super(processor);
        this.mapper = new ObjectMapper();
        logger.debug("MinifyDecorator initialized.");
    }

    /**
     * Validates input JSON first, then minifies it.
     *
     * @param jsonText raw input JSON
     * @return minified single-line JSON string
     * @throws Exception if processing or minification fails
     */
    @Override
    public String process(String jsonText) throws Exception
    {
        logger.info("MinifyDecorator starting execution.");
        String result = super.process(jsonText);
        
        try
        {
            JsonNode rootNode = mapper.readTree(result);
            String minified = mapper.writeValueAsString(rootNode);
            logger.info("MinifyDecorator completed successfully.");
            return minified;
        }
        catch (Exception e)
        {
            logger.error("MinifyDecorator failed to process JSON: {}", e.getMessage());
            throw e;
        }
    }
}
