package acmemedical.rest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Unit tests for the ConfigureJacksonObjectMapper class.
 * 
 * @author Harmeet Matharoo
 */
public class ConfigureJacksonObjectMapperTest {

    private ConfigureJacksonObjectMapper configureJacksonObjectMapper;

    @BeforeEach
    public void setUp() {
        configureJacksonObjectMapper = new ConfigureJacksonObjectMapper();
    }

    /**
     * Test that the configured ObjectMapper is not null.
     */
    @Test
    public void testObjectMapperIsNotNull() {
        ObjectMapper objectMapper = configureJacksonObjectMapper.getContext(ObjectMapper.class);
        assertNotNull(objectMapper, "Configured ObjectMapper should not be null");
    }

    /**
     * Test that the ObjectMapper has the correct modules registered.
     */
    @Test
    public void testObjectMapperHasJavaTimeModule() {
        ObjectMapper objectMapper = configureJacksonObjectMapper.getContext(ObjectMapper.class);
        assertTrue(objectMapper.getRegisteredModuleIds().contains(JavaTimeModule.class.getName()), 
            "ObjectMapper should have JavaTimeModule registered");
    }

    /**
     * Test that the ObjectMapper is configured correctly for date/time serialization.
     */
    @Test
    public void testObjectMapperDateSerialization() {
        ObjectMapper objectMapper = configureJacksonObjectMapper.getContext(ObjectMapper.class);
        assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS), 
            "ObjectMapper should be configured to not write dates as timestamps");
    }

    /**
     * Test that the ObjectMapper is configured correctly for empty beans serialization.
     */
    @Test
    public void testObjectMapperEmptyBeansSerialization() {
        ObjectMapper objectMapper = configureJacksonObjectMapper.getContext(ObjectMapper.class);
        assertFalse(objectMapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS), 
            "ObjectMapper should not fail on empty beans");
    }

    /**
     * Test that the ObjectMapper is configured correctly for unknown properties during deserialization.
     */
    @Test
    public void testObjectMapperUnknownProperties() {
        ObjectMapper objectMapper = configureJacksonObjectMapper.getContext(ObjectMapper.class);
        assertFalse(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES), 
            "ObjectMapper should not fail on unknown properties during deserialization");
    }
}
