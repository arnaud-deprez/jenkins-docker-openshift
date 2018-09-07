FROM arnaudeprez/jenkins-slave-base:latest

MAINTAINER Arnaud Deprez<arnaudeprez@gmail.com>

ENV NPM_CONFIG_PREFIX=$HOME/.npm-global \
    PATH=$HOME/node_modules/.bin/:$HOME/.npm-global/bin/:$PATH

USER root

ENV NODE_VERSION 8.11.1

RUN curl --silent --location https://rpm.nodesource.com/setup_8.x | bash - && \
    INSTALL_PKGS="nodejs-$NODE_VERSION gcc-c++ make" && \
    yum install -y --setopt=tsflags=nodocs $INSTALL_PKGS && \
    rpm -V $INSTALL_PKGS && \
    yum clean all -y

ENV YARN_VERSION 1.5.1

RUN set -ex \
    && for key in \
        6A010C5166006599AA17F08146C2130DFD2497F5 \
    ; do \
        gpg --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys "$key" || \
        gpg --keyserver hkp://ipv4.pool.sks-keyservers.net --recv-keys "$key" || \
        gpg --keyserver hkp://pgp.mit.edu:80 --recv-keys "$key" ; \
    done \
    && curl -fSLO --compressed "https://yarnpkg.com/downloads/$YARN_VERSION/yarn-v$YARN_VERSION.tar.gz" \
    && curl -fSLO --compressed "https://yarnpkg.com/downloads/$YARN_VERSION/yarn-v$YARN_VERSION.tar.gz.asc" \
    && gpg --batch --verify yarn-v$YARN_VERSION.tar.gz.asc yarn-v$YARN_VERSION.tar.gz \
    && mkdir -p /opt \
    && tar -xzf yarn-v$YARN_VERSION.tar.gz -C /opt/ \
    && ln -s /opt/yarn-v$YARN_VERSION/bin/yarn /usr/local/bin/yarn \
    && ln -s /opt/yarn-v$YARN_VERSION/bin/yarnpkg /usr/local/bin/yarnpkg \
    && rm yarn-v$YARN_VERSION.tar.gz.asc yarn-v$YARN_VERSION.tar.gz

ADD contrib/bin/* /usr/local/bin/
ADD test $HOME/test/

RUN chown -R 1001:0 $HOME && \
    chmod -R g+rw $HOME

USER 1001