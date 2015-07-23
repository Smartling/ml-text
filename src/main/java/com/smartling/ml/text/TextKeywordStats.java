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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Provides statistics of given keywords in a text content:
 * <ul>
 *  <li>count average over content length
 *  <li>median count per line
 * </ul>
 */
public class TextKeywordStats
{
    private final String[] keywords;

    private final TextSearchEngine searchEngine;

    public TextKeywordStats(final String[] keywords)
    {
        this(keywords, new TextSearchEngine());
    }

    public TextKeywordStats(final String[] keywords, final TextSearchEngine searchEngine)
    {
        this.keywords = keywords;
        this.searchEngine = searchEngine;

        for (String keyword : keywords)
            searchEngine.addKeyword(keyword);
    }

    public int keywordsCount()
    {
        return keywords.length;
    }

    public RealVector extractAverage(final String content)
    {
        RealVector counts = countKeywords(content);

        return counts.mapDivide(content.length());
    }

    private RealVector countKeywords(final String content)
    {
        Map<String, Integer> searchCount = searchEngine.search(content);

        Double[] keywordCounts = Arrays
                .stream(keywords)
                .map(searchCount::get)
                .map(x -> (double)x)
                .toArray(Double[]::new);

        return new ArrayRealVector(keywordCounts);
    }

    public RealVector extractLineMedians(final String content) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new StringReader(content)))
        {
            List<RealVector> list = new ArrayList<>();

            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                if (line.isEmpty())
                    continue;;
                RealVector counts = countKeywords(line);
                list.add(counts);
            }

            RealMatrix matrixCounts = toMatrix(list, keywords.length);
            return columnMedian(matrixCounts);
        }
    }

    private RealVector columnMedian(final RealMatrix matrix)
    {
        RealVector result = new ArrayRealVector(matrix.getColumnDimension());

        for (int i = 0, columns = matrix.getColumnDimension(); i < columns; i++)
        {
            double[] columnVector = matrix.getColumnVector(i).toArray();
            double median = StatUtils.percentile(columnVector, 50);
            result.setEntry(i, median);
        }

        return result;
    }

    private static RealMatrix toMatrix(final List<RealVector> rows, int columnDimension)
    {
        RealMatrix matrix = new Array2DRowRealMatrix(rows.size(), columnDimension);
        for (int row = 0; row < rows.size(); row++)
            matrix.setRow(row, rows.get(row).toArray());
        return matrix;
    }
}
