package br.com.codexia.snippet.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "br.com.codexia.snippet", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureDependencyTest {

    @ArchTest
    public static final ArchRule domain_should_not_depend_on_frameworks =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "javax.persistence.."
                    )
                    .because("O Domínio deve ser Java puro, isolado de bibliotecas de infraestrutura.");

    @ArchTest
    public static final ArchRule domain_should_not_depend_on_application_or_infrastructure =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..application..",
                            "..infrastructure.."
                    )
                    .because("O Domínio é o núcleo e não pode apontar para fora.");

    @ArchTest
    public static final ArchRule application_should_not_depend_on_infrastructure =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                    .because("A Camada de Aplicação deve depender apenas de interfaces (Ports) e do Domínio.");
}