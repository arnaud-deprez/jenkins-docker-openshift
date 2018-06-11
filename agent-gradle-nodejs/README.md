# Jenkins slave gradle nodejs image

This slave should be used with Jenkins in a Kubernetes like environment.
The slave is based on [jenkins-agent-gradle](../agent-gradle/README.md) and [jenkins-agent-nodejs](../agent-nodejs/README.md).

By combining these 2 images in a single PodTemplate, we can build and test [JHipster](https://www.jhipster.tech) like application easily.

This PodTemplate inherits from [jenkins-agent-gradle](../agent-gradle/README.md) (so this is a pre-requisite) and add the [jenkins-agent-nodejs](../agent-nodejs/README.md) on the top.

## Setup

Create the slave config, persistent or ephemeral:

```sh
# Persistent version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle-nodejs/openshift/agent-config-persistent.yml \
  -p VOLUME_CAPACITY=10Gi \
  -p IMAGE=imagestreamtag:cicd/jenkins-agent-gradle-nodejs:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle/openshift/agent-config-ephemeral.yml \
  -p IMAGE=imagestreamtag:cicd/jenkins-agent-gradle-nodejs:latest | oc apply -f -
```

Or if you want to use the pre-built slave images from docker.io

```sh
# Persistent version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle-nodejs/openshift/agent-config-persistent.yml \
  -p VOLUME_CAPACITY=10Gi \
  -p IMAGE=arnaudeprez/jenkins-agent-nodejs:latest | oc apply -f -
# Ephemeral version
oc process -f https://raw.githubusercontent.com/arnaud-deprez/jenkins-agent-docker/master/agent-gradle-nodejs/openshift/agent-config-ephemeral.yml \
  -p IMAGE=arnaudeprez/jenkins-agent-nodejs:latest | oc apply -f -
```