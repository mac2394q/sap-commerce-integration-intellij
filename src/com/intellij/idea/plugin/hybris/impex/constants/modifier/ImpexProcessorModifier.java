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

package com.intellij.idea.plugin.hybris.impex.constants.modifier;

import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettings;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettingsComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.search.GlobalSearchScope.allScope;

/**
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
public final class ImpexProcessorModifier {

    private ImpexProcessorModifier() {
    }

    public static ImpexProcessorModifierValue[] values() {
        final Project project = currentProject();
        final PsiClass clazz;
        if (project != null) {
            clazz = JavaPsiFacade.getInstance(project)
                                 .findClass(
                                     "de.hybris.platform.impex.jalo.imp.ImportProcessor",
                                     allScope(project)
                                 );

            if (clazz != null) {
                final Query<PsiClass> psiClasses = ClassInheritorsSearch.search(clazz, allScope(project), true);
                return psiClasses.findAll()
                                 .stream()
                                 .map(ImpexProcessorModifierValue::new)
                                 .toArray(ImpexProcessorModifierValue[]::new);
            }
        }
        return new ImpexProcessorModifierValue[]{};
    }

    private static Project currentProject() {

        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            final HybrisProjectSettings state = HybrisProjectSettingsComponent.getInstance(project).getState();
            if (!state.isHybrisProject()) {
                continue;
            }
            return project;
        }
        return null;
    }

    public static class ImpexProcessorModifierValue implements ImpexModifierValue {

        private PsiClass psiClass;

        public ImpexProcessorModifierValue(final PsiClass psiClass) {
            this.psiClass = psiClass;
        }

        public PsiClass getPsiClass() {
            return psiClass;
        }

        @NotNull
        @Override
        public String getModifierValue() {
            final String qualifiedName = psiClass.getQualifiedName();
            if (StringUtil.isNullOrEmpty(qualifiedName)) {
                return qualifiedName;
            }
            return StringUtils.EMPTY;
        }
    }
}
