
Dataset folder structure:

    data/
      label/
        files...
      label/
        files...



Scratchpad of commands:

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

    ls model
    
