#!/bin/bash
mkdir -p /tmp/bin

echo "installing helm"
HELM_VERSION=2.12.2
curl -ssL https://storage.googleapis.com/kubernetes-helm/helm-v${HELM_VERSION}-linux-amd64.tar.gz \
  | tar -xz -C /tmp/bin --strip-components 1 linux-amd64/helm
chmod +x /tmp/bin/helm
helm init --client-only