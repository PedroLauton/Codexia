package br.com.codexia.snippet.application.ports.input.tag;

import br.com.codexia.snippet.application.dto.command.DeleteTagCommand;

public interface DeleteTagUseCase {
    void execute(DeleteTagCommand command);
}
