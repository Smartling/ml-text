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

import com.smartling.ml.text.CsvFormat;
import java.util.stream.IntStream;
import libsvm.svm_node;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holds features of single observation.
 */
public class Features
{
    private static final CsvFormat csvFormat = new CsvFormat();

    private final double[] values;

    public static Features empty(int size)
    {
        return new Features(new double[size]);
    }

    public Features(double[] values)
    {
        this.values = values;
    }

    public int size()
    {
        return values.length;
    }

    public double get(int index)
    {
        return values[index];
    }

    public String toCSV()
    {
        return csvFormat.format(values);
    }

    public Object toSVM()
    {
        return IntStream
                .range(0, values.length)
                .mapToObj(i -> {
                            svm_node svm_node = new svm_node();
                            svm_node.index = i + 1;     // libsvm is 1-based
                            svm_node.value = values[i];
                            return svm_node;
                        }
                )
                .toArray(svm_node[]::new);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        final Features features = (Features)o;

        return new EqualsBuilder()
                .append(values, features.values)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(values)
                .toHashCode();
    }

    public double[] toArray()
    {
        return values.clone();
    }
}
