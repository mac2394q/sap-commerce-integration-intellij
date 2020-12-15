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

package com.intellij.idea.plugin.hybris.impex.assistance;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInsight.highlighting.HighlightManagerImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractImpexHighlighterService implements ImpexHighlighterService {

    /**
     * IIPS-174: It seems like sometimes when we highlight code inside "Code Preview Panel" in combination with OOTB
     * "unchanged lines" folding from IDEA it can end up in creating invalid highlighting ranges.
     * E.g. when you run an inspection for an impex file and in the results panel click on an inspection result
     * on the right it shows you a preview of a snippet from that file, and if you have multiple inspection warnings in
     * the same file when you click on them the preview panel jumps into different parts of the file, which leads to
     * creation of multiple highlight ranges while the editor stays the same, but OOTB folding messes everything up by
     * folding many lines as the result highlight ranges created for the first inspection have invalid start and end
     * offsets for the editor with folded lines when you click on some other inspection from the same file.
     */
    protected void removeInvalidRangeHighlighters(@NotNull final Editor editor) {
        final HighlightManager highlightManager = HighlightManager.getInstance(
            Objects.requireNonNull(editor.getProject())
        );

        final RangeHighlighter[] highlighters = ((HighlightManagerImpl) highlightManager).getHighlighters(editor);

        Arrays.stream(highlighters)
              .filter(this::isNotProperRangeHighlighter)
              .forEach(highlighter -> highlightManager.removeSegmentHighlighter(editor, highlighter));
    }

    /**
     * From {@link TextRange#isProperRange(int, int) TextRange#isProperRange(int, int))
     */
    protected boolean isNotProperRangeHighlighter(@NotNull RangeHighlighter rangeHighlighter) {
        return rangeHighlighter.getStartOffset() > rangeHighlighter.getEndOffset() || rangeHighlighter.getStartOffset() < 0;
    }
}
