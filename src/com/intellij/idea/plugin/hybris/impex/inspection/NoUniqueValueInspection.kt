package com.intellij.idea.plugin.hybris.impex.inspection

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.idea.plugin.hybris.impex.inspection.analyzer.createDataTable
import com.intellij.idea.plugin.hybris.impex.inspection.analyzer.fullParametersList
import com.intellij.idea.plugin.hybris.impex.inspection.analyzer.keyAttrPredicate
import com.intellij.idea.plugin.hybris.impex.inspection.analyzer.keyAttributesList
import com.intellij.idea.plugin.hybris.impex.inspection.analyzer.keyAttrsName
import com.intellij.idea.plugin.hybris.impex.inspection.analyzer.notKeyAttributesList
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderLine
import com.intellij.idea.plugin.hybris.impex.utils.ImpexPsiUtils
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil


class NoUniqueValueInspection : LocalInspectionTool() {
    override fun getDefaultLevel(): HighlightDisplayLevel {
        return HighlightDisplayLevel.WARNING
    }
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor = NoUniqueValueVisitor(holder)
}

private class NoUniqueValueVisitor(private val problemsHolder: ProblemsHolder) : PsiElementVisitor() {

    override fun visitFile(file: PsiFile) {
        val headers = PsiTreeUtil.getChildrenOfType(file, ImpexHeaderLine::class.java) ?: return

        val groupHeaders = headers.filter { it.fullHeaderType != null }
                .groupBy { createHeaderKey(it) }

        groupHeaders.forEach { _, headerLines ->
            val fullParametersList = fullParametersList(headerLines)
            val keyAttrsList = keyAttributesList(fullParametersList)

            if (keyAttrsList.isNotEmpty()) {
                val notKeyAttrsList = notKeyAttributesList(fullParametersList)
                val distinctCommonAttrsNames = notKeyAttrsList.map { it.text }.distinct()

                val keyAttrsGroupedByName = fullParametersList.filter { keyAttrPredicate(it) }.groupBy { it.anyHeaderParameterName.text }

                val dataMap = mutableMapOf<String, List<PsiElement>>()
                keyAttrsGroupedByName.forEach { name, attrs -> dataMap[name] = attrs.flatMap { ImpexPsiUtils.getColumnForHeader(it).map { it.lastChild } } }

                if (distinctCommonAttrsNames.isEmpty()) {
                    val attrsNames = fullParametersList.filter { keyAttrPredicate(it) }.map { it.text }.distinct()
                    createDataTable(dataMap, attrsNames, keyAttrsList).analyze(problemsHolder)
                } else {
                    createDataTable(dataMap, distinctCommonAttrsNames, notKeyAttrsList).analyze(problemsHolder)
                }
            }
        }
    }

    private fun createHeaderKey(it: ImpexHeaderLine) =
            "${it.fullHeaderType?.text?.cleanWhitespaces()}|${keyAttrsName(it).joinToString { attr -> attr.cleanWhitespaces() }}"
}

private fun String.cleanWhitespaces() = this.replace(" ", "")