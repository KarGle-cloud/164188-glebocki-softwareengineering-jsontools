package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator that pretty-prints JSON.
 * It reformats the input with spaces and indentations to make it readable.
 *
 * @author Karol Glebocki
 * @version 1.1
 */
public class PrettyPrintDecorator extends JsonDecorator
{
    /**
     * Logger utility for tracking PrettyPrint operation.
     */
    private static final Logger logger = LoggerFactory.getLogger(PrettyPrintDecorator.class);

    /**
     * Jackson mapper for pretty writes.
     */
    private final ObjectMapper mapper;

    /**
     * Wraps a processor with the pretty-print decorator.
     *
     * @param processor next processor in our chain
     */
    public PrettyPrintDecorator(JsonProcessor processor)
    {
        super(processor);
        this.mapper = new ObjectMapper();
        logger.debug("PrettyPrintDecorator initialized.");
    }

    /**
     * Validates input JSON first, then pretty-prints it.
     *
     * @param jsonText raw input JSON
     * @return pretty-printed indented JSON string
     * @throws Exception if processing or pretty-printing fails
     */
    @Override
    public String process(String jsonText) throws Exception
    {
        logger.info("PrettyPrintDecorator starting execution.");
        String result = super.process(jsonText);
        
        try
        {
            JsonNode rootNode = mapper.readTree(result);
            String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            logger.info("PrettyPrintDecorator completed successfully.");
            return pretty;
        }
        catch (Exception e)
        {
            logger.error("PrettyPrintDecorator failed to process JSON: {}", e.getMessage());
            throw e;
        }
    }
}
