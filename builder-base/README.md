# Base builder image

This image should be used with Jenkins in a Kubernetes like environment.
The base image contains base utility tools to build your application.

Specialized build image should extend this image to add their own tools.

## Usage

You can use the pre-built image from [docker hub](https://cloud.docker.com/u/arnaudeprez/repository/docker/arnaudeprez/jenkins-builder-base) or build it yourself inside or outside of Openshift.

The usage is limited because it does not contain any specific tool for any language.

Example:

```sh
docker run --rm -ti arnaudeprez/jenkins-builder-base /home/jenkins/test/run
```