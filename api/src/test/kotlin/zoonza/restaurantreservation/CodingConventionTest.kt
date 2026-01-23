package zoonza.restaurantreservation

import com.tngtech.archunit.core.domain.JavaCall.Predicates.target
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameStartingWith
import com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

@AnalyzeClasses(packages = ["zoonza.restaurantreservation"], importOptions = [ImportOption.DoNotIncludeTests::class])
class CodingConventionTest {
    @ArchTest
    fun setter(classes: JavaClasses) {
        noClasses().that().resideInAnyPackage("..application..", "..adapter..")
            .should().callMethodWhere(
                target(nameStartingWith("set"))
                .and(target(owner(resideInAPackage("..domain.."))))
            )
            .check(classes)
    }
}