IMAGES=2 jnlp agent-base agent-gradle agent-nodejs

buildCharts:
	for chart in charts/*; do \
		helm dependency build $$chart; \
		helm lint $$chart; \
	done

.PHONY: build
build: buildCharts
	for i in $(IMAGES); do \
		make -C $$i build; \
	done