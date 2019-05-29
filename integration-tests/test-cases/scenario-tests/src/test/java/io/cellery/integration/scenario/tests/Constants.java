/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package io.cellery.integration.scenario.tests;

/**
 * This class with hold the constants defined in the test cases.
 */
public class Constants {

    private Constants() {
    }

    public static final String CELL_ORG_NAME = "wso2cellery";
    public static final String TEST_CELL_ORG_NAME = "wso2cellerytest";
    public static final String SAMPLE_CELLS_VERSION = "latest";
    public static final String HELLO_WORLD_WEB_CONTENT = "hello cellery";
    public static final String PET_STORE_WEB_CONTENT = "Pet Accessories";
    public static final String PET_STORE_SIGN_IN_WEB_CONTENT = "SIGN IN";
    public static final String IDENTITY_SERVER_HEADER = "OPENID USER CLAIMS";
    public static final String PET_STORE_PERSONAL_INFORMATION_HEADER = "Pet Store";
    public static final String DEFAULT_HELLO_WORLD_URL = "http://hello-world.com";
    public static final String DEFAULT_PET_STORE_URL = "http://pet-store.com";
    public static final String PET_STORE_XPATH_SIGN_USER_BUTTON = "//*[@id=\"app\"]/div/header/div/div/button";
    public static final String PET_STORE_XPATH_PREFERENCE_CHECKBOX_DOG = "//*[@id=\"app\"]/div/main/div/div/div/" +
            "div[3]/div/div/div/div/div[1]/label[1]/span[1]/span[1]/input";
    public static final String PET_STORE_XPATH_PREFERENCE_CHECKBOX_CAT = "//*[@id=\"app\"]/div/main/div/div/div/" +
            "div[3]/div/div/div/div/div[1]/label[2]/span[1]/span[1]/input";
    public static final String PET_STORE_XPATH_PREFERENCE_SUBMIT_BUTTON = "//*[@id=\"app\"]/div/main/div/div/div/" +
            "div[3]/div/div/div/div/div[2]/button[2]";
    public static final String PET_STORE_XPATH_PET_CARRIER_CAGE = "//*[@id=\"app\"]/div/main/div/div[2]/div/div[1]/" +
            "div/div[4]/div[2]/button";
    public static final String PET_STORE_XPATH_BONE_SHAPED_TOY = "//*[@id=\"app\"]/div/main/div/div[2]/div/div[4]/" +
            "div/div[4]/div[2]/button";
    public static final String PET_STORE_XPATH_SIGN_OUT_BUTTON = "//*[@id=\"user-info-appbar\"]/div[2]/ul/li[2]";
    public static final String PET_STORE_IDENTITY_SERVER_LOGOUT_HEADER = "OPENID CONNECT LOGOUT";
    public static final String PET_STORE_NO_ORDERS_PLACED = "No Orders Placed";
}
