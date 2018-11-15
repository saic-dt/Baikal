/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datatech.baikal.task.util;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility class providing methods to call Restful Service provided by frontend web application for
 * storage of monitoring status.
 */
@Service
public class RestfulUtil {
    private static final Logger logger = LoggerFactory.getLogger(RestfulUtil.class);

    @Value("${restservice.url:@null}")
    private String restUrl;

    @Autowired
    private ZkHandler handler;

    public void callRestService(String url, NameValuePair[] data) {
        logger.info("callRestService  restful URL: [{}]", restUrl);
        HttpClient httpClient = new HttpClient();
        String loginUrl = restUrl + "loginByUser";
        PostMethod postMethod = new PostMethod(loginUrl);
        try {
            // prepare request body with username and password
            String password = checkIsNull(getPassword());
            password = Md5Util.getMd5(Md5Util.getMd5("taskClient" + password));
            NameValuePair[] data1 = { new NameValuePair("username", "admin"),
                    new NameValuePair("fromClient", "taskClient"), new NameValuePair("password", password) };
            postMethod.setRequestBody(data1);
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            httpClient.executeMethod(postMethod);

            String text = postMethod.getResponseBodyAsString();
            logger.info("login response body [{}]", text);

            // get cookie after successful login
            Cookie[] cookies = httpClient.getState().getCookies();
            StringBuffer tmpcookies = new StringBuffer();
            for (Cookie c : cookies) {
                tmpcookies.append(c.toString() + ";");
            }
            logger.info("cookies [{}]", tmpcookies.toString());
            // perform next step after login
            postMethod = new PostMethod(url);
            postMethod.setRequestBody(data);
            // set cookie for next request
            postMethod.setRequestHeader("cookie", tmpcookies.toString());
            postMethod.setRequestHeader("Referer", loginUrl);
            postMethod.setRequestHeader("User-Agent", "www Spot");
            httpClient.executeMethod(postMethod);

            text = postMethod.getResponseBodyAsString();
            logger.info("post response body [{}]", text);
            Gson gson = new Gson();
            ResponseObj robj = gson.fromJson(text, ResponseObj.class);
            logger.info("response status success: [{}]", robj.success);
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
        }
    }

    public String getUrl(String url) {
        return restUrl + url;
    }

    private String getPassword() throws Exception {
        String userPath = handler.getAdminPath();
        String userValue = Bytes.toString(handler.getClient().getData().forPath(userPath));
        JsonParser parser = new JsonParser();
        JsonObject jobj = parser.parse(userValue).getAsJsonObject();
        String password = jobj.get("PASSWORD").toString();
        return password;
    }

    private String checkIsNull(String str) {
        return ((str == null) ? "" : str.replaceAll("\"", ""));
    }

    private class ResponseObj {
        String success;
        String message;
        String timestamp;
        String resultCode;
        String data;
    }

}
