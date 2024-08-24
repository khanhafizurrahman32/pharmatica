package org.example.pharmaticb.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.utilities.Exception.ServiceError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Configuration
public class R2dbcConfiguration {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, Arrays.asList(new JsonToJsonNodeConverter(), new JsonNodeToJsonConverter()));
    }

    @ReadingConverter
    private static class JsonToJsonNodeConverter implements Converter<Json, JsonNode> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public JsonNode convert(Json json) {
            try {
                return objectMapper.readTree(json.asString());
            } catch (JsonProcessingException e) {
                throw new InternalException(HttpStatus.INTERNAL_SERVER_ERROR, "Error to read json", ServiceError.JSON_CONVERSION_ERROR);
            }
        }
    }

    @WritingConverter
    private static class JsonNodeToJsonConverter implements Converter<JsonNode, Json> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Json convert(JsonNode jsonNode) {
            try {
                return Json.of(objectMapper.writeValueAsString(jsonNode));
            } catch (JsonProcessingException e) {
                throw new InternalException(HttpStatus.INTERNAL_SERVER_ERROR, "Error to write json", ServiceError.JSON_CONVERSION_ERROR);
            }
        }
    }
}
