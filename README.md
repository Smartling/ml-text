[![build status](https://travis-ci.org/Smartling/ml-text.svg)](https://travis-ci.org/Smartling/ml-text)

Smartling Text Classifier
=========================


What is it
----------

This is the core library of text classification.
At Smartling we use it to detect format of textual files.

Specifically, this is [libsvm](https://github.com/cjlin1/libsvm) in C-SVM multi-class classification mode.

In the classification workflow of

1. collect dataset
2. extract features
3. train model
4. classify new data

... this library covers steps 2 and 4.

Note, that you should use the same text preprocessing and feature extractor for training and prediction.


Using
-----

Prepare model files. Default model is taken from resource files:

* ml.keywords - (YAML list of string literals) keywords to extract from text and build statistics 
* ml.labels - class names
* ml.scale - scaling range of numeric features
* ml.model - trained SVM model

Use `ClassifierModelLoader` and `FeatureKeywords` to load these from non-default location.

Sample classification:

```java
void doClassify(InputStream inputStream) {
    ContentPreprocessor preprocessor = new ContentPreprocessor()
    String content = preprocessor.convert(inputStream).get();

    FeatureExtractor featureExtractor = new FeatureExtractor();
    Features features = featureExtractor.extract(content);

    SvmClassifier classifier = new SvmClassifier();
    ClassifierResult result = classifier.classify(features);

    System.out.println(String.format("classified as: %s (%.2f confidence)",
         result.getLabels()[0], result.getConfidence()[0]));
}
```


Authors
-------

* [Pavel Ivashkov](https://github.com/paiv) 


Copyright and license
---------------------

Copyright 2015 Smartling, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
