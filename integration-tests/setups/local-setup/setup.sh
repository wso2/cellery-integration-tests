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

setup_type=$1
username=$2
password=$3
cellery_registry=$4

log_info() {
    echo "${log_prefix}[INFO]" $1
}   

cd $source_root
if [ $setup_type = "basic" ]; then
	log_info "Installing Cellery local basic setup..."
	cellery setup create local -y
else
    log_info "Installing Cellery local complete setup..."
	cellery setup create local --complete -y
fi

host_ip="192.168.56.10"
host_names="cellery-dashboard wso2sp-observability-api cellery-k8s-metrics wso2-apim-gateway wso2-apim hello-world.com pet-store.com idp.cellery-system"
sudo cp /etc/hosts /etc/hosts.back
echo "$host_ip  $host_names" | sudo tee -a /etc/hosts

log_info "Successfully installed cellery local $setup_type setup."

cellery login ${cellery_registry} -u ${username} -p ${password}
