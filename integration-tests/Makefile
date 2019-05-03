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


.EXPORT_ALL_VARIABLES:
    CELLERY_AUTOMATION_TESTS_ROOT = $(PROJECT_ROOT)

.PHONY: build-test-base
build-test-base:
	cd $(PROJECT_ROOT)/test-cases/base; \
	mvn clean install

.PHONY: setup-local-basic
setup-local-basic:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash setup.sh basic

.PHONY: test-local-basic
test-local-basic: setup-local-basic build-test-base
	cd $(PROJECT_ROOT)/test-cases/hello-world-web/test; \
	mvn clean install

.PHONY: setup-local-complete
setup-local-complete:
	cd $(PROJECT_ROOT)/setups/local-setup; \
	bash setup.sh complete

.PHONY: test-local-complete
test-local-complete: setup-local-complete
	cd $(PROJECT_ROOT)/test-cases/test-case-01; \
	bash test.sh
	cd $(PROJECT_ROOT)/test-cases/test-case-02; \
	bash test.sh

