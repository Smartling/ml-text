#!/bin/bash

openssl aes-256-cbc -k "$SIGNING_PASSWORD" -in .secring.gpg.enc -out .secring.gpg -d

./gradlew -Psigning.secretKeyRingFile=.secring.gpg uploadArchives --info --stacktrace
