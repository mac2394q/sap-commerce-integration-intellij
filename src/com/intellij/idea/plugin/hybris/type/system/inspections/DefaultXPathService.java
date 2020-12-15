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

package com.intellij.idea.plugin.hybris.type.system.inspections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created 5:15 PM 18 September 2016.
 *
 * @author Alexander Bartash <AlexanderBartash@gmail.com>
 */
public class DefaultXPathService implements XPathService {


    private final XPath myXPath = XPathFactory.newInstance().newXPath();

    @Override
    @NotNull
    public NodeList computeNodeSet(@Nullable final String xpath, @NotNull final Object start)
    throws XPathExpressionException {
        if (xpath == null) {
            return EMPTY_NODE_LIST;
        }

        return (NodeList) this.myXPath.evaluate(xpath, start, XPathConstants.NODESET);
    }

    @Override
    public boolean computeBoolean(@Nullable final String xpath, @NotNull final Object start)
    throws XPathExpressionException {
        if (xpath == null) {
            return false;
        }

        final Object result = this.myXPath.evaluate(xpath, start, XPathConstants.BOOLEAN);
        return Boolean.TRUE.equals(result);
    }

    private static final NodeList EMPTY_NODE_LIST = new NodeList() {

        @Override
        public Node item(final int index) {
            return null;
        }

        @Override
        public int getLength() {
            return 0;
        }
    };
}
