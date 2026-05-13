package br.com.codexia.snippet.infrastructure.config;

import br.com.codexia.snippet.application.ports.input.tag.*;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.shared.TagFinder;
import br.com.codexia.snippet.application.usecase.tag.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TagUseCaseConfig {

    @Bean
    public TagFinder tagFinder(TagQueryPort tagQueryPort) {
        return new TagFinder(tagQueryPort);
    }

    @Bean
    public CreateTagUseCase createTagUseCase(TagCommandPort tagCommandPort,
                                              TagQueryPort tagQueryPort) {
        return new CreateTagUseCaseImpl(tagCommandPort, tagQueryPort);
    }

    @Bean
    public DeleteTagUseCase deleteTagUseCase(TagCommandPort tagCommandPort,
                                              TagFinder tagFinder) {
        return new DeleteTagUseCaseImpl(tagCommandPort, tagFinder);
    }

    @Bean
    public UpdateTagUseCase updateTagUseCase(TagCommandPort tagCommandPort,
                                              TagFinder tagFinder,
                                              TagQueryPort tagQueryPort) {
        return new UpdateTagUseCaseImpl(tagCommandPort, tagFinder, tagQueryPort);
    }

    @Bean
    public RestoreTagUseCase restoreTagUseCase(TagCommandPort tagCommandPort,
                                                TagFinder tagFinder) {
        return new RestoreTagUseCaseImpl(tagCommandPort, tagFinder);
    }

    @Bean
    public PurgeTagUseCase purgeTagUseCase(TagCommandPort tagCommandPort,
                                            TagFinder tagFinder) {
        return new PurgeTagUseCaseImpl(tagCommandPort, tagFinder);
    }
}
