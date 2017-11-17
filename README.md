# Jenkins docker images repository

This repository contains jenkins docker images to run on kubernetes or openshift.
This has been tested on an Openshift cluster and this is based on official Openshift jenkins based image.

## Jenkins master

The jenkins master image is based on the official [openshift/jenkins](https://github.com/openshift/jenkins) image.
More information [here](2/README.adoc).

## Jenkins slave images

Here is the current list of images:

* [jenkins-slave-gradle](slave-gradle/README.adoc)
