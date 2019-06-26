#!/usr/bin/env bash

docker stop backend_calculator_1

mvn clean install spring-boot:repackage

docker start backend_calculator_1
