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

import com.smartling.ml.features.FeatureExtractor;
import com.smartling.ml.features.FeatureScaling;
import com.smartling.ml.features.Features;
import com.smartling.ml.model.ClassifierModel;
import com.smartling.ml.model.ClassifierModelLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.IntStream;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

/**
 * SVM multi-class classifier.
 * <p>
 *     Classifier operates using pre-trained model.
 *     Default model is taken from {@link ClassifierModel#defaultModel()}. Default model gets loaded from resource files,
 *     see {@link ClassifierModelLoader} for details.
 */
public class SvmClassifier
{
    private final ClassifierModel model;
    private final FeatureScaling featureScaling;

    public SvmClassifier()
    {
        this(ClassifierModel.defaultModel(), new FeatureScaling());
    }

    public SvmClassifier(ClassifierModel model, FeatureScaling featureScaling)
    {
        this.model = model;
        this.featureScaling = featureScaling;
    }

    public ClassifierResult classify(final Features features)
    {
        return classify(features, 0.);
    }

    /**
     * Runs classification on features.
     * @param features the features to classify, as returned from {@link FeatureExtractor#extract(String)}
     * @param confidenceThreshold [0..1] sets return value threshold; values below threshold will be discarded
     * @return label and confidence arrays, see {@link ClassifierResult}. The arrays might be empty,
     * if all values are below confidence threshold.
     */
    public ClassifierResult classify(final Features features, final double confidenceThreshold)
    {
        svm_node[] scaledFeatures = (svm_node[])featureScaling
                .rescale(features, model.getScaleParameters())
                .toSVM();

        final String[] labels = model.getLabels();
        final double[] confidence = new double[labels.length];

        svm_model svm_model = (svm_model)model.getSvmModel();

        svm.svm_predict_probability(svm_model, scaledFeatures, confidence);

        return filterResults(confidenceThreshold, labels, confidence);
    }

    private static ClassifierResult filterResults(final double confidenceThreshold, final String[] labels, final double[] confidence)
    {
        class r
        {
            String label;
            double value;

            r(String label, double value)
            {
                this.label = label;
                this.value = value;
            }
        }

        r[] rs = IntStream
                .range(0, confidence.length)
                .mapToObj(i -> new r(labels[i], confidence[i]))
                .filter(r -> r.value >= confidenceThreshold)
                .sorted(Collections.reverseOrder(Comparator.comparing(r -> r.value)))
                .toArray(r[]::new);

        String[] filteredLabels = Arrays.stream(rs).map(r -> r.label).toArray(String[]::new);
        double[] filteredConfidence = Arrays.stream(rs).mapToDouble(r -> r.value).toArray();

        return new ClassifierResult(filteredLabels, filteredConfidence);
    }
}
