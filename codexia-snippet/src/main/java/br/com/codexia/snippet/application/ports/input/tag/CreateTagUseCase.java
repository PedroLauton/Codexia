package br.com.codexia.snippet.application.ports.input.tag;

import br.com.codexia.snippet.application.dto.command.CreateTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;

public interface CreateTagUseCase {
    TagResponse execute(CreateTagCommand command);
}
