# Jenkins slave gradle image

This image should be used with Jenkins in a Kubernetes like environment.

The purpose of this image is to provide gradle with a configurable maven repository where to upload your artifacts.
This init script will override the configuration of 'maven-publish' plugin in your project
if you have defined any.

It optionally supports some environment variable when it is used with an enterprise maven repository:

| Environment variable         | Description |
| ---------------------------- | ----------- |
| `MAVEN_MIRROR_URL`           | a maven mirror url used to download artifacts |
| `MAVEN_PUBLISH_URL`          | a maven url used to publish artifacts |
| `MAVEN_PUBLISH_SNAPSHOT_URL` | a maven url used to publish snapshots artifacts. If not provided, it will use `MAVEN_PUBLISH_SNAPSHOT_URL` |
| `MAVEN_PUBLISH_USERNAME`     | a username used to authenticate against `MAVEN_PUBLISH_URL` and `MAVEN_PUBLISH_SNAPSHOT_URL` |
| `MAVEN_PUBLISH_PASSWORD`     | the password associated to the username for authentication |

## Usage

You can use the pre-built image from [docker hub](https://cloud.docker.com/u/arnaudeprez/repository/docker/arnaudeprez/jenkins-builder-gradle) or build it yourself inside or outside of Openshift.

Example:

```sh
docker run --rm -ti arnaudeprez/jenkins-builder-gradle /home/jenkins/test/run
```