# Jenkins for OpenShift

This repository is meant to be used with Jenkins S2I on OpenShift to install third parties plugins 
on top of the official Jenkins image provided in OpenShift.
So all the [official documentation](https://github.com/openshift/jenkins) remains valid for this image.

## Content

### Plugins

This jenkins master image contains Blue Ocean plugin among [other useful plugins](plugins.txt).

## How to use this image ?

### With docker

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

### With Openshift

Create a Jenkins S2I build with this Bitbucket repository:

```sh
oc new-build jenkins:2~https://github.com/arnaud-deprez/jenkins-openshift-docker.git \
    -e OVERRIDE_PV_CONFIG_WITH_IMAGE_CONFIG=true \
    -e OVERRIDE_PV_PLUGINS_WITH_IMAGE_PLUGINS=true \
    --context-dir=2 --name=jenkins-custom
oc patch bc jenkins-custom -p '{"spec": {"successfulBuildsHistoryLimit": 2, "failedBuildsHistoryLimit": 2}}'
```

Then you can deploy the Jenkins templates with the customized image. Replace cicd
with the project where the above S2I build is created:

```sh
oc new-app jenkins-persistent \
    -p NAMESPACE=cicd \
    -p JENKINS_IMAGE_STREAM_TAG=jenkins-custom:latest \
    -p MEMORY_LIMIT=2Gi \
    -p VOLUME_CAPACITY=5Gi
```

This will deploy a jenkins instance and a service account Jenkins.

## Allow Jenkins to deploy edit other projects

If Jenkins needs to deploy application in other project than its own, it needs to have the role edit.
You can achieve it by giving this role to the Jenkins service account:

Replace $PROJECT with the desired one:

```sh
oc new-project $PROJECT
oc policy add-role-to-user edit system:serviceaccount:cicd:jenkins -n $PROJECT
```

## Allow a project B to pull image from another project A

For this, you need to provide image-puller access, so that $PROJECT_B project can pull an image from the $PROJECT_A project.

```sh
oc policy add-role-to-group system:image-puller system:serviceaccounts:$PROJECT_B -n $PROJECT_A
```

A typical use case of it is that images will be build in development project and will be pulled in testing project.
So as an example:

```sh
oc policy add-role-to-group system:image-puller system:serviceaccounts:testing -n development
```