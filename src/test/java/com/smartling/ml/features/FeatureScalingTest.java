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
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FeatureScalingTest
{
    private ScaleParameters params = new ScaleParameters();

    private FeatureScaling scaler;

    @Before
    public void setUp()
    {
        scaler = new FeatureScaling();

        withScaleBounds(-1, 1);
        withFeatureMin(0, 0, 0, 0);
        withFeatureMax(1, 1, 1, 1);
    }

    private void withScaleBounds(double lower, double upper)
    {
        params.lower = lower;
        params.upper = upper;
    }

    private void withFeatureMin(double... mins)
    {
        params.feature_min = prepend(0, mins);
    }

    private void withFeatureMax(double... maxes)
    {
        params.feature_max = prepend(0, maxes);
    }

    private static double[] prepend(double element, final double[] values)
    {
        return ArrayUtils.add(values, 0, element);
    }

    @Test
    public void scalesFeatures()
    {
        withScaleBounds(0, 10);
        withFeatureMin(0, 0);
        withFeatureMax(1, 1000);

        assertThat(scaled(0.4, 42), is(new double[] { 4, 0.42 } ));
    }

    @Test
    public void scalesEmptyList()
    {
        assertThat(scaled(), is(new double[0]));
    }

    private double[] scaled(double... features)
    {
        return scaler
                .rescale(new Features(features), params)
                .toArray();
    }

    @Test
    public void preservesSingletonValue()
    {
        withFeatureMin(10);
        withFeatureMax(10);

        assertThat(scaled(4.2), is(new double[] { 4.2 } ));
    }

    @Test
    public void scalesOnLowerBound()
    {
        withFeatureMin(0.33);

        assertThat(scaled(0.33), is(new double[] { -1 } ));
    }

    @Test
    public void scalesOnUpperBound()
    {
        withFeatureMax(1.33);

        assertThat(scaled(1.33), is(new double[] { 1 } ));
    }
}
