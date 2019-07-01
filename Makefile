IMAGES=2 jnlp builder-base builder-gradle builder-nodejs

buildCharts:
	@for chart in charts/*; do \
		helm dependency build $$chart; \
		helm lint $$chart; \
	done

.PHONY: build
build: buildCharts
	@for i in $(IMAGES); do \
		make -C $$i build; \
	done

openshiftBuild:
	@oc -n $(NAMESPACE) start-build jenkins-openshift --from-dir=.
	@oc -n $(NAMESPACE) start-build jenkins-jnlp --from-dir=.
	@oc -n $(NAMESPACE) start-build jenkins-builder-base --from-dir=.
	@okd-verify-build $(NAMESPACE) jenkins-openshift jenkins-jnlp jenkins-builder-base
	@oc -n $(NAMESPACE) start-build jenkins-builder-gradle --from-dir=.
	@oc -n $(NAMESPACE) start-build jenkins-builder-nodejs --from-dir=.
	@okd-verify-build $(NAMESPACE) jenkins-builder-gradle jenkins-builder-nodejs

openshiftTestPipeline:
	@oc -n $(NAMESPACE) apply -f openshift/slave-test-pipeline.yaml
	@oc -n $(NAMESPACE) start-build jenkins-test-pipeline
	@okd-verify-build $(NAMESPACE) jenkins-test-pipeline