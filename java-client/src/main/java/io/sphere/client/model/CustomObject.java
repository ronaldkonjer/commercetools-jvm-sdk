package io.sphere.client.model;

import io.sphere.internal.command.Command;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * An object that contains a JSON value.
 */
public class CustomObject implements Command {
    private String container;
    private String key;
    private JsonNode value;

    public CustomObject(String container, String key, JsonNode value) {
        this.container = container;
        this.key = key;
        this.value = value;
    }

    // for JSON deserializer
    protected CustomObject() {
    }

    public String getContainer() {
        return container;
    }

    public String getKey() {
        return key;
    }

    public JsonNode getValue() {
        return value;
    }

    public <T> T as(Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(value, clazz);
    }

    @Override
    public String toString(){
        return String.format("[%s/%s value:%s]", container, key, value.toString());
    }
}
