package dcb.config.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import dcb.config.models.ComponentPort;

import java.lang.reflect.Type;

public class ComponentPortDeserializer implements JsonDeserializer<ComponentPort> {
    @Override
    public ComponentPort deserialize(JsonElement json,
                                     Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {
        String[] values = json.getAsString().split("\\.");
        if (values.length != 2) {
            throw new JsonParseException("did not follow pattern componentName.portName");
        }

        return new ComponentPort(values[0], values[1]);
    }
}
