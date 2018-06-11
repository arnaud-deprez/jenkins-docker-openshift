FROM openshift/jenkins-slave-base-centos7:v3.9

LABEL maintainer="Arnaud Deprez <arnaudeprez@gmail.com>"

# helm
ENV HELM_VERSION 2.9.1
RUN curl https://storage.googleapis.com/kubernetes-helm/helm-v${HELM_VERSION}-linux-amd64.tar.gz  | tar xzv && \
  mv linux-amd64/helm /usr/bin/ && \
  rm -rf linux-amd64