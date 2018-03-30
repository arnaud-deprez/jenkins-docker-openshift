FROM openshift/jenkins-slave-maven-centos7:v3.9

LABEL maintainer="Arnaud Deprez <arnaudeprez@gmail.com>"

USER root

ADD ./contrib/gradle/* $HOME/.gradle/
ADD ./contrib/bin/* /usr/local/bin/
ADD test $HOME/test/

RUN chown -R 1001:0 $HOME && \
    chmod -R g+rw $HOME

USER 1001
