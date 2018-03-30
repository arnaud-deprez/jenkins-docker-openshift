# Jenkins slave gradle image

This image should be used with Jenkins in a Kubernetes like environment.
The base image already contains a gradle installation setup.

The purpose of this image is to fine tune the base image to add a configurable nexus
repository where to upload your artifacts.
This init script will override the configuration of 'maven-publish' plugin in your project
if you have defined any.

## Setup this slave in Openshift

First, create a cicd project:

```sh
# Create project cicd if it does not exists yet
oc new-project cicd
```

Then create the slave config, persistent if you want to cache the gradle cache to reduce the built time
or ephemeral to not use persistent volume.
You can also choose to build the slave image in openshift or use a pre-built version built directly from
these sources.

### Build the slave in openshift

```sh
oc process -f openshift/build-template.yml -p NAME=jenkins-slave-gradle-centos7 | oc apply -f -
oc start-build jenkins-slave-gradle-centos7-docker
```

Or alternatively:

```sh
oc new-build https://github.com/arnaud-deprez/jenkins-slave-docker.git --context-dir=slave-gradle --name=jenkins-slave-gradle-centos7
```

Then create the slave config, persistent or ephemeral:

```sh
# Persistent version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-gradle/openshift/slave-config-persistent.yml \
  -p VOLUME_CAPACITY=10Gi \
  -p IMAGE=imagestreamtag:cicd/jenkins-slave-gradle-centos7:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-gradle/openshift/slave-config-ephemeral.yml \
  -p IMAGE=imagestreamtag:cicd/jenkins-slave-gradle-centos7:latest | oc apply -f -
```

## Setup this pre-built slave from docker.io

In this case, you don't need to build the new image, you can just use the pre-built
image directly in your slave config:

```sh
# Persistent version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-gradle/openshift/slave-config-persistent.yml \
  -p VOLUME_CAPACITY=10Gi \
  -p IMAGE=arnaudeprez/jenkins-slave-gradle:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-gradle/openshift/slave-config-ephemeral.yml \
  -p IMAGE=arnaudeprez/jenkins-slave-gradle:latest | oc apply -f -
```
