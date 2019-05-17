#!/bin/bash

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
set -e

source_root=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
samples_root=$source_root/samples
docker_hub_org="wso2-cellery"
date=`date +%Y-%m-%d`
time=`date +%H:%M:%S`
log_prefix="[$date $time]"

setyp_type=$1

log_info() {
    echo "${log_prefix}[INFO]" $1
}

cd $source_root
log_info "Installing Cellery GCP $setup_type setup..."
if [ "$setup_type" = "basic" ]; then
    setup_command="cellery setup create gcp"
else
	setup_command="cellery setup create gcp --complete"
fi

#setup_stdout=$($setup_command | tee /dev/tty)
exec 5>&1
setup_stdout=$($setup_command | tee >(cat - >&5))
cluster_name=$(echo $setup_stdout | grep -oP "cellery-cluster[0-9]{1,5}" | head -1)
echo $cluster_name
echo CLUSTER_NAME=$cluster_name > cluster.properties

host_ip=$(kubectl get svc ingress-nginx -n ingress-nginx -o jsonpath="{.status.loadBalancer.ingress[*].ip}")
host_names="cellery-dashboard wso2sp-observability-api cellery-k8s-metrics wso2-apim-gateway wso2-apim hello-world.com pet-store.com idp.cellery-system"
sudo cp /etc/hosts /etc/hosts.back
echo "$host_ip  $host_names" | sudo tee -a /etc/hosts

log_info "Successfully installed cellery GCP $setup_type setup."
