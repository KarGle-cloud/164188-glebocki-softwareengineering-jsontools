package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Decorator that removes specific keys from JSON objects.
 * All listed keys are recursively deleted.
 *
 * @author Karol Glebocki
 * @version 1.4
 */
public class ExcludeKeysDecorator extends JsonDecorator
{
    /**
     * Logger utility for tracking ExcludeKeys operation.
     */
    private static final Logger logger = LoggerFactory.getLogger(ExcludeKeysDecorator.class);

    /**
     * The set of keys we want to exclude.
     */
    private final Set<String> keysToExclude;

    /**
     * Jackson mapper for parsing and writing.
     */
    private final ObjectMapper mapper;

    /**
     * Sets up the exclude keys decorator.
     *
     * @param processor next processor in our chain
     * @param keys keys we want to remove
     */
    public ExcludeKeysDecorator(JsonProcessor processor, String[] keys)
    {
        super(processor);
        this.keysToExclude = keys != null ? new HashSet<>(Arrays.asList(keys)) : Collections.emptySet();
        this.mapper = new ObjectMapper();
        logger.debug("ExcludeKeysDecorator initialized with keys to exclude: {}", keysToExclude);
    }

    /**
     * Processes JSON, deleting targeted keys.
     *
     * @param jsonText raw input JSON
     * @return JSON string with excluded keys
     * @throws Exception if processing or exclusion fails
     */
    @Override
    public String process(String jsonText) throws Exception
    {
        logger.info("ExcludeKeysDecorator starting execution.");
        String result = super.process(jsonText);
        
        try
        {
            JsonNode rootNode = mapper.readTree(result);
            excludeNode(rootNode);
            String excluded = mapper.writeValueAsString(rootNode);
            logger.info("ExcludeKeysDecorator completed successfully.");
            return excluded;
        }
        catch (Exception e)
        {
            logger.error("ExcludeKeysDecorator failed to process JSON: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Recursive helper that traverses the tree and removes listed fields.
     *
     * @param node current node in traversal
     */
    private void excludeNode(JsonNode node)
    {
        if (node.isObject())
        {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<String> fieldNames = objectNode.fieldNames();
            Set<String> fieldsToRemove = new HashSet<>();
            
            while (fieldNames.hasNext())
            {
                String fieldName = fieldNames.next();
                if (keysToExclude.contains(fieldName))
                {
                    fieldsToRemove.add(fieldName);
                }
                else
                {
                    excludeNode(objectNode.get(fieldName));
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
                excludeNode(element);
            }
        }
    }
}
