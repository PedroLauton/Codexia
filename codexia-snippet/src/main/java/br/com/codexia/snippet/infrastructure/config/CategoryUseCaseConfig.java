package br.com.codexia.snippet.infrastructure.config;

import br.com.codexia.snippet.application.ports.input.category.*;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.application.usecase.category.*;
import br.com.codexia.snippet.application.usecase.shared.CategoryFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryUseCaseConfig {

    @Bean
    public CategoryFinder categoryFinder(CategoryQueryPort categoryQueryPort) {
        return new CategoryFinder(categoryQueryPort);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryCommandPort categoryCommandPort,
                                                        CategoryFinder categoryFinder,
                                                        CategoryQueryPort categoryQueryPort) {
        return new CreateCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryCommandPort categoryCommandPort,
                                                        CategoryFinder categoryFinder,
                                                        CategoryQueryPort categoryQueryPort) {
        return new UpdateCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(CategoryCommandPort categoryCommandPort,
                                                        CategoryFinder categoryFinder,
                                                        SnippetQueryPort snippetQueryPort) {
        return new DeleteCategoryUseCaseImpl(categoryCommandPort, categoryFinder, snippetQueryPort);
    }

    @Bean
    public RestoreCategoryUseCase restoreCategoryUseCase(CategoryCommandPort categoryCommandPort,
                                                          CategoryFinder categoryFinder) {
        return new RestoreCategoryUseCaseImpl(categoryCommandPort, categoryFinder);
    }

    @Bean
    public PurgeCategoryUseCase purgeCategoryUseCase(CategoryCommandPort categoryCommandPort,
                                                      CategoryFinder categoryFinder,
                                                      CategoryQueryPort categoryQueryPort) {
        return new PurgeCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }

    @Bean
    public ReparentCategoryUseCase reparentCategoryUseCase(CategoryCommandPort categoryCommandPort,
                                                            CategoryFinder categoryFinder,
                                                            CategoryQueryPort categoryQueryPort) {
        return new ReparentCategoryUseCaseImpl(categoryCommandPort, categoryFinder, categoryQueryPort);
    }
}
