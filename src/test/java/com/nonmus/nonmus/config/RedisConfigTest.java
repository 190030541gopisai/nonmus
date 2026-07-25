package com.nonmus.nonmus.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RedisConfigTest {
    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withBean(RedisConnectionFactory.class,
                            () -> mock(RedisConnectionFactory.class))
                    .withUserConfiguration(RedisConfig.class);

    @Test
    void shouldRegisterStringRedisTemplateBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StringRedisTemplate.class);

            StringRedisTemplate template = context.getBean(StringRedisTemplate.class);

            assertThat(template).isNotNull();
            assertThat(template.getConnectionFactory())
                    .isSameAs(context.getBean(RedisConnectionFactory.class));
        });
    }
}