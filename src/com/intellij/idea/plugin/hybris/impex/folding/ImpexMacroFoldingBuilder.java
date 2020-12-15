package com.intellij.idea.plugin.hybris.impex.folding;

import com.intellij.idea.plugin.hybris.impex.psi.ImpexFile;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderLine;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexMacroDeclaration;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexMacroNameDec;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexMacroUsageDec;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexString;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexTypes;
import com.intellij.idea.plugin.hybris.impex.psi.ImpexVisitor;
import com.intellij.idea.plugin.hybris.impex.utils.ProjectPropertiesUtils;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.lang.properties.IProperty;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.SmartList;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfAnyType;

public class ImpexMacroFoldingBuilder implements FoldingBuilder {

    private static final Logger LOG = Logger.getInstance(ImpexMacroFoldingBuilder.class);
    public static final String CONFIG_PREFIX = "$config-";

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(
        @NotNull final ASTNode node, @NotNull final Document document
    ) {
        if (!(node.getPsi() instanceof ImpexFile)) {
            return FoldingDescriptor.EMPTY;
        }
        ImpexFile root = (ImpexFile) node.getPsi();

        SmartList<FoldingDescriptor> results = new SmartList<>();
        SmartList<ImpexMacroUsageDec> localMacroList = new SmartList<>();

        // resolve $config- first
        findChildrenOfAnyType(root, ImpexMacroUsageDec.class).forEach(macroUsage->resolveMacroUsage(macroUsage, results, localMacroList));

        // then resolve other macros
        root.acceptChildren(new ImpexVisitor(){
            public void visitMacroDeclaration(@NotNull ImpexMacroDeclaration macroDeclaration) {
                super.visitMacroDeclaration(macroDeclaration);
                resolveMacroDeclaration(macroDeclaration, results);
            }

            @Override
            public void visitString(@NotNull final ImpexString o) {
                super.visitString(o);
                resolveIncludeExternalData(o);
            }
        });

        // resolve local macro last
        localMacroList.forEach(macroUsage->resolveLocalMacro(macroUsage, results));

        return results.toArray(FoldingDescriptor.EMPTY);
    }

    private void resolveIncludeExternalData(final ImpexString impexString) {
        String text = impexString.getText();
        int index = text.indexOf("impex.includeExternalData");
        if (index == -1) {
            return;
        }
        index = text.indexOf("getResourceAsStream");
        if (index == -1) {
            return;
        }
        int startIndex = text.indexOf("(", index);
        if (startIndex == -1) {
            return;
        }
        int endIndex = text.indexOf(")", startIndex);
        if (endIndex == -1) {
            return;
        }
        String resource = text.substring(startIndex+1, endIndex);
        resource = StringUtils.strip(resource, "\"' ");
        VirtualFile directory = impexString.getContainingFile().getContainingDirectory().getVirtualFile();
        String dirPath = directory.getCanonicalPath();
        VirtualFile referencedFile = LocalFileSystem.getInstance().findFileByIoFile(new File(dirPath, resource));
        if (referencedFile == null || !referencedFile.exists()) {
            return;
        }
        PsiFile referencedPsi = PsiManager.getInstance(impexString.getProject()).findFile(referencedFile);
        if (!(referencedPsi instanceof ImpexFile)) {
            return;
        }
        ;
        Map<String, ImpexMacroDescriptor> referencedCache = ImpexMacroUtils.getFileCache(referencedPsi).getValue();
        if (referencedCache.isEmpty()) {
            Document document = FileDocumentManager.getInstance().getDocument(referencedFile);
            if (document == null) {
                return;
            }
            preventRecursion(impexString);
            buildFoldRegions(referencedPsi.getNode(), document);
            referencedCache = ImpexMacroUtils.getFileCache(referencedPsi).getValue();
        }
        Map<String, ImpexMacroDescriptor> cache = ImpexMacroUtils.getFileCache(impexString.getContainingFile()).getValue();
        cache.putAll(referencedCache);
    }

    private void preventRecursion(final ImpexString impexString) {
        Map<String, ImpexMacroDescriptor> cache = ImpexMacroUtils.getFileCache(impexString.getContainingFile()).getValue();
        if (cache.isEmpty()) {
            cache.put("!", new ImpexMacroDescriptor("!", "!", impexString));
        }
    }

    private void resolveMacroDeclaration(
        ImpexMacroDeclaration macroLine,
        final SmartList<FoldingDescriptor> results
    ) {
        List<PsiElement> lineElements = getAllChildren(macroLine);
        String macroName = null;
        StringBuilder sb = new StringBuilder();
        Map<String, ImpexMacroDescriptor> cache = ImpexMacroUtils.getFileCache(macroLine.getContainingFile()).getValue();
        PsiElement anchor = macroLine;
        for (PsiElement child: lineElements) {
            if (child instanceof LeafPsiElement) {
                LeafPsiElement leafPsiElement = (LeafPsiElement) child;
                if (leafPsiElement.getElementType() == ImpexTypes.ASSIGN_VALUE) {
                    continue;
                }
            }
            if (child instanceof ImpexMacroNameDec) {
                macroName = child.getText();
                anchor = child;
            } else {
                if (child instanceof ImpexMacroUsageDec) {
                    ImpexMacroDescriptor descriptor = findInCache(cache, child.getText());
                    if (descriptor != null) {
                        sb.append(descriptor.getResolvedValue());
                        int delta = child.getText().length() - descriptor.getMacroName().length();
                        if (delta > 0) {
                            sb.append(child.getText().substring(descriptor.getMacroName().length()));
                        }
                    } else {
                        sb.append(child.getText());
                    }
                } else {
                    sb.append(child.getText());
                }
            }
        }

        cache.put(macroName, new ImpexMacroDescriptor(macroName, sb.toString(), anchor));
    }

    private void resolveMacroUsage(
        final ImpexMacroUsageDec macroUsage,
        final SmartList<FoldingDescriptor> results,
        final SmartList<ImpexMacroUsageDec> localMacroList
    ) {
        String text = macroUsage.getText();
        if (text.startsWith(CONFIG_PREFIX)) {
            resolveProperty(macroUsage, results);
        } else {
            // local macro needs to be resolved later when evaluating macro declarations
            PsiElement parent = macroUsage.getParent();
            if (parent instanceof ImpexMacroUsageDec) {
                return;
            }
            if (getRootPsi(parent) instanceof ImpexHeaderLine) {
                return;
            }
            localMacroList.add(macroUsage);
        }
    }

    private PsiElement getRootPsi(final PsiElement psiElement) {
        PsiElement root = psiElement;
        while (root.getParent() != null) {
            if (root.getParent() instanceof ImpexFile) {
                return root;
            }
            root = root.getParent();
        }
        return root;
    }

    private void resolveProperty(final ImpexMacroUsageDec macroUsage, final SmartList<FoldingDescriptor> results) {
        String text = macroUsage.getText();
        if (text.length() <= CONFIG_PREFIX.length()) {
            return;
        }
        Map<String, ImpexMacroDescriptor> cache = ImpexMacroUtils.getFileCache(macroUsage.getContainingFile()).getValue();
        ImpexMacroDescriptor descriptor = cache.get(text);
        if (descriptor == null) {
            String propertyName = text.substring(CONFIG_PREFIX.length());
            Module module = ModuleUtil.findModuleForPsiElement(macroUsage);
            if (module == null) {
                return;
            }
            IProperty property = ProjectPropertiesUtils.INSTANCE.findMacroProperty(module, propertyName);
            if (property == null) {
                return;
            }
            String value = ProjectPropertiesUtils.INSTANCE.resolvePropertyValue(module, property.getValue());
            descriptor = new ImpexMacroDescriptor(CONFIG_PREFIX + property.getKey(), value, property.getPsiElement());
            cache.put(text, descriptor);
            cache.put(CONFIG_PREFIX + property.getKey(), descriptor);
        }
        int start = macroUsage.getTextRange().getStartOffset();
        TextRange range = new TextRange(start, start + descriptor.getMacroName().length());
        results.add(new FoldingDescriptor(macroUsage.getNode(), range, null));
    }

    private void resolveLocalMacro(final ImpexMacroUsageDec macroUsage, final SmartList<FoldingDescriptor> results) {
        Map<String, ImpexMacroDescriptor> cache = ImpexMacroUtils.getFileCache(macroUsage.getContainingFile()).getValue();
        String currentKey = "";
        for (String key: cache.keySet()) {
            if (macroUsage.getText().startsWith(key)) {
                if (key.length() > currentKey.length()) {
                    currentKey = key;
                }
            }
        }
        if (currentKey.isEmpty()) {
            return;
        }
        ImpexMacroDescriptor descriptor = cache.get(currentKey);
        int start = macroUsage.getTextRange().getStartOffset();
        TextRange range = new TextRange(start, start + descriptor.getMacroName().length());
        results.add(new FoldingDescriptor(macroUsage.getNode(), range, null));

        cache.put(macroUsage.getText(), descriptor);
    }

    private ImpexMacroDescriptor findInCache(
        final Map<String, ImpexMacroDescriptor> cache,
        final String text
    ) {
        ImpexMacroDescriptor impexMacroDescriptor = cache.get(text);
        if (impexMacroDescriptor != null) {
            return impexMacroDescriptor;
        }
        for (ImpexMacroDescriptor md: cache.values()) {
            if (text.startsWith(md.getMacroName())) {
                cache.put(text, md);
                return md;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull final ASTNode node) {
        Map<String, ImpexMacroDescriptor> cache = ImpexMacroUtils.getFileCache(node.getPsi().getContainingFile()).getValue();
        ImpexMacroDescriptor descriptor = cache.get(node.getText());
        if (descriptor != null) {
            return descriptor.getResolvedValue();
        }
        return node.getText();
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull final ASTNode node) {
        return true;
    }

    private List<PsiElement> getAllChildren(final ImpexMacroDeclaration psiElement) {
        List<PsiElement> result = new ArrayList<>();
        PsiElement psiChild = psiElement.getFirstChild();
        while (psiChild != null) {
            result.add(psiChild);
            psiChild = psiChild.getNextSibling();
        }
        return result;
    }

}
