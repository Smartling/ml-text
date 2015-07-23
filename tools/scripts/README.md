
Dataset folder structure:
data/
  label/
    files...
  label/
    files...



    sudo port install libsvm

    cd tools
    ./gradlew :installDist
    cd scripts
    
    virtualenv -p $(which python) .env
    source .env/bin/activate
    pip install numpy

    mkdir -p model
    ./build_model.sh data model

    deactivate

    cp model/train.model ../../src/main/resources/ml.model
    cp model/range ../../src/main/resources/ml.scale
    cp model/labels.txt ../../src/main/resources/ml.labels
