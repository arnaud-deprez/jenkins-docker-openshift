IMAGE_NAME = arnaudeprez/jenkins-builder-nodejs:latest

.PHONY: build
build:
	docker build -t $(IMAGE_NAME) .

.PHONY: test
test: build
	docker run --rm $(IMAGE_NAME) /home/jenkins/test/run

.PHONY: publish
publish: build test
	echo "$(DOCKER_PASSWORD)" | docker login -u "$(DOCKER_USERNAME)" --password-stdin
	docker push $(IMAGE_NAME)