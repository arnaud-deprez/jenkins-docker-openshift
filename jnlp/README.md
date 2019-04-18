# Jenkins jnlp agent image

This image should be used with Jenkins in a Kubernetes like environment.
The jnlp image contains the jnlp agent to communicate with jenkins master.

This image is an extension of the [Openshift slave base](https://github.com/openshift/jenkins/tree/master/slave-base) where we just add the jenkins user so that it will run without root.

## Usage

This image is meant to be used with a jenkins master and so its purpose is only technical.