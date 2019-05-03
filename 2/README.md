# Jenkins for OpenShift

This repository is meant to be used with Jenkins S2I on OpenShift to install third parties plugins 
on top of the official Jenkins image provided in OpenShift.
So all the [official documentation](https://github.com/openshift/jenkins) remains valid for this image.

## Content

### Plugins

This jenkins master image contains Blue Ocean plugin among [other useful plugins](plugins.txt).

## Usage

### In docker

This image can be built via:

* regular docker build:

```sh
docker build -t jenkins-custom ./2
```

* [source-to-image tool](https://github.com/openshift/source-to-image):

```sh
s2i build https://github.com/arnaud-deprez/jenkins-openshift-docker openshift/jenkins-2-centos7:latest jenkins-custom --context-dir=./2
```

Then run a container:

```sh
docker run -d --name jenkins -p 8080:8080 -e JENKINS_PASSWORD=password -e OPENSHIFT_ENABLE_OAUTH=false jenkins-custom
```

### In Openshift/Kubernetes

//TODO: move it elsewhere

