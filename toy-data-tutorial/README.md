
Toy-data Tutorial
=================

Were you learn how to train SVM classifier and identify files by their content.

Follow this guide to generate the model files in format expected by **ml-text** library.


Prerequisites
-------------

OSX setup:

* JDK 1.8
* python
* bash
* numpy
    `pip install numpy`
* libsvm cli
    for macports: `sudo port install libsvm`
    for homebrew: `brew install libsvm`

Build feature extractor:

```bash
cd tools
./gradlew :installDist
```


Step 1. Collect data
--------------------

Create an empty directory and extract [toy-data.zip](toy-data.zip) there.
You should get this folder tree:

    toy-data\
      csv\
      json\
      predict\
      yaml\

The names of the sub-folders inside `toy-data` will be used as classification labels.


Step 2. Design features
-----------------------

The features that we will make are based on keywords that identify each file format.

In your directory, create a file named `keywords.yml`. This is a list of strings,
in YAML format; each string is a single keyword, in quotes.

Paste these keywords to `keywords.yml`:

```yaml
- ','
- ":\n"
- '},'
```

Step 3. Extract features and train the model
--------------------------------------------

Still in your folder, find the path of tools/scripts, and run

```bash
tools/scripts/build_model.sh toy-data toy-model keywords.yml
```

When done, the output should be something like

    C=2.0 g=0.25
    Accuracy = 80% (8/10) (classification)

Look inside `toy-model` folder. The files there are what **ml-text** library
expects:

* ml.keywords - the copy of our `keywords.yml`
* ml.labels - indexed list of labels; you can edit the names here, but keep the same order
* ml.scale - scaling parameters per each feature
* ml.model - the trained model, in libsvm format

Now you can identify files with a command

```bash
tools/scripts/predict.sh toy-model toy-data/predict/test1.wat
```

This should give you something like

    labels 1 0 2
    1 0.685109 0.140864 0.174027
    1 json

Which is JSON format with confidence 68.5%

If you are interested, the raw numeric features extracted from train dataset are
in `tmp.toy-model/*.csv` files. These files then get merged and scaled into
`tmp.toy-model/train.scale` file, from which SVM classifier is trained.


Step 4. Play around
-------------------

Load more files to `toy-data`, add more file formats, change keywords, and repeat
model generation step. See how the keywords you use affect accuracy of prediction.

When you done training your model on your dataset, put the generated model files
into your project's `resources` folder, where **ml-text** library will find them.

Well done.
