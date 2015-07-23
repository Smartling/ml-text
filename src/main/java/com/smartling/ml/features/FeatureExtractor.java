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

import com.smartling.ml.text.ContentPreprocessor;
import com.smartling.ml.text.TextKeywordStats;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.RealVector;

/**
 * Extracts features of a given text content.
 * <p>
 *     Features are based on statistics of keywords appearing in the text.
 *     There are two features extracted per each keyword, see {@link TextKeywordStats}:
 *     <ul>
 *         <li>average keyword count in file
 *         <li>median of keyword counts in each line
 *     </ul>
 * <p>
 *     Note: use {@link ContentPreprocessor} before extracting features.
 * <p>
 *     Default keywords for extraction are loaded from {@link FeatureKeywords#defaultKeywords()},
 *     see {@link FeatureKeywords} for details.
 */
public class FeatureExtractor
{
    private final TextKeywordStats keywordStats;

    public FeatureExtractor()
    {
        this(FeatureKeywords.defaultKeywords());
    }

    public FeatureExtractor(final String[] keywords)
    {
        this(new TextKeywordStats(keywords));
    }

    public FeatureExtractor(final TextKeywordStats keywordStats)
    {
        this.keywordStats = keywordStats;
    }

    public Features extract(String content) throws IOException
    {
        if (StringUtils.isEmpty(content))
        {
            return Features.empty(keywordStats.keywordsCount() * 2);
        }

        RealVector average = keywordStats.extractAverage(content);
        RealVector medians = keywordStats.extractLineMedians(content);

        RealVector featureVector = average.append(medians);

        return new Features(featureVector.toArray());
    }
}
