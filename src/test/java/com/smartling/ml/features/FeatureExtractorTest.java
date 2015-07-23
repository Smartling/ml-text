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
package com.smartling.ml.features;

import com.smartling.ml.text.TextKeywordStats;
import java.io.IOException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FeatureExtractorTest
{
    @Mock
    private TextKeywordStats keywordStats;

    @InjectMocks
    private FeatureExtractor extractor;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);

        when(keywordStats.extractAverage(any())).thenReturn(new ArrayRealVector());
        when(keywordStats.extractLineMedians(any())).thenReturn(new ArrayRealVector());
    }

    @Test
    public void extractsZeroesFromEmpty() throws IOException
    {
        when(keywordStats.keywordsCount()).thenReturn(3);

        Features features = extractor.extract("");

        assertThat(features.size(), is(3 * 2));

        for (int i = 0, num = features.size(); i < num; i++)
            assertThat(features.get(i), is(.0));
    }

    @Test
    public void extractsAverage() throws IOException
    {
        extractor.extract("some text");

        verify(keywordStats).extractAverage("some text");
    }

    @Test
    public void extractsMeans() throws IOException
    {
        extractor.extract("some text");

        verify(keywordStats).extractLineMedians("some text");
    }

    @Test
    public void combinesCounters() throws IOException
    {
        RealVector stats1 = new ArrayRealVector(new double[]{3, 5, 2, 4});
        RealVector stats2 = new ArrayRealVector(new double[]{7, 1, 3, 9});

        when(keywordStats.extractAverage(any())).thenReturn(stats1);
        when(keywordStats.extractLineMedians(any())).thenReturn(stats2);

        Features features = extractor.extract("ignore");

        assertThat(features.toArray(), is(new double[]{3, 5, 2, 4, 7, 1, 3, 9}));
    }
}
