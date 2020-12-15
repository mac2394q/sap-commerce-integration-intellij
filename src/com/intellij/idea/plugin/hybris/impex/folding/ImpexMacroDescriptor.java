package com.intellij.idea.plugin.hybris.impex.folding;

import com.intellij.psi.PsiElement;

public class ImpexMacroDescriptor {
    private String macroName;
    private String resolvedValue;
    private PsiElement psiElement;

    public ImpexMacroDescriptor(final String macroName, final String resolvedValue, final PsiElement psiElement) {
        this.psiElement = psiElement;
        this.macroName = macroName;
        this.resolvedValue = resolvedValue;
        replaceBlank();
    }

    private void replaceBlank() {
        if (resolvedValue == null || resolvedValue.isEmpty()) {
            resolvedValue = "<blank>";
        }
    }

    public String getMacroName() {
        return macroName;
    }

    public String getResolvedValue() {
        return resolvedValue;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }
}
