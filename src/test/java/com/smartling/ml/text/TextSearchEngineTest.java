/*
 *  Copyright 2015 Smartling, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this work except in compliance with the License.
 *  You may obtain a copy of the License in the LICENSE file, or at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.smartling.ml.text;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TextSearchEngineTest
{
    private TextSearchEngine searchEngine;

    @Before
    public void setUp()
    {
        searchEngine = new TextSearchEngine();
    }

    @Test
    public void returnsCountOfMatches()
    {
        searchEngine.addKeyword("some 1");
        searchEngine.addKeyword("some 2");

        Map<String, Integer> result = searchEngine.search("abcd some 1 efgh some 2 ijkl");

        assertThat(result.get("some 1"), is(1));
        assertThat(result.get("some 2"), is(1));
    }

    @Test
    public void returnsZeroes()
    {
        searchEngine.addKeyword("some 1");
        searchEngine.addKeyword("some 2");

        Map<String, Integer> result = searchEngine.search("some text");

        assertThat(result.get("some 1"), is(0));
        assertThat(result.get("some 2"), is(0));
    }
}
