package br.com.codexia.snippet.infrastructure.adapters.input.rest.controller;

import br.com.codexia.snippet.application.dto.command.*;
import br.com.codexia.snippet.application.dto.response.*;
import br.com.codexia.snippet.application.ports.input.snippet.*;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.AddSnippetVersionRequest;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.CreateSnippetRequest;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.ReassignSnippetRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/snippets")
public class SnippetController {

    private final CreateSnippetUseCase createSnippetUseCase;
    private final ReassignSnippetUseCase reassignSnippetUseCase;
    private final DeleteSnippetUseCase deleteSnippetUseCase;
    private final AddSnippetVersionUseCase addSnippetVersionUseCase;

    public SnippetController(CreateSnippetUseCase createSnippetUseCase,
                             ReassignSnippetUseCase reassignSnippetUseCase,
                             DeleteSnippetUseCase deleteSnippetUseCase,
                             AddSnippetVersionUseCase addSnippetVersionUseCase) {
        this.createSnippetUseCase = createSnippetUseCase;
        this.reassignSnippetUseCase = reassignSnippetUseCase;
        this.deleteSnippetUseCase = deleteSnippetUseCase;
        this.addSnippetVersionUseCase = addSnippetVersionUseCase;
    }

    @PostMapping
    public ResponseEntity<SnippetResponse> create(
            @PathVariable String workspaceId,
            @RequestBody @Valid CreateSnippetRequest request) {

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

        return ResponseEntity.status(HttpStatus.CREATED).body(createSnippetUseCase.execute(command));
    }

    @PutMapping("/{snippetId}/category")
    public ResponseEntity<SnippetReassignedResponse> reassign(
            @PathVariable String workspaceId,
            @PathVariable String snippetId,
            @RequestBody ReassignSnippetRequest request) {

        ReassignSnippetCommand command = new ReassignSnippetCommand(
                snippetId,
                workspaceId,
                request.categoryId(),
                request.tagIds()
        );

        return ResponseEntity.ok(reassignSnippetUseCase.execute(command));
    }

    @DeleteMapping("/{snippetId}")
    public ResponseEntity<Void> delete(
            @PathVariable String workspaceId,
            @PathVariable String snippetId) {

        deleteSnippetUseCase.execute(new DeleteSnippetCommand(snippetId, workspaceId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{snippetId}/versions")
    public ResponseEntity<SnippetVersionAddedResponse> addVersion(
            @PathVariable String workspaceId,
            @PathVariable String snippetId,
            @RequestBody @Valid AddSnippetVersionRequest request) {

        AddSnippetVersionCommand command = new AddSnippetVersionCommand(
                snippetId,
                workspaceId,
                request.title(),
                request.description(),
                request.content(),
                request.language()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(addSnippetVersionUseCase.execute(command));
    }
}
