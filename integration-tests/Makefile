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

run-local-basic: setup-local-basic test-local-basic cleanup-local-basic
run-local-complete: setup-local-complete test-local-complete cleanup-local-complete
run-gcp-basic: setup-gcp-basic test-gcp-basic cleanup-gcp-basic
run-gcp-complete: setup-gcp-complete test-gcp-basic cleanup-gcp-complete

run-minikube-basic: setup-minikube-basic test-minikube-basic cleanup-minikube-basic

.EXPORT_ALL_VARIABLES:
    CELLERY_AUTOMATION_TESTS_ROOT = $(PROJECT_ROOT)

.PHONY: setup-local-basic
setup-local-basic:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash setup.sh basic

.PHONY: test-local-basic
test-local-basic:
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	mvn clean install -Dsuite=src/test/resources/basic.xml -fae

.PHONY: cleanup-local-basic
cleanup-local-basic:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash cleanup.sh

.PHONY: setup-local-complete
setup-local-complete:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash setup.sh complete

.PHONY: test-local-complete
test-local-complete: setup-local-complete
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	mvn clean install -Dsuite=src/test/resources/complete.xml

.PHONY: cleanup-local-complete
cleanup-local-complete:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash cleanup.sh

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

.PHONY: test-minikube-basic
test-minikube-basic:
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	mvn clean install -Dsuite=src/test/resources/basic.xml

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

.PHONY: setup-gcp-basic
setup-gcp-basic:
	cd $(PROJECT_ROOT)/setups/gcp-setup; \
	bash setup.sh basic

.PHONY: test-gcp-basic
test-gcp-basic:
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	mvn clean install -Dsuite=src/test/resources/basic.xml -fae

.PHONY: cleanup-gcp-basic
cleanup-gcp-basic:
	cd $(PROJECT_ROOT)/setups/gcp-setup; \
	bash cleanup.sh

.PHONY: setup-gcp-complete
setup-gcp-complete:
	cd $(PROJECT_ROOT)/setups/gcp-setup; \
	bash setup.sh complete

.PHONY: test-gcp-complete
test-gcp-complete:
	cd $(PROJECT_ROOT)/test-cases/scenario-tests/; \
	mvn clean install -Dsuite=src/test/resources/complete.xml -fae

.PHONY: cleanup-gcp-complete
cleanup-gcp-complete:
	cd $(PROJECT_ROOT)/setups/gcp-setup; \
	bash cleanup.sh
