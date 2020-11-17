package laboratory.astrea.buitlin.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.core.ParameterizedTypeReference;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static laboratory.astrea.buitlin.core.Functions.Try;

public final class Json {

    private static final String EMPTY_OBJECT = "{}";
    private static final SimpleModule PARAMETER_NAMES_MODULE = new ParameterNamesModule();
    private static final SimpleModule JAVA_TIME_MODULE = new JavaTimeModule();
    private static final Jdk8Module JDK_8_MODULE = new Jdk8Module();

    private static final ObjectMapper OBJECT_MAPPER;

    static {


        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.FAIL_ON_EMPTY_BEANS);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        OBJECT_MAPPER.registerModules(JAVA_TIME_MODULE, PARAMETER_NAMES_MODULE, JDK_8_MODULE);
        ObjectMapper.findModules(Json.class.getClassLoader()).forEach(OBJECT_MAPPER::registerModules);

    }


    private Json() {
        throw new UnsupportedOperationException();
    }


    public static String jsonString(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return Functions.Try(() -> OBJECT_MAPPER.writeValueAsString(value));
    }

    public static <T> T jsonValue(String jsonString, Class<T> clazz) {
        return Functions.Try(() -> OBJECT_MAPPER.readValue(jsonString, clazz));
    }

    public static <T> T jsonValue(String jsonString, ParameterizedTypeReference<T> typeReference) {
        final var javaType = OBJECT_MAPPER.constructType(typeReference.getType());
        return Functions.Try(() -> OBJECT_MAPPER.readValue(jsonString, javaType));
    }

    public static <T> T jsonValue(JsonNode jsonNode, Class<T> clazz) {
        return Functions.Try(() -> OBJECT_MAPPER.convertValue(jsonNode, clazz));
    }

    public static <T> List<T> jsonList(String content, Class<T> elementType) {

        final var type = Parameterized.makeList(elementType);

        final var javaType = OBJECT_MAPPER.constructType(type);

        return Functions.Try(() -> OBJECT_MAPPER.readValue(content, javaType));
    }

    public static <T> List<T> jsonList(JsonNode jsonNode, Class<T> elementType) {

        final var type = Parameterized.makeList(elementType);

        final var javaType = OBJECT_MAPPER.constructType(type);

        return Functions.Try(() -> OBJECT_MAPPER.convertValue(jsonNode, javaType));
    }

    public static <K, V> Map<K, V> jsonMap(String content, Class<K> keyType, Class<V> valueType) {

        final var type = Parameterized.makeMap(keyType, valueType);

        final var javaType = OBJECT_MAPPER.constructType(type);

        return Functions.Try(() -> OBJECT_MAPPER.readValue(content, javaType));
    }

    public static JsonNode jsonNode(Object value) {
        return Functions.Try(() -> OBJECT_MAPPER.convertValue(value, JsonNode.class));
    }

    public static JsonNode jsonNode(String value) {
        return Functions.Try(() -> OBJECT_MAPPER.readValue(value, JsonNode.class));
    }

    public static JsonNode jsonNode(byte[] value){
        return Functions.Try(() -> OBJECT_MAPPER.readValue(value, JsonNode.class));
    }

    public static JsonNode jsonNode(File file) {
        return Functions.Try(() -> OBJECT_MAPPER.readValue(file, JsonNode.class));
    }

    public static JsonNode jsonNode(Path path) {
        return jsonNode(path.toFile());
    }

    public static void addProperty(JsonNode jsonNode, String name, String value) {
        ((ObjectNode) jsonNode).put(name, value);
    }

    public static <T> T convertValue(Object value, ParameterizedTypeReference<T> typeReference) {
        final var javaType = OBJECT_MAPPER.constructType(typeReference.getType());
        return OBJECT_MAPPER.convertValue(value, javaType);
    }

    public static boolean isEmptyObject(String value){
        return EMPTY_OBJECT.equals(value);
    }

    public static Predicate<String> emptyObject(){
        return EMPTY_OBJECT::equals;
    }

}
