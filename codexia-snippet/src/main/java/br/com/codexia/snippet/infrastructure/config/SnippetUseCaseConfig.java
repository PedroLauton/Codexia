package br.com.codexia.snippet.infrastructure.config;

import br.com.codexia.snippet.application.ports.input.snippet.*;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.SnippetQueryPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.shared.SnippetFinder;
import br.com.codexia.snippet.application.usecase.snippet.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnippetUseCaseConfig {

    @Bean
    public SnippetFinder snippetFinder(SnippetQueryPort snippetQueryPort) {
        return new SnippetFinder(snippetQueryPort);
    }

    @Bean
    public CreateSnippetUseCase createSnippetUseCase(SnippetCommandPort snippetCommandPort,
                                                      CategoryQueryPort categoryQueryPort,
                                                      TagQueryPort tagQueryPort) {
        return new CreateSnippetUseCaseImpl(snippetCommandPort, categoryQueryPort, tagQueryPort);
    }

    @Bean
    public ReassignSnippetUseCase reassignSnippetUseCase(SnippetCommandPort snippetCommandPort,
                                                          SnippetFinder snippetFinder,
                                                          CategoryQueryPort categoryQueryPort,
                                                          TagQueryPort tagQueryPort) {
        return new ReassignSnippetUseCaseImpl(snippetCommandPort, snippetFinder, categoryQueryPort, tagQueryPort);
    }

    @Bean
    public AddSnippetVersionUseCase addSnippetVersionUseCase(SnippetCommandPort snippetCommandPort,
                                                              SnippetFinder snippetFinder) {
        return new AddSnippetVersionUseCaseImpl(snippetCommandPort, snippetFinder);
    }

    @Bean
    public DeleteSnippetUseCase deleteSnippetUseCase(SnippetCommandPort snippetCommandPort,
                                                      SnippetFinder snippetFinder) {
        return new DeleteSnippetUseCaseImpl(snippetCommandPort, snippetFinder);
    }
}
