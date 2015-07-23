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

import java.io.IOException;
import java.util.stream.IntStream;
import libsvm.svm_model;
import libsvm.svm_parameter;

/**
 * Holds parameters of a trained classifier model.
 * <p>
 *     See {@link ClassifierModelLoader}
 */
public class ClassifierModel
{
    private static ClassifierModel defaultModel;

    private final svm_model svmModel;
    private final ScaleParameters scaleParameters;
    private final String[] labels;

    public ClassifierModel(final Object svmModel, final ScaleParameters scaleParameters, final String[] labels)
    {
        this.svmModel = (svm_model)svmModel;
        this.scaleParameters = scaleParameters;
        this.labels = labels;
    }

    public static ClassifierModel defaultModel()
    {
        if (defaultModel != null)
            return defaultModel;

        try
        {
            defaultModel = new ClassifierModelLoader().loadDefaultModel();
            return defaultModel;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Object getSvmModel()
    {
        return svmModel;
    }

    public void assertModel()
    {
        svm_parameter param = svmModel.param;
        assert param.svm_type == svm_parameter.C_SVC;
        assert param.kernel_type == svm_parameter.RBF;
        assert svmModel.nr_class == labels.length;
    }

    public String[] getLabels()
    {
        return IntStream
                .of(svmModel.label)
                .mapToObj(i -> labels[i])
                .toArray(String[]::new);
    }

    public ScaleParameters getScaleParameters()
    {
        return scaleParameters;
    }
}
