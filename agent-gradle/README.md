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
# Centos
oc process -f openshift/build-template.yml -p NAME=jenkins-agent-gradle | oc apply -f -
# Rhel
oc process -f openshift/build-template.yml -p NAME=jenkins-agent-gradle -p DOCKERFILE_PATH=Dockerfile.rhel | oc apply -f -
oc start-build jenkins-agent-gradle-docker
```

Or alternatively:

```sh
oc new-build https://github.com/arnaud-deprez/jenkins-agent-docker.git --context-dir=agent-gradle --name=jenkins-agent-gradle
```

Then create the slave config, persistent or ephemeral:

```sh
# Persistent version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle/openshift/agent-config-persistent.yml \
  -p VOLUME_CAPACITY=10Gi \
  -p IMAGE=imagestreamtag:cicd/jenkins-agent-gradle:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle/openshift/agent-config-ephemeral.yml \
  -p IMAGE=imagestreamtag:cicd/jenkins-agent-gradle:latest | oc apply -f -
```

### Setup this pre-built slave from docker.io

In this case, you don't need to build the new image, you can just use the pre-built
image directly in your slave config:

```sh
# Persistent version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle/openshift/agent-config-persistent.yml \
  -p VOLUME_CAPACITY=10Gi \
  -p IMAGE=arnaudeprez/jenkins-agent-gradle:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle/openshift/agent-config-ephemeral.yml \
  -p IMAGE=arnaudeprez/jenkins-agent-gradle:latest | oc apply -f -
```
