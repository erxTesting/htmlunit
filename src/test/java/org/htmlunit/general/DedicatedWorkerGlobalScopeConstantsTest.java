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
package org.htmlunit.general;

import static org.htmlunit.BrowserVersionFeatures.JS_ERROR_STACK_TRACE_LIMIT;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.htmlunit.TestCaseTest;
import org.htmlunit.WebDriverTestCase;
import org.htmlunit.javascript.configuration.ClassConfiguration;
import org.htmlunit.javascript.configuration.ClassConfiguration.ConstantInfo;
import org.htmlunit.javascript.configuration.WorkerJavaScriptConfiguration;
import org.htmlunit.junit.BrowserParameterizedRunner;
import org.htmlunit.junit.BrowserParameterizedRunner.Default;
import org.htmlunit.junit.BrowserRunner.Alerts;
import org.htmlunit.util.MimeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests that constants class are correct.
 *
 * @author Ronald Brill
 */
@RunWith(BrowserParameterizedRunner.class)
public class DedicatedWorkerGlobalScopeConstantsTest extends WebDriverTestCase {

    /**
     * Returns the parameterized data.
     * @return the parameterized data
     * @throws Exception if an error occurs
     */
    @Parameters
    public static Collection<Object[]> data() throws Exception {
        final List<Object[]> list = new ArrayList<>();
        final Set<String> strings = TestCaseTest.getAllClassNames();
        for (final String host : strings) {
            if (!"Audio".equals(host)) {
                list.add(new Object[] {host});
            }
        }
        return list;
    }

    /**
     * The parent element name.
     */
    @Parameter
    public String host_;

    /**
     * The default test.
     * @throws Exception if an error occurs
     */
    @Test
    @Default
    public void test() throws Exception {
        test(host_, getExpectedString(host_));
    }

    private void test(final String className, final String[] expectedAlerts) throws Exception {
        final String html = "<html><body>"
                + "<script>\n"
                + LOG_TITLE_FUNCTION
                + "try {\n"
                + "  var myWorker = new Worker('worker.js');\n"
                + "  myWorker.onmessage = function(e) {\n"
                + "    window.document.title = '' + e.data;\n"
                + "  };\n"
                + "  setTimeout(function() { myWorker.postMessage('test');}, 10);\n"
                + "} catch(e) { window.document.title = 'exception'; }\n"
                + "</script></body></html>\n";

        final String workerJs = "onmessage = function(e) {\n"
                + "  var workerResult = '';\n"
                + "  try {\n"
                + "    var all = [];\n"
                + "    for (var x in " + className + ") {\n"
                + "      if (typeof " + className + "[x] == 'number') {\n"
                + "        all.push(x);\n"
                + "      }\n"
                + "    }\n"
                + "    all.sort();\n"

                + "    for (var i in all) {\n"
                + "      var x = all[i];\n"
                + "      workerResult += x + ':' + " + className + "[x] + '\\u00a7';\n"
                + "    }\n"
                + "  } catch(e) {}\n"
                + "  postMessage(workerResult);\n"
                + "}\n";

        getMockWebConnection().setResponse(new URL(URL_FIRST, "worker.js"), workerJs, MimeType.TEXT_JAVASCRIPT);

        loadPage2(html);
        verifyTitle2(DEFAULT_WAIT_TIME / 4, getWebDriver(), expectedAlerts);
    }

    private String[] getExpectedString(final String className) {
        if (getExpectedAlerts() != null && getExpectedAlerts().length > 0) {
            return getExpectedAlerts();
        }

        if (className.endsWith("Array") || "Image".equals(className) || "Option".equals(className)) {
            return new String[0];
        }
        if ("Error".equals(className) && getBrowserVersion().hasFeature(JS_ERROR_STACK_TRACE_LIMIT)) {
            return new String[] {"stackTraceLimit:10"};
        }

        final WorkerJavaScriptConfiguration javaScriptConfig
                = WorkerJavaScriptConfiguration.getInstance(getBrowserVersion());
        final List<String> constants = new ArrayList<>();
        ClassConfiguration classConfig = javaScriptConfig.getClassConfiguration(className);

        boolean first = true;
        while (classConfig != null) {
            if (first && !classConfig.isJsObject()) {
                break;
            }
            if (first || classConfig.getJsConstructor() != null) {
                final List<ConstantInfo> constantInfos = classConfig.getConstants();
                if (constantInfos != null) {
                    for (final ConstantInfo constantInfo : constantInfos) {
                        constants.add(constantInfo.getName() + ":" + constantInfo.getValue());
                    }
                }
            }
            classConfig = javaScriptConfig.getClassConfiguration(classConfig.getExtendedClassName());
            first = false;
        }

        Collections.sort(constants, new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.substring(0, o1.indexOf(':')).compareTo(o2.substring(0, o2.indexOf(':')));
            }
        });
        return constants.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWebClientCached() {
        return true;
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(CHROME = {"AT_TARGET:2", "BUBBLING_PHASE:3", "CAPTURING_PHASE:1", "NONE:0"},
            EDGE = {"AT_TARGET:2", "BUBBLING_PHASE:3", "CAPTURING_PHASE:1", "NONE:0"},
            FF = "-",
            FF_ESR = "-")
    public void _SecurityPolicyViolationEvent() throws Exception {
        final String[] expected = getExpectedAlerts();
        if (expected.length == 1 && "-".equals(expected[0])) {
            test("SecurityPolicyViolationEvent", new String[0]);
            return;
        }
        test("SecurityPolicyViolationEvent", expected);
    }
}
