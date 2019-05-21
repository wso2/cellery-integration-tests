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

set -e

source_root=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
samples_root=$source_root/samples
docker_hub_org="wso2-cellery"
date=`date +%Y-%m-%d`
time=`date +%H:%M:%S`
log_prefix="[$date $time]"

setup_type=$1

persistence=false
if [ -z "$4" ]; then
    persistence=$4
fi

username=$2
password=$3

log_info() {
    echo "${log_prefix}[INFO]" $1
}

cd $source_root
if [ $setup_type = "basic" ]; then
    if [ $persistence ]; then
        log_info "Installing Cellery basic setup (persistent) on existing kubeadm k8s cluster..."
        mkdir -p /var/tmp/cellery && chmod 777 -R /var/tmp/cellery
	    cellery setup create existing --persistent
    else
        log_info "Installing Cellery basic setup on existing kubeadm k8s cluster..."
        cellery setup create existing
	fi
else
    if [ $persistence ]; then
        log_info "Installing Cellery complete setup (persistent) on existing kubeadm k8s cluster..."
        mkdir -p /var/tmp/cellery && chmod 777 -R /var/tmp/cellery
	    cellery setup create existing --complete --persistent
    else
        log_info "Installing Cellery complete setup on existing kubeadm k8s cluster..."
        cellery setup create existing --complete
	fi
fi
kubernetes_master=$(kubectl cluster-info | grep -i "Kubernetes master" | awk '{print $6}' )
host_ip=$(grep -oE '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}' <<< "$kubernetes_master")
kubectl patch svc ingress-nginx -n ingress-nginx -p '{"spec": {"externalIPs":["'$host_ip'"]}}'
#service/ingress-nginx patched

host_names="cellery-dashboard wso2sp-observability-api cellery-k8s-metrics wso2-apim-gateway wso2-apim hello-world.com pet-store.com idp.cellery-system"
sudo cp /etc/hosts /etc/hosts.back
echo "$host_ip  $host_names" | sudo tee -a /etc/hosts

log_info "Successfully installed cellery kubeadm $setup_type setup."

cellery login -u ${username} -p ${password}

