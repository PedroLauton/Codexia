package br.com.codexia.snippet.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "br.com.codexia.snippet", importOptions = ImportOption.DoNotIncludeTests.class)
class NamingConventionTest {

    @ArchTest
    static ArchRule useCaseImplsMustImplementInterface =
            classes().that().resideInAPackage("..application.usecase..")
                    .and().haveSimpleNameEndingWith("Impl")
                    .should(implementInterfaceEndingWith("UseCase"));

    @ArchTest
    static ArchRule controllersMustEndWithController =
            classes().that().resideInAPackage("..infrastructure.adapters.input.rest.controller..")
                    .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static ArchRule persistenceAdaptersMustEndWithAdapter =
            classes().that().resideInAPackage("..infrastructure.adapters.output.persistence.adapter..")
                    .should().haveSimpleNameEndingWith("Adapter");

    @ArchTest
    static ArchRule jpaEntitiesMustEndWithJpaEntity =
            classes().that().resideInAPackage("..infrastructure.adapters.output.persistence.entity..")
                    .and().areTopLevelClasses()
                    .should().haveSimpleNameEndingWith("JpaEntity");

    @ArchTest
    static ArchRule repositoriesMustEndWithJpaRepository =
            classes().that().areInterfaces().and().resideInAPackage("..infrastructure.adapters.output.persistence.repository..")
                    .should().haveSimpleNameEndingWith("JpaRepository");

    @ArchTest
    static ArchRule domainExceptionsMustEndWithException =
            classes().that().resideInAPackage("..domain.exception..")
                    .should().haveSimpleNameEndingWith("Exception");

    @ArchTest
    static ArchRule inputPortsMustEndWithUseCase =
            classes().that().areInterfaces().and().resideInAPackage("..application.ports.input..")
                    .should().haveSimpleNameEndingWith("UseCase");

    @ArchTest
    static ArchRule outputPortsMustEndWithPort =
            classes().that().areInterfaces().and().resideInAPackage("..application.ports.output..")
                    .should().haveSimpleNameEndingWith("Port");

    private static ArchCondition<JavaClass> implementInterfaceEndingWith(String suffix) {
        return new ArchCondition<>("implement an interface ending with '" + suffix + "'") {
            @Override
            public void check(JavaClass clazz, ConditionEvents events) {
                boolean ok = clazz.getRawInterfaces().stream()
                        .anyMatch(i -> i.getSimpleName().endsWith(suffix));
                if (!ok) {
                    events.add(SimpleConditionEvent.violated(clazz,
                            clazz.getName() + " does not implement any interface ending with '" + suffix + "'"));
                }
            }
        };
    }
}
