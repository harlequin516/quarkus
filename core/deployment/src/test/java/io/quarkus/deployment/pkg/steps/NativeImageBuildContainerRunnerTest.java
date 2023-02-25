package io.quarkus.deployment.pkg.steps;

import static io.quarkus.deployment.pkg.steps.NativeImageBuildLocalContainerRunner.DOCKER_EXECUTABLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;

import io.quarkus.deployment.pkg.NativeConfig;

class NativeImageBuildContainerRunnerTest {

    // This will default to false in the maven build and true in the IDE, so this will still run if invoked explicitly
    @DisabledIfSystemProperty(named = "avoid-containers", matches = "true")
    @Test
    void testBuilderImageBeingPickedUp() {
        NativeConfig nativeConfig = new NativeConfig();
        nativeConfig.containerRuntime = Optional.empty();
        boolean found;
        NativeImageBuildLocalContainerRunner localRunner;
        String[] command;

        nativeConfig.builderImage = "graalvm";
        localRunner = new NativeImageBuildLocalContainerRunner(nativeConfig, Path.of("/tmp"));
        command = localRunner.buildCommand(DOCKER_EXECUTABLE, Collections.emptyList(), Collections.emptyList());
        found = false;
        for (String part : command) {
            if (part.contains("ubi-quarkus-graalvmce-builder-image")) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();

        nativeConfig.builderImage = "mandrel";
        localRunner = new NativeImageBuildLocalContainerRunner(nativeConfig, Path.of("/tmp"));
        command = localRunner.buildCommand(DOCKER_EXECUTABLE, Collections.emptyList(), Collections.emptyList());
        found = false;
        for (String part : command) {
            if (part.contains("ubi-quarkus-mandrel-builder-image")) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();

        nativeConfig.builderImage = "RandomString";
        localRunner = new NativeImageBuildLocalContainerRunner(nativeConfig, Path.of("/tmp"));
        command = localRunner.buildCommand(DOCKER_EXECUTABLE, Collections.emptyList(), Collections.emptyList());
        found = false;
        for (String part : command) {
            if (part.equals("RandomString")) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }
}
