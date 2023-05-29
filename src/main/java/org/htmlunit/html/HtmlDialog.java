/*
 * Copyright (c) 2002-2023 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.htmlunit.html;

import static org.htmlunit.BrowserVersionFeatures.CSS_DIALOG_NONE;

import java.util.Map;

import org.htmlunit.SgmlPage;

/**
 * Wrapper for the HTML element "dialog".
 *
 * @author Ahmed Ashour
 */
public class HtmlDialog extends HtmlElement {

    /** The HTML tag represented by this element. */
    public static final String TAG_NAME = "dialog";

    /**
     * Creates a new instance.
     *
     * @param qualifiedName the qualified name of the element type to instantiate
     * @param page the HtmlPage that contains this element
     * @param attributes the initial attributes
     */
    HtmlDialog(final String qualifiedName, final SgmlPage page,
            final Map<String, DomAttr> attributes) {
        super(qualifiedName, page, attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DisplayStyle getDefaultStyleDisplay() {
        if (hasFeature(CSS_DIALOG_NONE)) {
            return DisplayStyle.NONE;
        }
        return DisplayStyle.INLINE;
    }

    /**
     * Returns the {@code open} property.
     * @return the {@code open} property
     */
    public boolean isOpen() {
        return hasAttribute("open");
    }

    /**
     * Sets the open state.
     *
     * @param newValue the new value
     */
    public void setOpen(final boolean newValue) {
        if (newValue) {
            setAttribute("open", "");
        }
        else {
            removeAttribute("open");
        }
    }

    /**
     *  Displays the dialog modelessly.
     */
    public void show() {
        if (!isOpen()) {
            setOpen(true);
        }
    }

    /**
     *  Displays the dialog modal.
     */
    public void showModal() {
        if (!isOpen()) {
            setOpen(true);
        }
    }
}
