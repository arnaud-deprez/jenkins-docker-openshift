IMAGE_NAME = arnaudeprez/jenkins-openshift-jnlp:latest

.PHONY: build
build:
	docker build -t $(IMAGE_NAME) .

.PHONY: test
test: build


.PHONY: publish
publish: build test
	echo "$(DOCKER_PASSWORD)" | docker login -u "$(DOCKER_USERNAME)" --password-stdin
	docker push $(IMAGE_NAME)