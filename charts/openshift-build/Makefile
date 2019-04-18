build:
	helm init --client-only
	helm dependency build .
	helm lint .

apply:
	helm template --name jenkins --namespace $(NAMESPACE) \
		--set fullnameOverride=jenkins-openshift \
		--set global.image.registry=docker-registry.default.svc:5000/$(NAMESPACE) \
		. | kubectl -n $(NAMESPACE) apply -f -

applyRhel:
	helm template --name jenkins --namespace $(NAMESPACE) \
		--set fullnameOverride=jenkins-openshift \
		--set global.image.registry=docker-registry.default.svc:5000/$(NAMESPACE) \
		--set images.os=rhel \
		. | kubectl -n $(NAMESPACE) apply -f -