FROM arnaudeprez/jenkins-slave-base:latest

MAINTAINER Arnaud Deprez<arnaudeprez@gmail.com>

ENV NPM_CONFIG_PREFIX=$HOME/.npm-global \
    PATH=$HOME/node_modules/.bin/:$HOME/.npm-global/bin/:$PATH

USER root

ENV NODE_VERSION=8.11.1 \
    YARN_VERSION=1.9.4

RUN set -o errexit -o nounset && \
    curl --silent --location https://rpm.nodesource.com/setup_8.x | bash - && \
    curl --silent --location https://dl.yarnpkg.com/rpm/yarn.repo | tee /etc/yum.repos.d/yarn.repo && \
    INSTALL_PKGS="nodejs-${NODE_VERSION} gcc-c++ make yarn-${YARN_VERSION}" && \
    yum install -y --setopt=tsflags=nodocs $INSTALL_PKGS && \
    rpm -V $INSTALL_PKGS && \
    yum clean all -y

ADD bin/* /usr/local/bin/
ADD test $HOME/test

RUN chown -R 1001:0 $HOME && \
    chmod -R g+rw $HOME

USER 1001