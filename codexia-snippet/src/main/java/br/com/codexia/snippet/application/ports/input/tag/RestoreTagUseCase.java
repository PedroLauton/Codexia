package br.com.codexia.snippet.application.ports.input.tag;

import br.com.codexia.snippet.application.dto.command.RestoreTagCommand;
import br.com.codexia.snippet.application.dto.response.TagResponse;

public interface RestoreTagUseCase {
    TagResponse execute(RestoreTagCommand command);
}
