# Jenkins slave gradle image

This image should be used with Jenkins in a Kubernetes like environment.
The base image already contains a gradle installation setup.

The purpose of this image is to fine tune the base image to add a configurable nexus
repository where to upload your artifacts.
This init script will override the configuration of 'maven-publish' plugin in your project
if you have defined any.

## Setup this slave in Openshift

```sh
# Import the source image
oc import-image jenkins-slave-maven-centos7:latest --from=docker.io/openshift/jenkins-slave-maven-centos7:latest --confirm
# Build the new image
oc new-build https://github.com/arnaud-deprez/jenkins-slave-docker.git --context-dir=slave-gradle --name=jenkins-slave-gradle-centos7
# Create the jenkins-slave config
oc create -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-gradle/openshift/slave-config.yml
```
