package br.com.codexia.snippet.infrastructure.adapters.input.rest.controller;

import br.com.codexia.snippet.application.dto.command.*;
import br.com.codexia.snippet.application.dto.response.CategoryResponse;
import br.com.codexia.snippet.application.ports.input.category.*;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.CreateCategoryRequest;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.ReparentCategoryRequest;
import br.com.codexia.snippet.infrastructure.adapters.input.rest.dto.request.UpdateCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final RestoreCategoryUseCase restoreCategoryUseCase;
    private final PurgeCategoryUseCase purgeCategoryUseCase;
private final ReparentCategoryUseCase reparentCategoryUseCase;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase,
                              UpdateCategoryUseCase updateCategoryUseCase,
                              DeleteCategoryUseCase deleteCategoryUseCase,
                              RestoreCategoryUseCase restoreCategoryUseCase,
                              PurgeCategoryUseCase purgeCategoryUseCase,
                              ReparentCategoryUseCase reparentCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.restoreCategoryUseCase = restoreCategoryUseCase;
        this.purgeCategoryUseCase = purgeCategoryUseCase;
        this.reparentCategoryUseCase = reparentCategoryUseCase;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @PathVariable String workspaceId,
            @RequestBody @Valid CreateCategoryRequest request) {

        CreateCategoryCommand command = new CreateCategoryCommand(
                workspaceId,
                request.name(),
                request.description(),
                request.parentId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createCategoryUseCase.execute(command));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable String workspaceId,
            @PathVariable String categoryId,
            @RequestBody @Valid UpdateCategoryRequest request) {

        UpdateCategoryCommand command = new UpdateCategoryCommand(
                categoryId,
                workspaceId,
                request.name(),
                request.description()
        );

        return ResponseEntity.ok(updateCategoryUseCase.execute(command));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(
            @PathVariable String workspaceId,
            @PathVariable String categoryId) {

        deleteCategoryUseCase.execute(new DeleteCategoryCommand(categoryId, workspaceId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{categoryId}/restore")
    public ResponseEntity<CategoryResponse> restore(
            @PathVariable String workspaceId,
            @PathVariable String categoryId) {

        return ResponseEntity.ok(restoreCategoryUseCase.execute(new RestoreCategoryCommand(categoryId, workspaceId)));
    }

    @DeleteMapping("/{categoryId}/purge")
    public ResponseEntity<Void> purge(
            @PathVariable String workspaceId,
            @PathVariable String categoryId) {

        purgeCategoryUseCase.execute(new DeleteCategoryCommand(categoryId, workspaceId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{categoryId}/parent")
    public ResponseEntity<CategoryResponse> reparent(
            @PathVariable String workspaceId,
            @PathVariable String categoryId,
            @RequestBody ReparentCategoryRequest request) {

        ReparentCategoryCommand command = new ReparentCategoryCommand(
                categoryId,
                workspaceId,
                request.parentId()
        );

        return ResponseEntity.ok(reparentCategoryUseCase.execute(command));
    }
}
