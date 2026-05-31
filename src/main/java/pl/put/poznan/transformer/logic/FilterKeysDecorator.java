package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Decorator that keeps only specified keys in JSON objects.
 * Everything else gets recursively deleted from the structure.
 *
 * @author Karol Glebocki
 * @version 1.4
 */
public class FilterKeysDecorator extends JsonDecorator
{
    /**
     * Logger utility for tracking FilterKeys operation.
     */
    private static final Logger logger = LoggerFactory.getLogger(FilterKeysDecorator.class);

    /**
     * The set of keys we want to keep.
     */
    private final Set<String> keysToKeep;

    /**
     * Jackson mapper for parsing and writing.
     */
    private final ObjectMapper mapper;

    /**
     * Sets up the filter keys decorator.
     *
     * @param processor next processor in our chain
     * @param keys keys we want to keep
     */
    public FilterKeysDecorator(JsonProcessor processor, String[] keys)
    {
        super(processor);
        this.keysToKeep = keys != null ? new HashSet<>(Arrays.asList(keys)) : Collections.emptySet();
        this.mapper = new ObjectMapper();
        logger.debug("FilterKeysDecorator initialized with keys to keep: {}", keysToKeep);
    }

    /**
     * Processes JSON, keeping only target keys.
     *
     * @param jsonText raw input JSON
     * @return filtered JSON string
     * @throws Exception if processing or filtering fails
     */
    @Override
    public String process(String jsonText) throws Exception
    {
        logger.info("FilterKeysDecorator starting execution.");
        String result = super.process(jsonText);
        
        try
        {
            JsonNode rootNode = mapper.readTree(result);
            filterNode(rootNode);
            String filtered = mapper.writeValueAsString(rootNode);
            logger.info("FilterKeysDecorator completed successfully.");
            return filtered;
        }
        catch (Exception e)
        {
            logger.error("FilterKeysDecorator failed to process JSON: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Recursive helper that traverses the tree and removes unlisted fields.
     *
     * @param node current node in traversal
     */
    private void filterNode(JsonNode node)
    {
        if (node.isObject())
        {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            Set<String> fieldsToRemove = new HashSet<>();
            
            while (fieldNames.hasNext())
            {
                String fieldName = fieldNames.next();
                if (!keysToKeep.contains(fieldName))
                {
                    fieldsToRemove.add(fieldName);
                }
                else
                {
                    filterNode(objectNode.get(fieldName));
                }
            }
            
            if (!fieldsToRemove.isEmpty())
            {
                logger.debug("Removing fields {} from ObjectNode.", fieldsToRemove);
                objectNode.remove(fieldsToRemove);
            }
        }
        else if (node.isArray())
        {
            for (JsonNode element : node)
            {
                filterNode(element);
            }
        }
    }
}
