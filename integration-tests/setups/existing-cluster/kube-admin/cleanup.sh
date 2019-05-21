#!/bin/bash
# ------------------------------------------------------------------------
#
# Copyright 2019 WSO2, Inc. (http://wso2.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License
#
# ------------------------------------------------------------------------

log_info() {
    echo "${log_prefix}[INFO]" $1
}
sudo mv /etc/hosts.back /etc/hosts
if [ -d /var/tmp/cellery ]; then
    rm -rf /var/tmp/cellery
fi
log_info "Destroying kubeadmin cellery setup..."
cellery setup cleanup existing -y

log_info "Cleanup complete."
