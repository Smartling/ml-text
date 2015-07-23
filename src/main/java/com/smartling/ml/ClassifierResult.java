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
package com.smartling.ml;

/**
 * Holds results of classification.
 * <p>
 *     Labels are ordered by confidence, from high to low.
 */
public class ClassifierResult
{
    private final String[] labels;
    private final double[] confidence;

    public static ClassifierResult empty()
    {
        return new ClassifierResult(new String[0], new double[0]);
    }

    public ClassifierResult(String[] labels, double[] confidence)
    {
        this.labels = labels;
        this.confidence = confidence;
    }

    public String[] getLabels()
    {
        return labels;
    }

    public double[] getConfidence()
    {
        return confidence;
    }


}
