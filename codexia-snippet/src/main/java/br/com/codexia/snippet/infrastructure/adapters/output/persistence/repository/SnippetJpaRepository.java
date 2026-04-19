package br.com.codexia.snippet.infrastructure.adapters.output.persistence.repository;

import br.com.codexia.snippet.infrastructure.adapters.output.persistence.entity.SnippetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SnippetJpaRepository extends JpaRepository<SnippetJpaEntity, UUID> {
}
