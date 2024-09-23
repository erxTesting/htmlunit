/*
 * Copyright (c) 2002-2024 Gargoyle Software Inc.
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
package org.htmlunit;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.SerializationUtils;
import org.htmlunit.junit.BrowserRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link WebClientOptions}.
 *
 * @author Ronald Brill
 */
@RunWith(BrowserRunner.class)
public class WebClientOptionsTest extends SimpleWebTestCase {

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void serialization() throws Exception {
        final WebClientOptions original = new WebClientOptions();

        final byte[] bytes = SerializationUtils.serialize(original);
        final WebClientOptions deserialized = (WebClientOptions) SerializationUtils.deserialize(bytes);

        assertEquals(original.isJavaScriptEnabled(), deserialized.isJavaScriptEnabled());
        assertEquals(original.isCssEnabled(), deserialized.isCssEnabled());

        assertEquals(original.isPrintContentOnFailingStatusCode(), deserialized.isPrintContentOnFailingStatusCode());
        assertEquals(original.isThrowExceptionOnFailingStatusCode(),
                        deserialized.isThrowExceptionOnFailingStatusCode());
        assertEquals(original.isThrowExceptionOnScriptError(), deserialized.isThrowExceptionOnScriptError());
        assertEquals(original.isPopupBlockerEnabled(), deserialized.isPopupBlockerEnabled());
        assertEquals(original.isRedirectEnabled(), deserialized.isRedirectEnabled());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void serializationChanged() throws Exception {
        final WebClientOptions original = new WebClientOptions();
        original.setJavaScriptEnabled(false);
        original.setCssEnabled(false);

        original.setPrintContentOnFailingStatusCode(false);
        original.setThrowExceptionOnFailingStatusCode(false);
        original.setThrowExceptionOnScriptError(false);
        original.setPopupBlockerEnabled(true);
        original.setRedirectEnabled(false);

        final byte[] bytes = SerializationUtils.serialize(original);
        final WebClientOptions deserialized = (WebClientOptions) SerializationUtils.deserialize(bytes);

        assertEquals(original.isJavaScriptEnabled(), deserialized.isJavaScriptEnabled());
        assertEquals(original.isCssEnabled(), deserialized.isCssEnabled());

        assertEquals(original.isPrintContentOnFailingStatusCode(), deserialized.isPrintContentOnFailingStatusCode());
        assertEquals(original.isThrowExceptionOnFailingStatusCode(),
                        deserialized.isThrowExceptionOnFailingStatusCode());
        assertEquals(original.isThrowExceptionOnScriptError(), deserialized.isThrowExceptionOnScriptError());
        assertEquals(original.isPopupBlockerEnabled(), deserialized.isPopupBlockerEnabled());
        assertEquals(original.isRedirectEnabled(), deserialized.isRedirectEnabled());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void serializationSSLContextProvider() throws Exception {
        final WebClientOptions original = new WebClientOptions();
        original.setSSLContext(SSLContext.getDefault());

        final byte[] bytes = SerializationUtils.serialize(original);
        final WebClientOptions deserialized = (WebClientOptions) SerializationUtils.deserialize(bytes);

        assertNull(deserialized.getSSLContext());
    }
}
