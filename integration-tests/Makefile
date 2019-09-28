#  Copyright (c) 2019 WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
#
#  WSO2 Inc. licenses this file to you under the Apache License,
#  Version 2.0 (the "License"); you may not use this file except
#  in compliance with the License.
#  You may obtain a copy of the License at
#
#  http:www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.

PROJECT_ROOT := $(realpath $(dir $(abspath $(lastword $(MAKEFILE_LIST)))))

USERNAME ?= celleryio
PASSWORD ?= celleryio
CELLERY_REGISTRY ?= registry-1.docker.io
GCP_CLUSTER ?= false

run-local-basic: setup-local-basic test-basic cleanup-local
run-local-complete: setup-local-complete test-complete cleanup-local
run-gcp-basic: setup-gcp-basic test-basic cleanup-gcp
run-gcp-complete: setup-gcp-complete test-complete cleanup-gcp
run-kubeadm-basic: setup-kubeadm-basic test-basic cleanup-kubeadm
run-kubeadm-basic-persistent: setup-kubeadm-basic-persistent test-basic cleanup-kubeadm
run-kubeadm-complete: setup-kubeadm-complete test-complete cleanup-kubeadm
run-kubeadm-complete-persistent: setup-kubeadm-complete-persistent test-complete cleanup-kubeadm

run-minikube-basic: setup-minikube-basic test-basic cleanup-minikube-basic

.EXPORT_ALL_VARIABLES:
    CELLERY_AUTOMATION_TESTS_ROOT = $(PROJECT_ROOT)

.PHONY: clone-samples
clone-samples:
	git clone https://github.com/wso2-cellery/sdk
	cd sdk && git rev-parse --verify HEAD
	mkdir test-cases/cells
	cp -r sdk/test-cases/* test-cases/cells/
	rm -rf sdk

.PHONY: remove-samples
remove-samples:
	rm -rf $(PROJECT_ROOT)/test-cases/cells

.PHONY: test-basic
test-basic: clone-samples
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	export IS_GCP=$(GCP_CLUSTER);\
	mvn clean install -Dsuite=src/test/resources/basic.xml -fae
	rm -rf $(PROJECT_ROOT)/test-cases/cells

.PHONY: test-complete
test-complete: clone-samples
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	export IS_GCP=$(GCP_CLUSTER);\
	mvn clean install -Dsuite=src/test/resources/complete.xml
	rm -rf test-cases/cells

# Local setup targets
.PHONY: setup-local-basic
setup-local-basic:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash setup.sh basic $(USERNAME) $(PASSWORD) $(CELLERY_REGISTRY)

.PHONY: setup-local-complete
setup-local-complete:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash setup.sh complete $(USERNAME) $(PASSWORD) $(CELLERY_REGISTRY)

.PHONY: cleanup-local
cleanup-local:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash cleanup.sh

# GCP setup targets
.PHONY: setup-gcp-basic
setup-gcp-basic:
	cd $(PROJECT_ROOT)/setups/gcp-setup; \
	bash setup.sh basic $(USERNAME) $(PASSWORD)

.PHONY: setup-gcp-complete
setup-gcp-complete:
	cd $(PROJECT_ROOT)/setups/gcp-setup; \
	bash setup.sh complete $(USERNAME) $(PASSWORD)

.PHONY: cleanup-gcp
cleanup-gcp:
	cd $(PROJECT_ROOT)/setups/gcp-setup; \
	bash cleanup.sh

# GCP existing setup targets
.PHONY: init-gcp-persistent
init-gcp-persistent:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash init.sh true

.PHONY: setup-existing-gcp-basic-persistent
setup-existing-gcp-basic-persistent:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash setup.sh basic $(USERNAME) $(PASSWORD) true

.PHONY: setup-existing-gcp-complete-persistent
setup-existing-gcp-complete-persistent:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash setup.sh complete $(USERNAME) $(PASSWORD) true

.PHONY: cleanup-existing-gcp
cleanup-existing-gcp:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash cleanup.sh

.PHONY: destroy-gcp-persistent
destroy-gcp-persistent:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash destroy.sh true

.PHONY: init-gcp
init-gcp:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash init.sh

.PHONY: setup-existing-gcp-basic
setup-existing-gcp-basic:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash setup.sh basic $(USERNAME) $(PASSWORD)

.PHONY: setup-existing-gcp-complete
setup-existing-gcp-complete:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash setup.sh complete $(USERNAME) $(PASSWORD)

.PHONY: destroy-gcp
destroy-gcp:
	cd $(PROJECT_ROOT)/setups/existing-cluster/gcp-gke; \
	bash destroy.sh

# Kubeadm setup targets
.PHONY: init-kubeadm
init-kubeadm:
	cd $(PROJECT_ROOT)/setups/existing-cluster/kube-admin; \
	sudo bash init.sh

.PHONY: setup-kubeadm-basic
setup-kubeadm-basic:
	cd $(PROJECT_ROOT)/setups/existing-cluster/kube-admin; \
	bash setup.sh basic $(USERNAME) $(PASSWORD) $(CELLERY_REGISTRY)

.PHONY: setup-kubeadm-basic-persistent
setup-kubeadm-basic-persistent:
	cd $(PROJECT_ROOT)/setups/existing-cluster/kube-admin; \
	bash setup.sh basic $(USERNAME) $(PASSWORD) $(CELLERY_REGISTRY) true

.PHONY: setup-kubeadm-complete
setup-kubeadm-complete:
	cd $(PROJECT_ROOT)/setups/existing-cluster/kube-admin; \
	bash setup.sh complete $(USERNAME) $(PASSWORD) $(CELLERY_REGISTRY)

.PHONY: setup-kubeadm-complete-persistent
setup-kubeadm-complete-persistent:
	cd $(PROJECT_ROOT)/setups/existing-cluster/kube-admin; \
	bash setup.sh complete $(USERNAME) $(PASSWORD) $(CELLERY_REGISTRY) true

.PHONY: cleanup-kubeadm
cleanup-kubeadm:
	cd $(PROJECT_ROOT)/setups/existing-cluster/kube-admin; \
	bash cleanup.sh

# Minikube setup targets
.PHONY: start-minikube
start-minikube:
	cd $(PROJECT_ROOT)/setups/existing-cluster/minikube; \
	bash init.sh

.PHONY: delete-minikube
delete-minikube:
	cd $(PROJECT_ROOT)/setups/existing-cluster/minikube; \
	bash destroy.sh

.PHONY: setup-minikube-basic
setup-minikube-basic:
	cd $(PROJECT_ROOT)/setups/existing-cluster/minikube; \
	bash setup.sh basic

.PHONY: cleanup-minikube-basic
cleanup-minikube-basic:
	cd $(PROJECT_ROOT)/setups/existing-cluster/minikube; \
	bash cleanup.sh

.PHONY: start-docker-desktop
start-docker-desktop:
	cd $(PROJECT_ROOT)/setups/existing-cluster/docker-desktop; \
	bash init.sh

.PHONY: stop-docker-desktop
stop-docker-desktop:
	cd $(PROJECT_ROOT)/setups/existing-cluster/docker-desktop; \
	bash destroy.sh

.PHONY: setup-docker-desktop-basic
setup-docker-desktop-basic:
	cd $(PROJECT_ROOT)/setups/existing-cluster/docker-desktop; \
	bash setup.sh basic

.PHONY: test-docker-desktop-basic
test-docker-desktop-basic:
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	mvn clean install -Dsuite=src/test/resources/basic.xml

.PHONY: cleanup-docker-desktop-basic
cleanup-docker-desktop-basic:
	cd $(PROJECT_ROOT)/setups/existing-cluster/docker-desktop; \
	bash cleanup.sh
