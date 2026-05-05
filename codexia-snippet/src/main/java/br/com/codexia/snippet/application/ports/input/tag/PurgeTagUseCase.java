package br.com.codexia.snippet.application.ports.input.tag;

import br.com.codexia.snippet.application.dto.command.PurgeTagCommand;

public interface PurgeTagUseCase {
    void execute(PurgeTagCommand command);
}
