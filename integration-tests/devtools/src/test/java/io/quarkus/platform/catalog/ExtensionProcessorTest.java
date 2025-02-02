package io.quarkus.platform.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.quarkus.devtools.testing.PlatformAwareTestBase;
import io.quarkus.maven.ArtifactKey;
import io.quarkus.platform.catalog.processor.ExtendedKeywords;
import io.quarkus.platform.catalog.processor.ExtensionProcessor;
import io.quarkus.registry.catalog.Extension;
import io.quarkus.registry.catalog.ExtensionCatalog;

public class ExtensionProcessorTest extends PlatformAwareTestBase {

    @Test
    void testRESTEasyMetadata() {
        final ExtensionCatalog catalog = getExtensionsCatalog();
        final Extension resteasy = findExtension(catalog, "quarkus-resteasy");
        final ExtensionProcessor extensionProcessor = ExtensionProcessor.of(resteasy);

        assertThat(extensionProcessor.getSyntheticMetadata())
                .containsEntry("status", List.of("stable"))
                .containsEntry("with", List.of("starter-code"))
                .containsEntry("origin", List.of("platform"));
        assertThat(extensionProcessor.getShortName()).contains("jax-rs");
        assertThat(extensionProcessor.getCategories()).contains("web");
        assertThat(extensionProcessor.getCodestartKind()).isEqualTo(ExtensionProcessor.CodestartKind.EXTENSION_CODESTART);
        assertThat(extensionProcessor.getCodestartName()).isEqualTo("resteasy");
        assertThat(extensionProcessor.getCodestartArtifact())
                .isEqualTo("io.quarkus:quarkus-project-core-extension-codestarts::jar:" + getQuarkusCoreVersion());
        assertThat(extensionProcessor.getCodestartLanguages()).contains("java", "kotlin", "scala");
        assertThat(extensionProcessor.getKeywords()).contains("resteasy", "jaxrs", "web", "rest");
        assertThat(extensionProcessor.getExtendedKeywords()).contains("resteasy", "jaxrs", "web", "rest");
        assertThat(extensionProcessor.getGuide()).isEqualTo("https://quarkus.io/guides/rest-json");
    }

    @Test
    void testGetBom() {
        final ExtensionCatalog catalog = getExtensionsCatalog();
        final Extension kotlin = findExtension(catalog, "quarkus-kotlin");
        assertThat(ExtensionProcessor.getBom(kotlin).get().getKey())
                .isEqualTo(ArtifactKey.fromString("io.quarkus:quarkus-bom::pom"));
    }

    @Test
    void testGetNonQuarkusBomOnly() {
        final ExtensionCatalog catalog = getExtensionsCatalog();
        final Extension kotlin = findExtension(catalog, "quarkus-kotlin");
        assertThat(ExtensionProcessor.getNonQuarkusBomOnly(kotlin)).isEmpty();
    }

    @Test
    void testKotlinMetadata() {
        final ExtensionCatalog catalog = getExtensionsCatalog();
        final Extension kotlin = findExtension(catalog, "quarkus-kotlin");
        final ExtensionProcessor extensionProcessor = ExtensionProcessor.of(kotlin);
        assertThat(extensionProcessor.getSyntheticMetadata())
                .containsEntry("status", List.of("preview"))
                .containsEntry("origin", List.of("platform"));
        assertThat(extensionProcessor.getShortName()).contains("");
        assertThat(extensionProcessor.getCategories()).contains("alt-languages");
        assertThat(extensionProcessor.getCodestartKind()).isEqualTo(ExtensionProcessor.CodestartKind.CORE);
        assertThat(extensionProcessor.getCodestartName()).isEqualTo("kotlin");
        assertThat(extensionProcessor.getCodestartLanguages()).isEmpty();
        assertThat(extensionProcessor.getCodestartArtifact())
                .isEqualTo("io.quarkus:quarkus-project-core-extension-codestarts::jar:" + getQuarkusCoreVersion());
        assertThat(extensionProcessor.getKeywords()).contains("kotlin");
        assertThat(extensionProcessor.getExtendedKeywords()).contains("kotlin", "quarkus-kotlin", "services", "write");
        assertThat(extensionProcessor.getGuide()).isEqualTo("https://quarkus.io/guides/kotlin");
    }

    @Test
    void testExtendedKeywords() {
        assertThat(ExtendedKeywords.extendsKeywords(
                "quarkus-something-reactive-cool",
                "RESTEasy Reactive test",
                "ola-reactive",
                List.of("web", "bar", "foo bar"),
                "reactive rest http service of the",
                List.of("foo", "bar"))).containsExactlyInAnyOrder(
                        "resteasy", "reactive", "test", "http", "ola", "web", "rest", "bar", "foo",
                        "cool", "quarkus-something-reactive-cool", "ola-reactive", "foo-bar", "something", "service");
    }

    private Extension findExtension(ExtensionCatalog catalog, String id) {
        final Optional<Extension> first = catalog.getExtensions().stream()
                .filter(e -> e.getArtifact().getArtifactId().equals(id)).findFirst();
        assertThat(first).isPresent();
        return first.get();
    }

}
