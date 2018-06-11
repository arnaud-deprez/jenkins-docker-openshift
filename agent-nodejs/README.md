# Jenkins slave nodejs image

This image should be used with Jenkins in a Kubernetes like environment.

The purpose of this image is to:

* install nodejs and npm
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
oc process -f openshift/build-template.yml -p NAME=jenkins-agent-nodejs | oc apply -f -
oc start-build jenkins-agent-nodejs-docker
```

Or alternatively:

```sh
oc new-build https://github.com/arnaud-deprez/jenkins-agent-docker.git --context-dir=agent-nodejs --name=jenkins-agent-nodejs
```

Then create the slave config, persistent or ephemeral:

```sh
# TODO: Persistent version
#oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-nodejs/openshift/agent-config-persistent.yml \
#  -p VOLUME_CAPACITY=10Gi \
#  -p IMAGE=imagestreamtag:cicd/jenkins-agent-nodejs:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-nodejs/openshift/agent-config-ephemeral.yml \
  -p IMAGE=imagestreamtag:cicd/jenkins-agent-nodejs:latest | oc apply -f -
```

## Setup this pre-built slave from docker.io

In this case, you don't need to build the new image, you can just use the pre-built
image directly in your slave config:

```sh
# TODO: Persistent version
# oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-nodejs/openshift/agent-config-persistent.yml \
#   -p VOLUME_CAPACITY=10Gi \
#   -p IMAGE=arnaudeprez/jenkins-agent-nodejs:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-nodejs/openshift/agent-config-ephemeral.yml \
  -p IMAGE=arnaudeprez/jenkins-agent-nodejs:latest | oc apply -f -
```