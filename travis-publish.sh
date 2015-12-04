#!/bin/bash

if ([ "$TRAVIS_BRANCH" == "master" ] || [ ! -z "$TRAVIS_TAG" ]) &&
  [ "$TRAVIS_PULL_REQUEST" == "false" ]
then

  ./gradlew uploadArchives --info --stacktrace

else
  echo "Skipping publish"
fi
