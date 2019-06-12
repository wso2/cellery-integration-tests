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
package io.cellery.integration.scenario.tests.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This class is used to send http requests.
 */
public class HttpClient {
    /**
     * Send an http get request and get the response.
     *
     * @param url     Url to which the request is being sent
     * @param headers A hash map containing header keys and values
     * @return response message
     * @throws IOException              exception if http connection fails
     * @throws KeyManagementException   if TLS verification skip fails
     * @throws NoSuchAlgorithmException if TLS verification skip fails
     */
    public static String sendGet(String url, Map<String, String> headers) throws IOException
            , KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection con = null;
        // Skipping TLS verification since certificate is self-signed
        trustAllCerts();
        try {
            URL getUrl = new URL(url);
            con = (HttpsURLConnection) getUrl.openConnection();
            con.setRequestMethod("GET");
            StringBuilder content;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()
                    , StandardCharsets.UTF_8))) {
                String line;
                content = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            return content.toString();
        } finally {
            assert con != null;
            con.disconnect();
        }
    }

    /**
     * Send an http post request and get the response.
     *
     * @param url     Url to which the request is being sent
     * @param payload String
     *                Url parameters
     * @param headers A hash map containing header keys and values
     * @return response message
     * @throws IOException              exception if http connection fails
     * @throws KeyManagementException   if TLS verification skip fails
     * @throws NoSuchAlgorithmException if TLS verification skip fails
     */
    public static String sendPost(String url, String payload, Map<String, String> headers)
            throws IOException, KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection con = null;
        // Skipping TLS verification since certificate is self-signed
        trustAllCerts();
        try {
            URL postUrl = new URL(url);
            con = (HttpsURLConnection) postUrl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                byte[] postData;
                if (payload != null && !payload.isEmpty()) {
                    postData = payload.getBytes(StandardCharsets.UTF_8);
                    wr.write(postData);
                }
            }
            StringBuilder content;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()
                    , StandardCharsets.UTF_8))) {
                String line;
                content = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            return content.toString();
        } finally {
            assert con != null;
            con.disconnect();
        }
    }

    /**
     * Skip TLS validation.
     *
     * @throws NoSuchAlgorithmException if TLS verification skip fails
     * @throws KeyManagementException   if TLS verification skip fails
     */
    private static void trustAllCerts() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}
