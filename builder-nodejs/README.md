# Jenkins slave nodejs image

This image should be used with Jenkins in a Kubernetes like environment.

The purpose of this image is to:

* install nodejs and npm
* install yarn

It optionally supports some environment variable when it is used with an enterprise maven repository:

| Environment variable         | Description |
| ---------------------------- | ----------- |
| `NPM_MIRROR_URL`           | a npm mirror url used to download artifacts |
| `NPM_PUBLISH_URL`          | a npm repository url used to publish artifacts |
| `NPM_PUBLISH_USERNAME`     | a username used to authenticate against `NPM_PUBLISH_URL` |
| `NPM_PUBLISH_PASSWORD`     | the password associated to the username for authentication |

## Usage

You can use the pre-built image from [docker hub](https://cloud.docker.com/u/arnaudeprez/repository/docker/arnaudeprez/jenkins-builder-nodejs) or build it yourself inside or outside of Openshift.

Example:

```sh
docker run --rm -ti arnaudeprez/jenkins-builder-nodejs /home/jenkins/test/run
```