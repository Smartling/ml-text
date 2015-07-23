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

import com.smartling.ml.model.ScaleParameters;
import java.util.stream.IntStream;

/**
 * Applies scaling from {@link ScaleParameters} to features.
 */
public class FeatureScaling
{
    public Features rescale(final Features features, final ScaleParameters params)
    {
        double[] doubles = features.toArray();

        double[] scaled = IntStream
                .range(0, doubles.length)
                .mapToDouble(i -> rescale(
                                doubles[i],
                                params.lower,
                                params.upper,
                                params.feature_max[i + 1],
                                params.feature_min[i + 1]
                        )
                )
                .toArray();

        return new Features(scaled);
    }

    private static double rescale(double value, double lower, double upper, double max, double min)
    {
        if (max == min)
            return value;

        if (value == min)
            value = lower;
        else if (value == max)
            value = upper;
        else
            value = lower + (upper - lower) *
                    (value - min) /
                    (max - min);

        return value;
    }
}
