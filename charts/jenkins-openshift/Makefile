build:
	helm init --client-only
	helm dependency build .
	helm lint .

applyFromBuild:
	# To avoid openshift to auto provision the default jenkins, you must a service called `jenkins`.
	# So use fullnameOverride=jenkins
	# See https://docs.openshift.org/latest/install_config/configuring_pipeline_execution.html
	helm template --name jenkins --namespace $(NAMESPACE) \
		-f ../openshift-build/values.yaml \
		--set fullnameOverride=jenkins \
		--set global.image.registry=172.30.1.1:5000/$(NAMESPACE) \
		--set master.route.enabled=true \
		. | kubectl -n $(NAMESPACE) apply -f -

apply:
	# To avoid openshift to auto provision the default jenkins, you must a service called `jenkins`.
	# So use fullnameOverride=jenkins
	# See https://docs.openshift.org/latest/install_config/configuring_pipeline_execution.html
	helm template --name jenkins --namespace $(NAMESPACE) \
		--set fullnameOverride=jenkins \
		--set master.route.enabled=true \
		. | kubectl -n $(NAMESPACE) apply -f -