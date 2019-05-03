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

# Creates a k8s cluster with docker-desktop for installing Cellery

date=`date +%Y-%m-%d`
time=`date +%H:%M:%S`
log_prefix="[$date $time]"

log_info() {
    echo "${log_prefix}[INFO]" $1
}

start_docker(){
    log_info "Staring docker-desktop with a k8s cluster..."
    open /Applications/Docker.app
    kubectl config use-context docker-for-desktop
}

wait_for_startup(){
    sleep 60
    condition=$(kubectl version | grep -i "Server Version:")
    counter=1

    log_info "Waiting for Docker for Desktop to start..."

    while [[ -z $condition && $counter -lt 30 ]]; do
        log_info "Waiting for 30 seconds to retry..."
        sleep 10
        counter=$((counter+1))
        condition=$(kubectl version | grep -i "Server Version:")
    done
}

start_docker
wait_for_startup

if [[ -z $condition ]]; then
    echo "Error while starting Docker for Desktop."
    exit 1
fi

log_info "Successfully started Docker for Desktop with a Kubernetes cluster"