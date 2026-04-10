package br.com.codexia.snippet.domain.model;

import java.util.Arrays;

public enum Language {
    JAVA("java", ".java"),
    PYTHON("python", ".py"),
    JAVASCRIPT("javascript", ".js"),
    TYPESCRIPT("typescript", ".ts"),
    CSHARP("csharp", ".cs"),
    GO("go", ".go"),
    RUST("rust", ".rs"),
    SQL("sql", ".sql"),
    HTML("html", ".html"),
    CSS("css", ".css"),
    MARKDOWN("markdown", ".md"),
    JSON("json", ".json"),
    YAML("yaml", ".yaml");

    private final String slug;
    private final String extension;

    Language(String slug, String extension) {
        this.slug = slug;
        this.extension = extension;
    }

    public String getSlug() { return slug; }
    public String getExtension() { return extension; }

    public static Language fromString(String value) {
        return Arrays.stream(Language.values())
                .filter(lang -> lang.name().equalsIgnoreCase(value) || lang.slug.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unsupported language: " + value));
    }
}
