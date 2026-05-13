package br.com.codexia.snippet.infrastructure.adapters.input.rest.controller;

import br.com.codexia.snippet.application.dto.command.*;
import br.com.codexia.snippet.application.dto.response.TagResponse;
import br.com.codexia.snippet.application.ports.input.tag.*;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.CreateTagRequest;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.UpdateTagRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/tags")
public class TagController {

    private final CreateTagUseCase createTagUseCase;
    private final UpdateTagUseCase updateTagUseCase;
    private final DeleteTagUseCase deleteTagUseCase;
    private final RestoreTagUseCase restoreTagUseCase;
    private final PurgeTagUseCase purgeTagUseCase;

    public TagController(CreateTagUseCase createTagUseCase,
                         UpdateTagUseCase updateTagUseCase,
                         DeleteTagUseCase deleteTagUseCase,
                         RestoreTagUseCase restoreTagUseCase,
                         PurgeTagUseCase purgeTagUseCase) {
        this.createTagUseCase = createTagUseCase;
        this.updateTagUseCase = updateTagUseCase;
        this.deleteTagUseCase = deleteTagUseCase;
        this.restoreTagUseCase = restoreTagUseCase;
        this.purgeTagUseCase = purgeTagUseCase;
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(
            @PathVariable String workspaceId,
            @RequestBody @Valid CreateTagRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createTagUseCase.execute(new CreateTagCommand(workspaceId, request.title(), request.hexColor())));
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<TagResponse> update(
            @PathVariable String workspaceId,
            @PathVariable String tagId,
            @RequestBody @Valid UpdateTagRequest request) {

        return ResponseEntity.ok(
                updateTagUseCase.execute(new UpdateTagCommand(tagId, workspaceId, request.title(), request.hexColor())));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> delete(
            @PathVariable String workspaceId,
            @PathVariable String tagId) {

        deleteTagUseCase.execute(new DeleteTagCommand(tagId, workspaceId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tagId}/restore")
    public ResponseEntity<TagResponse> restore(
            @PathVariable String workspaceId,
            @PathVariable String tagId) {

        return ResponseEntity.ok(
                restoreTagUseCase.execute(new RestoreTagCommand(tagId, workspaceId)));
    }

    @DeleteMapping("/{tagId}/purge")
    public ResponseEntity<Void> purge(
            @PathVariable String workspaceId,
            @PathVariable String tagId) {

        purgeTagUseCase.execute(new PurgeTagCommand(tagId, workspaceId));
        return ResponseEntity.noContent().build();
    }
}
