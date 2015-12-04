#!/bin/bash

openssl aes-256-cbc -k "$SIGNING_PASSWORD" -in .secring.gpg.enc -out .secring.gpg -d

./gradlew \
  -Psigning.keyId="$SIGNING_KEY" \
  -Psigning.password="$SIGNING_PASSWORD" \
  -Psigning.secretKeyRingFile="$TRAVIS_BUILD_DIR/.secring.gpg" \
  uploadArchives --info --stacktrace
