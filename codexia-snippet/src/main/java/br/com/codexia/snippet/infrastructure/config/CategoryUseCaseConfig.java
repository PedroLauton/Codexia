package br.com.codexia.snippet.infrastructure.config;

import br.com.codexia.snippet.application.ports.input.category.CreateCategoryUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.usecase.category.CreateCategoryUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryCommandPort categoryCommandPort, CategoryQueryPort categoryQueryPort) {
        return new CreateCategoryUseCaseImpl(categoryCommandPort,categoryQueryPort);
    }
}
