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

log_info() {
    echo "${log_prefix}[INFO]" $1
}   

cd $source_root
if [ $setup_type = "basic" ]; then
	log_info "Installing Cellery basic setup on Docker for Desktop..."
	cellery setup create existing --loadbalancer
else
    log_info "Installing Cellery complete setup on Docker for Desktop..."
	cellery setup create existing --loadbalancer --complete 
fi

log_info "Successfully installed cellery $setup_type setup on Docker for Desktop."
