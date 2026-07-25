package com.nonmus.nonmus.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ModelMapperConfigTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
                .withUserConfiguration(ModelMapperConfig.class);

    @Test
    void shouldRegisterModelMapperBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ModelMapper.class);

            ModelMapper modelMapper = context.getBean(ModelMapper.class);

            assertThat(modelMapper).isNotNull();
        });
    }
}