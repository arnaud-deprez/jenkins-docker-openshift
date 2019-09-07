FROM arnaudeprez/jenkins-builder-base:latest

LABEL maintainer="Arnaud Deprez<arnaudeprez@gmail.com>"

ENV NPM_CONFIG_PREFIX=$HOME/.npm-global \
    PATH=$HOME/node_modules/.bin/:$HOME/.npm-global/bin/:$PATH

ARG NODE_VERSION=10.15.3
ARG YARN_VERSION=1.15.2

USER root

RUN curl -fsSL https://rpm.nodesource.com/setup_10.x | bash - && \
    curl -fsSL https://dl.yarnpkg.com/rpm/yarn.repo | tee /etc/yum.repos.d/yarn.repo && \
    INSTALL_PKGS="nodejs-${NODE_VERSION} gcc-c++ make yarn-${YARN_VERSION}" && \
    yum install -y --setopt=tsflags=nodocs $INSTALL_PKGS && \
    # rpm -V $INSTALL_PKGS && \
    yum clean all -y && \
    rm -rf /var/cache/yum/*

ADD bin/ /usr/local/bin/
ADD test $HOME/test

RUN chown -R 1001:0 $HOME && \
    chmod -R g+rw $HOME

USER 1001