package pl.put.poznan.transformer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.put.poznan.transformer.logic.*;

import java.util.Arrays;

/**
 * Spring REST controller for JSON tools.
 * Exposes a POST endpoint at /api/json to process strings in the request body.
 *
 * @author Karol Glebocki
 * @version 1.5
 */
@RestController
@RequestMapping("/api")
public class JsonToolsController
{
    /**
     * Logger utility for recording request parameters and processing phases.
     */
    private static final Logger logger = LoggerFactory.getLogger(JsonToolsController.class);

    /**
     * POST endpoint that processes JSON.
     * Takes transforms in query parameters to stack decorators.
     *
     * @param jsonContent the raw input JSON in the POST body
     * @param transforms list of operations (minify, pretty, filter, exclude)
     * @param keys keys list for filter or exclude operations
     * @return transformed JSON or error response
     */
    @PostMapping(value = "/json", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processJson(
            @RequestBody String jsonContent,
            @RequestParam(value = "transforms", required = false) String[] transforms,
            @RequestParam(value = "keys", required = false) String[] keys)
    {
        logger.info("Received request to process JSON.");
        logger.debug("Request body: {}", jsonContent);
        logger.debug("Applied transforms: {}", transforms != null ? Arrays.toString(transforms) : "none");
        logger.debug("Keys parameter: {}", keys != null ? Arrays.toString(keys) : "none");

        try
        {
            // Instantiate the base component
            JsonProcessor processor = new BaseJsonProcessor();

            // Chain decorators dynamically based on the requested order of transforms
            if (transforms != null)
            {
                for (String transform : transforms)
                {
                    if ("minify".equalsIgnoreCase(transform))
                    {
                        logger.info("Chaining MinifyDecorator to pipeline.");
                        processor = new MinifyDecorator(processor);
                    }
                    else if ("pretty".equalsIgnoreCase(transform) || "prettyprint".equalsIgnoreCase(transform))
                    {
                        logger.info("Chaining PrettyPrintDecorator to pipeline.");
                        processor = new PrettyPrintDecorator(processor);
                    }
                    else if ("filter".equalsIgnoreCase(transform))
                    {
                        if (keys == null || keys.length == 0)
                        {
                            logger.warn("Filter transform requested but no keys provided.");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("{\"error\": \"Invalid parameters: Key filtering requires one or more 'keys' to keep.\"}");
                        }
                        logger.info("Chaining FilterKeysDecorator to pipeline with keys: {}", Arrays.toString(keys));
                        processor = new FilterKeysDecorator(processor, keys);
                    }
                    else if ("exclude".equalsIgnoreCase(transform))
                    {
                        if (keys == null || keys.length == 0)
                        {
                            logger.warn("Exclude transform requested but no keys provided.");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("{\"error\": \"Invalid parameters: Key exclusion requires one or more 'keys' to exclude.\"}");
                        }
                        logger.info("Chaining ExcludeKeysDecorator to pipeline with keys: {}", Arrays.toString(keys));
                        processor = new ExcludeKeysDecorator(processor, keys);
                    }
                    else
                    {
                        logger.warn("Unknown transform requested: {}", transform);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("{\"error\": \"Invalid parameters: Unknown transform action '" + transform + "'. Available actions: minify, pretty, filter, exclude.\"}");
                    }
                }
            }

            // Execute the pipeline
            String outputJson = processor.process(jsonContent);
            logger.info("JSON processing pipeline completed successfully.");
            return ResponseEntity.ok(outputJson);
        }
        catch (com.fasterxml.jackson.core.JsonProcessingException e)
        {
            logger.error("JSON parsing error during pipeline execution: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Invalid JSON format: " + e.getOriginalMessage() + "\"}");
        }
        catch (IllegalArgumentException e)
        {
            logger.error("Invalid arguments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
        catch (Exception e)
        {
            logger.error("Internal error processing JSON pipeline: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An internal processing error occurred: " + e.getMessage() + "\"}");
        }
    }
}
