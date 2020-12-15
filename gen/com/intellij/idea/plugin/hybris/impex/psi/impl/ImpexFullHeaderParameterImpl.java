// This is a generated file. Not intended for manual editing.
package com.intellij.idea.plugin.hybris.impex.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.intellij.idea.plugin.hybris.impex.psi.ImpexTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.idea.plugin.hybris.impex.psi.*;

public class ImpexFullHeaderParameterImpl extends ASTWrapperPsiElement implements ImpexFullHeaderParameter {

  public ImpexFullHeaderParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull ImpexVisitor visitor) {
    visitor.visitFullHeaderParameter(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ImpexVisitor) accept((ImpexVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ImpexAnyHeaderParameterName getAnyHeaderParameterName() {
    return findNotNullChildByClass(ImpexAnyHeaderParameterName.class);
  }

  @Override
  @NotNull
  public List<ImpexModifiers> getModifiersList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ImpexModifiers.class);
  }

  @Override
  @NotNull
  public List<ImpexParameters> getParametersList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, ImpexParameters.class);
  }

}
