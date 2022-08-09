package com.bigbank.game.config;

import com.bigbank.game.mock.MockJsonMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public MockJsonMapper<?> mockJsonMapper() {
        return new MockJsonMapper<>();
    }
}
