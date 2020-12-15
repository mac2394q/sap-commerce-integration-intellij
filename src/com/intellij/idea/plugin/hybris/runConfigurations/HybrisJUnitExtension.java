/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.runConfigurations;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.idea.plugin.hybris.project.utils.HybrisRootUtil;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettings;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettingsComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import static com.intellij.idea.plugin.hybris.common.HybrisConstants.HYBRIS_DATA_DIRECTORY;
import static com.intellij.idea.plugin.hybris.common.HybrisConstants.HYBRIS_DATA_DIR_ENV;

/**
 * @author Eugene.Kudelevsky
 */
public class HybrisJUnitExtension extends RunConfigurationExtension {

    @Override
    public <T extends RunConfigurationBase> void updateJavaParameters(
        final T configuration, final JavaParameters params, final RunnerSettings runnerSettings
    ) throws ExecutionException {
        if (runnerSettings != null || !isApplicableFor(configuration)) {
            return;
        }
        final Project project = configuration.getProject();
        final ParametersList vmParameters = params.getVMParametersList();

        if (!vmParameters.hasParameter("-ea")) {
            vmParameters.add("-ea");
        }
        if (vmParameters.getParameters().stream().noneMatch(param -> param.startsWith("-Dplatformhome="))) {
            final VirtualFile platformRootDirectory = HybrisRootUtil.findPlatformRootDirectory(project);

            if (platformRootDirectory != null) {
                vmParameters.add("-Dplatformhome=" + platformRootDirectory.getPath());
            }
        }
        if (!params.getEnv().containsKey(HYBRIS_DATA_DIR_ENV)) {
            final HybrisProjectSettings settings = HybrisProjectSettingsComponent.getInstance(project).getState();

            final String hybrisDataDirPath = FileUtil.toCanonicalPath(
                project.getBasePath() + '/' + settings.getHybrisDirectory() + '/' + HYBRIS_DATA_DIRECTORY);

            if (hybrisDataDirPath != null) {
                params.addEnv(HYBRIS_DATA_DIR_ENV, hybrisDataDirPath);
            }
        }
    }

    @Override
    public boolean isApplicableFor(@NotNull final RunConfigurationBase configuration) {
        if (!(configuration instanceof JUnitConfiguration)) {
            return false;
        }
        final Project project = configuration.getProject();
        final HybrisProjectSettings settings = HybrisProjectSettingsComponent.getInstance(project).getState();
        return settings.isHybrisProject();
    }

}
