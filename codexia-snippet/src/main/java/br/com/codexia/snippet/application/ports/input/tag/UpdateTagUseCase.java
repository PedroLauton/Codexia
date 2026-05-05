package br.com.codexia.snippet.application.ports.input.tag;

import br.com.codexia.snippet.application.dto.command.UpdateTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;

public interface UpdateTagUseCase {
    TagResponse execute(UpdateTagCommand command);
}
