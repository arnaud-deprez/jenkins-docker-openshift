FROM openshift/jenkins-slave-base-centos7:v3.11

LABEL maintainer="Arnaud Deprez <arnaudeprez@gmail.com>"

RUN chown -R 1001:0 $HOME && \
    chmod -R g+rw $HOME

USER 1001