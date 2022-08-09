package com.bigbank.game.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.util.List;

public class MockJsonMapper<T> {

    @SneakyThrows
    public T[] mockListResponse(final String jsonFile, final Class<T> typeParameterClass) {
        final Resource resource = new ClassPathResource("mock/" + jsonFile);
        final ObjectMapper objectMapper = new ObjectMapper();

        final List<T> dtoList = objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, typeParameterClass));
        return dtoList.toArray((T[]) Array.newInstance(typeParameterClass, dtoList.size()));
    }

    @SneakyThrows
    public T mockResponse(final String jsonFile, final Class<T> typeParameterClass) {
        final Resource resource = new ClassPathResource("mock/" + jsonFile);
        final ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(resource.getInputStream(),
                objectMapper.getTypeFactory().constructType(typeParameterClass));
    }

    @SneakyThrows
    public ResponseEntity<T> mockResponseEntity(final String jsonFile, final Class<T> typeParameterClass,
                                                final HttpStatus statusCode) {
        return new ResponseEntity<>(mockResponse(jsonFile, typeParameterClass), statusCode);
    }
}
