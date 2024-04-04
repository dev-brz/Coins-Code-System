package com.cgzt.coinscode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {
    JavaClasses javaClasses = new ClassFileImporter().importPackages("com.cgzt.coinscode");

    String[] blackListedPackages = new String[]{
            //"..adapter..", // FIXME. Will fail. Resolve in [#73].
            "..command..", "..domains..", "..entity..", "..inbounds..", "..mapper..",
            //"..model..", // FIXME. Will fail. Resolve in [#73].
            "..outbounds..", "..port..", "..query..",
            // "..repository..", "..service..", // FIXME. Will fail. Resolve in [#73].
            "..strategy.."};

    @Test
    void domain_shouldNotDependOnAdapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..adapters..");

        rule.check(javaClasses);
    }

    @Test
    void inboundAdapters_shouldNotUseDomainModels() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..adapters.inbound..")
                .and().haveSimpleNameNotContaining("Test")
                .should().dependOnClassesThat()
                .resideInAPackage("..domain.models..");

        rule.check(javaClasses);
    }

    @Test
    @Disabled
        // FIXME. Will fail. Resolve in [#73].
    void allPackagesExpectCore_shouldContainAdaptersAndDomainPackagesOnly() {
        ArchRule rule = classes()
                .that().resideOutsideOfPackage("..core..")
                .and().resideOutsideOfPackage("com.cgzt.coinscode")
                .should().resideInAPackage("..adapters..")
                .orShould().resideInAPackage("..domain..");

        rule.check(javaClasses);
    }

    @Test
    void corePackage_shouldNotContainAdaptersAndDomainPackages() {
        ArchRule rule = classes()
                .that().resideInAPackage("..core..")
                .should().resideOutsideOfPackage("..adapters.")
                .orShould().resideOutsideOfPackage("..domain..");

        rule.check(javaClasses);
    }

    @Test
    void adaptersPackages_shouldContainInboundAndOutboundPackagesOnly() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapters..")
                .should().resideInAPackage("..adapters.inbound..")
                .orShould().resideInAPackage("..adapters.outbound..");

        rule.check(javaClasses);
    }

    @Test
    void domainPackages_shouldContainModelsAndPortsPackagesOnly() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.models..")
                .orShould().resideInAPackage("..domain.ports..");

        rule.check(javaClasses);
    }

    @Test
    void portsPackages_shouldContainInboundAndOutboundPackagesOnly() {
        ArchRule rule = classes()
                .that().resideInAPackage("..ports..")
                .should().resideInAPackage("..ports.inbound..")
                .orShould().resideInAPackage("..ports.outbound..");

        rule.check(javaClasses);
    }

    @Test
    void packagesWithFollowingNames_shouldNotExist() {
        ArchRule rule = noClasses()
                .should().resideInAnyPackage(blackListedPackages);

        rule.check(javaClasses);
    }

    @Test
    void testClasses_shouldResideInTheSamePackageAsImplementation() {
        ArchRule rule = GeneralCodingRules.testClassesShouldResideInTheSamePackageAsImplementation();

        rule.check(javaClasses);
    }

    @Test
    void code_shouldAdhereToGeneralCodingRules() {
        List<ArchRule> generalRules = List.of(
                GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS,
                GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS,
                GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING,
                GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME,
                GeneralCodingRules.ASSERTIONS_SHOULD_HAVE_DETAIL_MESSAGE,
                GeneralCodingRules.DEPRECATED_API_SHOULD_NOT_BE_USED
        );

        generalRules.forEach(rule -> rule.check(javaClasses));
    }
}
