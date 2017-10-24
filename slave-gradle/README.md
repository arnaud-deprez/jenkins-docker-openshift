# Jenkins slave gradle image

This image should be used with Jenkins in a Kubernetes like environment.
The base image already contains a gradle installation setup.

The purpose of this image is to fine tune the base image to add a configurable nexus
repository where to upload your artifacts.
This init script will override the configuration of 'maven-publish' plugin in your project
if you have defined any.

## Setup this slave in Openshift

```sh
# Create project cicd if it does not exists yet
oc new-project cicd
# Import the source image
oc import-image jenkins-slave-maven-centos7:latest --from=docker.io/openshift/jenkins-slave-maven-centos7:latest --confirm
# Build the new image
oc new-build https://github.com/arnaud-deprez/jenkins-slave-docker.git --context-dir=slave-gradle --name=jenkins-slave-gradle-centos7
# Create the jenkins-slave config
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-gradle/openshift/slave-config.yml | oc apply -f -
```

## Setup this pre-built slave from docker.io

In this case, you don't need to build the new image, you can just use the pre-built
image directly in your slave config:

```sh
# Create the jenkins-slave config
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-gradle/openshift/slave-config.yml -p IMAGE=docker.io/arnaudeprez/jenkins-slave-gradle:latest | oc apply -f -
```
