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

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TextKeywordStatsTest
{
    @Mock
    private TextSearchEngine searchEngine;

    @Mock
    private Map<String, Integer> searchResult;

    private TextKeywordStats keywordStats;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);

        when(searchResult.get(any())).thenReturn(0);
        when(searchEngine.search(any())).thenReturn(searchResult);

        withKeywords("ignore");
    }

    private void withKeywords(String keywordString)
    {
        keywordStats = new TextKeywordStats(split(keywordString), searchEngine);
    }

    @Test
    public void addsKeywordsToSearchEngine() throws IOException
    {
        withKeywords("some keyword");

        for (String keyword : new String[]{"some", "keyword"})
            verify(searchEngine).addKeyword(keyword);
    }

    @Test
    public void callsSearchEngineOnContentForAverage() throws IOException
    {
        keywordStats.extractAverage("some text");

        verify(searchEngine).search("some text");
    }

    @Test
    public void extractsAverageInDocument() throws IOException
    {
        String keywords = "{  }  <  >";
        int[] counts = randomInts(4);

        withKeywords(keywords);
        withSearchResults(keywords, counts);

        checkAveragePerKeyword(keywords, counts, "some text");
    }

    private void checkAveragePerKeyword(String keywordString, int[] counts, String content) throws IOException
    {
        RealVector features = keywordStats.extractAverage(content);

        String[] keywords = split(keywordString);
        int contentLength = content.length();

        assertThat(features.getDimension(), is(keywords.length));
        for (int i = 0; i < keywords.length; i++)
            assertThat(features.getEntry(i), is(counts[i] / (double)contentLength));
    }

    private int[] randomInts(int size)
    {
        return new Random().ints(size, 0, 10).toArray();
    }

    private void withSearchResults(final String keywordString, int... counts)
    {
        String[] keywords = split(keywordString);

        for (int i = 0; i < keywords.length; i++)
            when(searchResult.get(keywords[i])).thenReturn(counts[i]);
    }

    private static String[] split(final String value)
    {
        return Arrays
                .stream(value.split(" "))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    @Test
    public void callsSearchEngineForLineMedians() throws IOException
    {
        keywordStats.extractLineMedians("some text \n\n more\n text");

        verify(searchEngine).search("some text ");
        verify(searchEngine).search(" more");
        verify(searchEngine).search(" text");
    }

    @Test
    public void extractsLinesMedians() throws IOException
    {
        String keywords = "{  }  <  >";
        keywordStats = new TextKeywordStats(split(keywords));

        RealVector features = keywordStats.extractLineMedians("{{ <<< \n >> } \n >{< ><<");

        assertThat(features.toArray(), is(new double[]{1.0, 0.0, 3.0, 2.0}));
    }

    @Test
    public void skipsEmptyLines() throws IOException
    {
        String keywords = "{  }  <";
        keywordStats = new TextKeywordStats(split(keywords));

        RealVector features = keywordStats.extractLineMedians("{{ << \n <} \n\n {< <<\n");

        assertThat(features.toArray(), is(new double[]{1.0, 0.0, 2.0}));
    }

}
