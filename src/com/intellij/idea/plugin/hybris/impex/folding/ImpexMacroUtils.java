package com.intellij.idea.plugin.hybris.impex.folding;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;

import java.util.HashMap;
import java.util.Map;

public class ImpexMacroUtils {

    private static final Key<CachedValue<Map<String, ImpexMacroDescriptor>>> FILE_IMPEX_FOLDING_CACHE_KEY = Key.create("FILE_IMPEX_FOLDING_CACHE");

    public static CachedValue<Map<String, ImpexMacroDescriptor>> getFileCache(PsiFile impexFile) {
        CachedValue<Map<String, ImpexMacroDescriptor>> fileModelCache = impexFile.getUserData(FILE_IMPEX_FOLDING_CACHE_KEY);

        if (fileModelCache == null) {
            fileModelCache = CachedValuesManager.getManager(impexFile.getProject()).createCachedValue(
                () -> ApplicationManager.getApplication().runReadAction(
                    (Computable<CachedValueProvider.Result<Map<String, ImpexMacroDescriptor>>>) () ->
                        CachedValueProvider.Result.create(new HashMap<>(), impexFile)
                ), false);
            impexFile.putUserData(FILE_IMPEX_FOLDING_CACHE_KEY, fileModelCache);
        }

        return fileModelCache;
    }

}
