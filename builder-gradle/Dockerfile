FROM arnaudeprez/jenkins-builder-base:latest

LABEL maintainer="Arnaud Deprez <arnaudeprez@gmail.com>"

ENV GRADLE_HOME=/opt/gradle

ARG GRADLE_VERSION=5.3.1
ARG GRADLE_DOWNLOAD_SHA256=1c59a17a054e9c82f0dd881871c9646e943ec4c71dd52ebc6137d17f82337436

USER root

RUN INSTALL_PKGS="java-11-openjdk-devel" \
    && yum install -y --enablerepo=centosplus $INSTALL_PKGS \
	&& echo "Downloading Gradle" \
	&& curl -fsSL -o gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
	\
	&& echo "Checking download hash" \
	&& echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum -c - \
	\
	&& echo "Installing Gradle" \
	&& unzip gradle.zip \
	&& rm gradle.zip \
	&& mkdir -p /opt \
	&& mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    && ln --symbolic "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
    && mkdir -p $HOME/.gradle \
    && rpm -V $INSTALL_PKGS \
    && yum clean all -y \
	&& rm -rf /var/cache/yum/*

ADD gradle/ $GRADLE_HOME/
ADD test $HOME/test

RUN chown -R 1001:0 $HOME && \
    chmod -R g+rw $HOME

USER 1001
