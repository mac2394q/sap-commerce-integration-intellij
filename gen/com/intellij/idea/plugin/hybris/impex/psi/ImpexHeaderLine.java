// This is a generated file. Not intended for manual editing.
package com.intellij.idea.plugin.hybris.impex.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ImpexHeaderLine extends PsiElement {

  @NotNull
  ImpexAnyHeaderMode getAnyHeaderMode();

  @NotNull
  List<ImpexFullHeaderParameter> getFullHeaderParameterList();

  @Nullable
  ImpexFullHeaderType getFullHeaderType();

}
