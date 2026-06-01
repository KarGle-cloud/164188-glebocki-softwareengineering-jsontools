# JSON Tools - Software Engineering Project

This is a Spring Boot application designed for processing and transforming JSON data. The project was built using the Decorator design pattern, which makes it easy to chain different JSON operations together

## Implemented Features

1. **Minify JSON**: Strips out all spaces and formatting to put the JSON on a single line.
2. **Pretty Print**: Formats the JSON with indentations to make it readable.
3. **Filter Keys**: Keeps only the specified keys and removes everything else.
4. **Exclude Keys**: Deletes specific keys from the JSON objects.
5. **Error Reporting**: Validates the JSON syntax and returns a bad request error with a description if the JSON is malformed.

## Architecture

I used the Decorator design pattern for the JSON processing pipeline:
- `JsonProcessor` - The base interface.
- `BaseJsonProcessor` - The core class that validates the JSON.
- `JsonDecorator` - Abstract decorator class.
- Concrete decorators: `MinifyDecorator`, `PrettyPrintDecorator`, `FilterKeysDecorator`, and `ExcludeKeysDecorator`.

---

## REST API Endpoint

The server exposes a single POST endpoint to run the transformations.

- **URL**: `http://localhost:8080/api/json`
- **Method**: `POST`
- **Headers**: `Content-Type: application/json`
- **Query Params**:
  - `transforms` (optional): comma-separated list of actions to apply (minify, pretty, filter, exclude).
  - `keys` (optional): comma-separated list of keys for filtering or excluding.

### Test Examples

#### 1. Minify Example
- **URL**: `http://localhost:8080/api/json?transforms=minify`
- **Input**:
  ```json
  {
    "title": "JSON Tools",
    "version": 1.1
  }
  ```
- **Output**: `{"title":"JSON Tools","version":1.1}`

#### 2. Pretty-Printing Example
- **URL**: `http://localhost:8080/api/json?transforms=pretty`
- **Input**: `{"title":"JSON Tools"}`
- **Output**:
  ```json
  {
    "title": "JSON Tools",
    "version": 1.1
  }
  ```

---

## How to Run

1. **Run Unit and Mock Tests**:
   ```bash
   mvn clean test
   ```
   This compiles the project and runs all 30 tests.

2. **Start the Application**:
   ```bash
   mvn spring-boot:run
   ```
   This will start the local Tomcat server on port 8080.
