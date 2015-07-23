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
package com.smartling.ml.model;

import com.smartling.ml.features.Features;
import com.smartling.ml.SvmClassifier;

/**
 * Holds scaling parameters per feature.
 * Scaling helps in training better models.
 * When predicting, {@link SvmClassifier#classify(Features)} applies scaling to features.
 * See {@link ClassifierModel}
 */
public class ScaleParameters
{
    public double lower = -1.0;
    public double upper = 1.0;
    public double[] feature_max;
    public double[] feature_min;
}
