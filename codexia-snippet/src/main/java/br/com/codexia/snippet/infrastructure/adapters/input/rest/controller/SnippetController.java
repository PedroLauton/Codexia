package br.com.codexia.snippet.infrastructure.adapters.input.rest.controller;

import br.com.codexia.snippet.application.dto.command.CreateSnippetCommand;
import br.com.codexia.snippet.application.dto.response.SnippetResponse;
import br.com.codexia.snippet.application.ports.input.CreateSnippetUseCase;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.CreateSnippetRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/snippets")
public class SnippetController {

    private final CreateSnippetUseCase createSnippetUseCase;

    public SnippetController(CreateSnippetUseCase createSnippetUseCase) {
        this.createSnippetUseCase = createSnippetUseCase;
    }

    @PostMapping
    public ResponseEntity<SnippetResponse> create(
            @RequestBody @Valid CreateSnippetRequest request) {

        // Por enquanto hardcoded — será extraído do JWT quando segurança for implementada
        String workspaceId = "a0000000-0000-0000-0000-000000000001";
        String accountId = "b0000000-0000-0000-0000-000000000001";

        CreateSnippetCommand command = new CreateSnippetCommand(
                workspaceId,
                accountId,
                request.categoryId(),
                request.tagIds(),
                request.title(),
                request.description(),
                request.content(),
                request.language()
        );

        SnippetResponse response = createSnippetUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
