// This is a generated file. Not intended for manual editing.
package com.intellij.idea.plugin.hybris.impex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ImpexFullHeaderParameter extends PsiElement {

  @NotNull
  ImpexAnyHeaderParameterName getAnyHeaderParameterName();

  @NotNull
  List<ImpexModifiers> getModifiersList();

  @NotNull
  List<ImpexParameters> getParametersList();

}
