# Jenkins slave nodejs image

This image should be used with Jenkins in a Kubernetes like environment.
The base image already contains a gradle installation setup.

The purpose of this image is to: 
* install a more recent version of nodejs and npm
* install yarn
* use NPM_MIRROR_URL to download dependencies from a private mirror registry
* use combination of NPM_PUBLISH_URL, NPM_PUBLISH_USERNAME and NPM_PUBLISH_PASSWORD to publish nodejs libraries on a private registry

## Setup this slave in Openshift

First, create a cicd project:

```sh
# Create project cicd if it does not exists yet
oc new-project cicd
```

Then create the slave config, persistent if you want to cache the nodejs modules to reduce the built time
or ephemeral to not use persistent volume.
You can also choose to build the slave image in openshift or use a pre-built version built directly from
these sources.

### Build the slave in openshift

```sh
oc new-build https://github.com/arnaud-deprez/jenkins-slave-docker.git --context-dir=slave-nodejs --name=jenkins-slave-nodejs-centos7
```

Then create the slave config, persistent or ephemeral:

```sh
# TODO: Persistent version
#oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-nodejs/openshift/slave-config-persistent.yml \
#  -p VOLUME_CAPACITY=10Gi \
#  -p IMAGE=imagestreamtag:cicd/jenkins-slave-nodejs-centos7:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-nodejs/openshift/slave-config-ephemeral.yml \
  -p IMAGE=imagestreamtag:cicd/jenkins-slave-nodejs-centos7:latest | oc apply -f -
```

## Setup this pre-built slave from docker.io

In this case, you don't need to build the new image, you can just use the pre-built
image directly in your slave config:

```sh
# TODO: Persistent version
# oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-nodejs/openshift/slave-config-persistent.yml \
#   -p VOLUME_CAPACITY=10Gi \
#   -p IMAGE=arnaudeprez/jenkins-slave-nodejs:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-slave-docker/master/slave-nodejs/openshift/slave-config-ephemeral.yml \
  -p IMAGE=arnaudeprez/jenkins-slave-nodejs:latest | oc apply -f -
```