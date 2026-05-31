package pl.put.poznan.transformer.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base decorator class. 
 * Holds a reference to the wrapped processor and passes calls to it.
 *
 * @author Karol Glebocki
 * @version 1.0
 */
public abstract class JsonDecorator implements JsonProcessor
{
    /**
     * Logger utility for decorators.
     */
    private static final Logger logger = LoggerFactory.getLogger(JsonDecorator.class);

    /**
     * The nested processor we are decorating.
     */
    protected final JsonProcessor processor;

    /**
     * Wraps the given processor.
     *
     * @param processor next processor in our chain
     */
    public JsonDecorator(JsonProcessor processor)
    {
        if (processor == null)
        {
            logger.error("Attempted to construct JsonDecorator with null processor.");
            throw new IllegalArgumentException("Processor delegate cannot be null.");
        }
        this.processor = processor;
        logger.debug("JsonDecorator constructed around: {}", processor.getClass().getSimpleName());
    }

    /**
     * Passes the processing call to the wrapped processor.
     *
     * @param jsonText input JSON string
     * @return output from next processor in chain
     * @throws Exception if processing fails
     */
    @Override
    public String process(String jsonText) throws Exception
    {
        logger.debug("Delegating processing from decorator to: {}", processor.getClass().getSimpleName());
        return processor.process(jsonText);
    }
}
