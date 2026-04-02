package com.remizov.deal.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import org.springframework.util.ResourceUtils;

@UtilityClass
public class JsonTestDataUtils {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public <T> T read(String path, Class<T> clazz) throws Exception {
        return mapper.readValue(ResourceUtils.getFile("classpath:" + path), clazz);
    }
}