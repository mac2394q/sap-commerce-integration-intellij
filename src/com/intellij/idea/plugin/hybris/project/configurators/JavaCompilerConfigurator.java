package com.intellij.idea.plugin.hybris.project.configurators;

import com.intellij.idea.plugin.hybris.project.descriptors.HybrisProjectDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface JavaCompilerConfigurator {

    void configure(
        @NotNull final HybrisProjectDescriptor descriptor,
        @NotNull final Project project,
        @NotNull final HybrisConfiguratorCache cache
    );
}
