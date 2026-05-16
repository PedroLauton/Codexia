package br.com.codexia.snippet.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "br.com.codexia.snippet", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureDependencyTest {

    // ─── Domain isolation ─────────────────────────────────────────────────────

    @ArchTest
    static ArchRule domainMustNotDependOnInfrastructure =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

    @ArchTest
    static ArchRule domainMustNotDependOnApplication =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..application..");

    @ArchTest
    static ArchRule domainMustNotImportSpring =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static ArchRule domainMustNotImportJpa =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..");

    @ArchTest
    static ArchRule domainMustNotImportValidation =
            noClasses().that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.validation..");

    // ─── Application isolation ────────────────────────────────────────────────

    @ArchTest
    static ArchRule applicationMustNotDependOnInfrastructure =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

    @ArchTest
    static ArchRule applicationMustNotImportSpring =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static ArchRule applicationMustNotImportJpa =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..");

    @ArchTest
    static ArchRule applicationMustNotImportValidation =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("jakarta.validation..");

    @ArchTest
    static ArchRule applicationMustNotUseSpringData =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("org.springframework.data..");

    // ─── @Transactional boundary ──────────────────────────────────────────────

    @ArchTest
    static ArchRule transactionalClassesMustBeInPersistenceAdapter =
            classes().that().areAnnotatedWith(Transactional.class)
                    .should().resideInAPackage("..infrastructure.adapters.output.persistence.adapter..");

    @ArchTest
    static ArchRule transactionalMethodsMustBeInPersistenceAdapter =
            methods().that().areAnnotatedWith(Transactional.class)
                    .should().beDeclaredInClassesThat()
                    .resideInAPackage("..infrastructure.adapters.output.persistence.adapter..")
                    .allowEmptyShould(true);

    // ─── Spring stereotype boundary ───────────────────────────────────────────

    @ArchTest
    static ArchRule noSpringStereotypesInUseCases =
            noClasses().that().resideInAPackage("..application.usecase..")
                    .should().beAnnotatedWith(Service.class)
                    .orShould().beAnnotatedWith(Component.class)
                    .orShould().beAnnotatedWith(Repository.class);

    // ─── Adapter isolation ────────────────────────────────────────────────────

    @ArchTest
    static ArchRule inputAdaptersMustNotDependOnOutputAdapters =
            noClasses().that().resideInAPackage("..infrastructure.adapters.input..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.adapters.output..");

    @ArchTest
    static ArchRule outputAdaptersMustNotDependOnInputAdapters =
            noClasses().that().resideInAPackage("..infrastructure.adapters.output..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.adapters.input..");
}
