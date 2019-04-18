#!/bin/bash

images=$(oc -n cicd-staging get istag -l build=jenkins -o go-template --template='{{range .items}}{{.metadata.name}} {{end}}')
for i in $images
do
    oc tag cicd-staging/$i cicd/$i
done