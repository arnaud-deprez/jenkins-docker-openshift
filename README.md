# Jenkins docker images repository

This repository contains jenkins docker images to run on kubernetes or openshift.
This has been tested on an Openshift cluster and this is based on official Openshift jenkins based image.

## Jenkins master

The jenkins master image is based on the official [openshift/jenkins](https://github.com/openshift/jenkins) image with some added plugins such as:

* [blueocean](https://plugins.jenkins.io/blueocean)

This image can be either:

* build by regular docker build:

```sh
docker build -t <repository>/<image_name> ./2
```

* or via source-to-image tool:

```sh
s2i build https://github.com/arnaud-deprez/jenkins-openshift-docker openshift/jenkins-2-centos7:latest <repository>/<image_name> --context-dir=./2
```

* or in openshift

```sh
oc new-build jenkins:2~https://github.com/arnaud-deprez/jenkins-openshift-docker --context-dir=./2
```

## Jenkins slave images

Here is the current list of images:

* [jenkins-slave-gradle](slave-gradle/README.adoc)
