IMAGES=2 jnlp agent-base agent-gradle agent-nodejs

.PHONY: build
build:
	for i in $(IMAGES); do \
		make -C $$i build; \
	done