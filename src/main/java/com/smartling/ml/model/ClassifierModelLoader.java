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

import com.smartling.ml.readers.LabelsReader;
import com.smartling.ml.readers.ScaleParametersReader;
import com.smartling.ml.readers.SvmModelReader;
import java.io.IOException;
import libsvm.svm_model;

/**
 * Loads {@link ClassifierModel} from a resource.
 * <p>
 *     Default resource files:
 *     <ul>
 *         <li><b>"ml.model"</b> - pre-trained model
 *         <li><b>"ml.scale"</b> - scaling parameters
 *         <li><b>"ml.labels"</b> - named labels
 *     </ul>
 */
public class ClassifierModelLoader
{
    private final ResourceLoader resourceLoader;

    public ClassifierModelLoader()
    {
        this(new ResourceLoader());
    }

    public ClassifierModelLoader(final ClassLoader classLoader)
    {
        this(new ResourceLoader(classLoader));
    }

    public ClassifierModelLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public ClassifierModel loadDefaultModel() throws IOException
    {
        return loadResource("ml.model", "ml.scale", "ml.labels");
    }

    public ClassifierModel loadResource(final String modelResource, final String scaleResource, final String labelsResource) throws IOException
    {
        svm_model model = loadModel(modelResource, resourceLoader);
        ScaleParameters scale = loadScaleParameters(scaleResource, resourceLoader);
        String[] labels = loadLabels(labelsResource, resourceLoader);

        return new ClassifierModel(model, scale, labels);
    }

    private svm_model loadModel(String resourceName, final ResourceLoader resourceLoader) throws IOException
    {
        return (svm_model)new SvmModelReader(resourceLoader).fromResource(resourceName);
    }

    private ScaleParameters loadScaleParameters(String resourceName, final ResourceLoader resourceLoader) throws IOException
    {
        return new ScaleParametersReader(resourceLoader).fromResource(resourceName);
    }

    private String[] loadLabels(String resourceName, final ResourceLoader resourceLoader) throws IOException
    {
        return new LabelsReader(resourceLoader).fromResource(resourceName);
    }

}
