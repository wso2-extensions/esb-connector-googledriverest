/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
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
 */

package org.wso2.carbon.connector.integration.test.googledriveRest;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GoogledriveRestConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap;
    private Map<String, String> apiRequestHeadersMap;
    private Map<String, String> headersMap = new HashMap<String, String>();
    private String apiEndpointUrl;
    private String apiEndpoint;
    private String multipartProxyUrl;
    private String accessToken;

    /**
     * Set up the environment.
     */
    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        init("googledriveRest-connector-1.0.0");
        esbRequestHeadersMap = new HashMap<String, String>();
        apiRequestHeadersMap = new HashMap<String, String>();
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/json");
        esbRequestHeadersMap.put("Accept", "application/json");

        apiEndpoint = "https://www.googleapis.com/oauth2/v3/token?grant_type=refresh_token&client_id="+
                connectorProperties.getProperty("clientId")+"&client_secret="+connectorProperties.getProperty("clientSecret")
                +"&refresh_token=" + connectorProperties.getProperty("refreshToken");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "POST", apiRequestHeadersMap);
        accessToken = apiRestResponse.getBody().getString("access_token");
        connectorProperties.put("accessToken", accessToken);

        apiRequestHeadersMap.put("Authorization", "Bearer " + accessToken);
        apiRequestHeadersMap.putAll(esbRequestHeadersMap);
        apiEndpointUrl = connectorProperties.getProperty("apiUrl") + "/drive/v2";

        String multipartPoxyName = connectorProperties.getProperty("multipartProxyName");
        multipartProxyUrl = getProxyServiceURL(multipartPoxyName);

    }

    /**
     * Positive test case for listFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description ="googleanalytics {listFile} integration test with mandatory parameters.")
    public void testListFileWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFile_mandatory.json");

        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind"), "drive#fileList");

    }

    /**
     * Positive test case for listFile method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description ="googleanalytics {listFile} integration test with optional parameters.")
    public void testListFileWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listFile_optional.json");

        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind"), "drive#fileList");
        String fileId = esbResponse.getJSONArray("items").getJSONObject(0).getString("id");
        connectorProperties.put("fileId", fileId);
    }

    /**
     * Positive test case for getFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getFile} integration test with mandatory parameters.",
            dependsOnMethods = {"testListFileWithOptionalParameters"})
    public void tesGetFileWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getFile_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind"), "drive#file");
    }

    /**
     * Positive test case for copyFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {copyFile} integration test with mandatory parameters.",
            dependsOnMethods = {"testListFileWithOptionalParameters"})
    public void tesCopyFileWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:copyFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_copyFile_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/copy";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind"), "drive#file");
    }

    /**
     * Positive test case for getInfo method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getInfo} integration test with mandatory parameters.")
    public void testGetInfoWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAbout");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInfo_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/about";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), apiResponse.getString("kind").toString());
    }

    /**
     * Positive test case for getInfo method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getInfo} integration test with optional parameters.")
    public void testGetInfoWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getAbout");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getInfo_optional.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/about?fields=kind,name";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("name").toString(), apiResponse.getString("name").toString());
    }

    /**
     * Positive test case for listChanges method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listChanges} integration test with mandatory parameters.")
    public void testListChangesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listChanges");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listChanges_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/changes";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#changeList");
        String changeId = esbResponse.getJSONArray("items").getJSONObject(0).getString("id");
        connectorProperties.put("changeId", changeId);

    }

    /**
     * Positive test case for getChanges method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getChanges} integration test with mandatory parameters.", dependsOnMethods = {
            "testListChangesWithMandatoryParameters"})
    public void testGetChangesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getChanges");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getChanges_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/changes/" + connectorProperties.getProperty("changeId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#change");
    }

    /**
     * Positive test case for listChildren method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listChildren} integration test with mandatory parameters.")
    public void testListChildrenWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listChildren");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listChildren_mandatory.json");

        final JSONObject esbResponse = esbRestResponse.getBody();

        final String apiEndpoint = apiEndpointUrl + "/files/root/children";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#childList");
        String childId = esbResponse.getJSONArray("items").getJSONObject(0).getString("id");
        connectorProperties.put("childId", childId);

    }

    /**
     * Positive test case for getChild method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getChild} integration test with mandatory parameters.")
    public void testGetChildWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getChild");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getChild_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl +"/files/root/children/" + connectorProperties.getProperty("childId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#childReference");
    }

    /**
     * Positive test case for getChild method with negative parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getChild} integration test with negative parameters.")
    public void testGetChildWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getChild");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getChild_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/root/children/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }

    /**
     * Positive test case for insertChild method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {insertChild} integration test with mandatory parameters.")
    public void testInsertChildWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertChild");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertChild_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#childReference");
    }

    /**
     * Positive test case for deleteChild method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {deleteChild} integration test with mandatory parameters.",
            dependsOnMethods = {"testInsertChildWithMandatoryParameters"})
    public void testDeleteChildWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteChild");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteChild_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#childReference");
    }

    /**
     * Positive test case for deleteChild method with negative parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {deleteChild} integration test with negative parameters.")
    public void testDeleteChildWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteChild");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteChild_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for listComments method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listComments} integration test with mandatory parameters.")
    public void testListCommentsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listComments_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/comments";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#commentList");
        String commentId = esbResponse.getJSONArray("items").getJSONObject(0).getString("commentId").toString();
        connectorProperties.put("commentId", commentId);
    }

    /**
     * Positive test case for getComments method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getComments} integration test with mandatory parameters.",
            dependsOnMethods = {"testListCommentsWithMandatoryParameters"})
    public void testGetCommentsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getComments");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getComments_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint =apiEndpointUrl+"/files/"+connectorProperties.getProperty("fileId")+"/comments/"+
                connectorProperties.getProperty("commentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#commentList");
    }

    /**
     * Positive test case for createComment method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {createComments} integration test with mandatory parameters.")
    public void testCreateCommentsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:createComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createComments_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint =apiEndpointUrl+"/files/"+connectorProperties.getProperty("fileId")+"/comments";
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#comment");
        String commentIdCreated = esbResponse.getString("commentId").toString();
        connectorProperties.put("commentIdCreated", commentIdCreated);
    }

    /**
     * Positive test case for updateComment method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {updateComment} integration test with mandatory parameters.", dependsOnMethods = {"testCreateCommentsWithMandatoryParameters"})
    public void testUpdateCommentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateComment_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#commentList");
    }

    /**
     * Positive test case for deleteComment method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {deleteComment} integration test with mandatory parameters.",
            dependsOnMethods = {"testUpdateCommentWithMandatoryParameters"})
    public void testDeleteCommentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteComment");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteComment_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#commentList");
    }

    /**
     * Positive test case for listParent method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listParent} integration test with mandatory parameters.")
    public void testListParentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listParents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listParent_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("folderId") + "/parents/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#parentList");
        String parentId = esbResponse.getJSONArray("items").getJSONObject(0).getString("id");
        connectorProperties.put("parentId", parentId);
    }

    /**
     * Negative test case for listParent method with Negative parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listParent} integration test with Negative parameters.")
    public void testListParentWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listParents");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listParent_negative.json");

        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/invalid/parents/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }

    /**
     * Positive test case for getParent method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getParent} integration test with mandatory parameters.",
            dependsOnMethods = {"testListParentWithMandatoryParameters"})
    public void testGetParentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getParent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getParent_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("folderId") + "/parents/" + connectorProperties.getProperty("parentId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#parentReference");
    }

    /**
     * Negative test case for getParent method with Negative parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getParent} integration test with Negative parameters.")
    public void testGetParentWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getParent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getParent_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("folderId") + "/parents/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }

    /**
     * Positive test case for createParent method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {createParent} integration test with mandatory parameters.")
    public void testCreateParentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertParent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createParent_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/parents";
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#parentReference");
        String parentIdCreated = esbResponse.getString("id");
        connectorProperties.put("parentIdCreated", parentIdCreated);
    }

    /**
     * Negative test case for createParent method with Negative parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {createParent} integration test with Negative parameters.")
    public void testCreateParentWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertParent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createParent_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for deleteParent method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {deleteParent} integration test with mandatory parameters.",
            dependsOnMethods = {"testCreateParentWithMandatoryParameters"})
    public void testDeleteParentWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteParent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteParent_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/parents" + connectorProperties.getProperty("parentIdCreated");
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Negative test case for deleteParent method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {deleteParent} integration test with Negative parameters.")
    public void testDeleteParentWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteParent");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteParent_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for listPermission method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listPermissions} integration test with mandatory parameters.")
    public void testListPermissionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listPermissions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPermissions_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/permissions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#permissionList");
        String permissionId = esbResponse.getJSONArray("items").getJSONObject(0).getString("id");
        connectorProperties.put("permissionId", permissionId);
    }

    /**
     * Positive test case for updatePermission method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {updatePermission} integration test with mandatory parameters.",
            dependsOnMethods = {"testListPermissionWithMandatoryParameters"})
    public void testUpdatePermissionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updatePermission");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updatePermission_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/permissions/" + connectorProperties.getProperty("permissionId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "PUT", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#permission");
    }

    /**
     * Negative test case for listPermission method with negative parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listPermissions} integration test with negative parameters.")
    public void testListPermissionWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listPermissions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listPermissions_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/invalid/permissions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }

    /**
     * Positive test case for getPermission method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getPermission} integration test with mandatory parameters.",
            dependsOnMethods = {"testListPermissionWithMandatoryParameters"})
    public void testGetPermissionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getPermissions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPermission_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/permissions/" + connectorProperties.getProperty("permissionId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#permission");
    }

    /**
     * Negative test case for getPermission method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getPermission} integration test with Negative parameters.")
    public void testGetPermissionWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getPermissions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getPermission_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/permissions/invalid";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }

    /**
     * Positive test case for createPermission method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {createPermission} integration test with mandatory parameters.")
    public void testInsertPermissionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertPermission");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createPermission_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#permission");
    }

    /**
     * Positive test case for deletePermission method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {deletePermission} integration test with mandatory parameters.",
            dependsOnMethods = {"testInsertPermissionWithMandatoryParameters"})
    public void testDeletePermissionWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deletePermissions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deletePermission_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }


    /**
     * Positive test case for getIdForEmail method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {getIdForEmail} integration test with mandatory parameters.")
    public void testGetIdForEmailWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getIdForEmail");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getIdForEmail_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/permissionIds/" + connectorProperties.getProperty("email");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#permissionId");
    }

    /**
     * Positive test case for listProperties method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listProperties} integration test with mandatory parameters.")
    public void testListPropertiesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listProperties");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProperties_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/properties";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#propertyList");
    }

    /**
     * Positive test case for gettProperties method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {gettProperties} integration test with mandatory parameters.",
            dependsOnMethods = {"testCreatePropertiesWithMandatoryParameters"})
    public void testGettPropertiesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getProperties");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listProperties_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/properties" + connectorProperties.getProperty("propertyId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#propertyList");
    }

    /**
     * Positive test case for createProperties method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {createProperties} integration test with mandatory parameters.")
    public void testCreatePropertiesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertProperties");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_createProperties_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/properties";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#property");
        String propertyKey = esbResponse.getString("key");
        connectorProperties.put("propertyKey", propertyKey);
    }

    /**
     * Positive test case for deleteProperties method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {deleteProperties} integration test with mandatory parameters.",
            dependsOnMethods = {"testCreatePropertiesWithMandatoryParameters"})
    public void testDeletePropertiesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteProperties");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteProperties_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for gettProperties method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listRevisions} integration test with mandatory parameters.")
    public void testListRevisionsWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listRevisions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRevisions_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/revisions/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#revisionList");
    }

    /**
     * Negative test case for gettProperties method with negative parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = true, description = "googleanalytics {listRevisions} integration test with negative parameters.")
    public void testListRevisionsWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listRevisions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRevisions_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/invalid/revisions/";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), apiRestResponse.getHttpStatusCode());
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("reason"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("reason"));
        Assert.assertEquals(esbRestResponse.getBody().getJSONObject("error").getJSONArray("errors").getJSONObject(0)
                .getString("message"), apiRestResponse.getBody().getJSONObject("error").getJSONArray("errors")
                .getJSONObject(0).getString("message"));
    }

    /**
     * Positive test case for listProperties method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, enabled = false, description = "googleanalytics {listProperties} integration test with mandatory parameters.",
            dependsOnMethods = {"testCreatePropertiesWithMandatoryParameters"})
    public void testUpdatePropertiesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateProperties");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateProperties_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/properties";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#propertyList");
    }

    /**
     * Positive test case for listReplies method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {listReplies} integration test with mandatory parameters.",
            dependsOnMethods = {"testListCommentsWithMandatoryParameters"})
    public void testListRepliesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listReplies");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listReplies_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/comments/" + connectorProperties.getProperty("commentId") + "replies";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#commentReplyList");
        String replyId = esbResponse.getJSONArray("items").getJSONObject(0).getString("replyId");
        connectorProperties.put("replyId", replyId);
    }

    /**
     * Positive test case for getReplies method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {getReplies} integration test with mandatory parameters.",
            dependsOnMethods = {"testListRepliesWithMandatoryParameters"})
    public void testGetRepliesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getReplies");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getReplies_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/comments/" + connectorProperties.getProperty("commentId") + "replies" + connectorProperties.getProperty("replyId");
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#commentReply");
    }

    /**
     * Positive test case for insertReplies method with mandatory parameters. ///Error
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {insertReplies} integration test with mandatory parameters",
            dependsOnMethods = {"testListCommentsWithMandatoryParameters"})
    public void testInsertRepliesWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertReplies");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertReplies_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for getRealtime method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {getRealtime} integration test with mandatory parameters.")
    public void testGetRealTimeWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getRealTime");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRealTime_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }

    /**
     * Positive test case for listRevision method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {listRevision} integration test with mandatory parameters.")
    public void testListRevisionTimeWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:listRevisions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_listRevision_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        final String apiEndpoint = apiEndpointUrl + "/files/" + connectorProperties.getProperty("fileId") + "/revisions";
        RestResponse<JSONObject> apiRestResponse = sendJsonRestRequest(apiEndpoint, "GET", apiRequestHeadersMap);
        final JSONObject apiResponse = apiRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#revisionList");
        String revisionId = esbResponse.getJSONArray("items").getJSONObject(0).getString("id");
        connectorProperties.put("revisionId", revisionId);
    }

    /**
     * Positive test case for getRevision method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {getRevisions} integration test with mandatory parameters.",
            dependsOnMethods = {"testListRevisionTimeWithMandatoryParameters"})
    public void testGetRevisionTimeWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:getRevisions");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_getRevision_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#revision");
    }

    /**
     * Positive test case for updateRevision method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {updateRevision} integration test with mandatory parameters.",
            dependsOnMethods = {"testListRevisionTimeWithMandatoryParameters"})
    public void testUpdateRevisionTimeWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateRevision");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateRevision_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#revision");
    }


    /**
     * Positive test case for insertFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {insertFile} integration test with mandatory parameters")
    public void testInsertFileWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertFile_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#file");
        String idToUpdate = esbResponse.getString("id").toString();
        connectorProperties.put("idToUpdate", idToUpdate);
    }

    /**
     * Positive test case for insertFile method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {insertFile} integration test with optional parameters")
    public void testInsertFileWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertFile_optional.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#file");
    }

    /**
     * Negative test case for insertFile method .
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {insertFile} integration test with Negative parameters")
    public void testInsertFileWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:insertFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_insertFile_Negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 400);
    }

    /**
     * Positive test case for uploadFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {uploadFile} integration test with mandatory parameters")
    public void testUploadFileWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:uploadFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_UploadFile_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#file");
        String idToDelete = esbResponse.getString("id").toString();
        connectorProperties.put("idToDelete", idToDelete);
    }

    /**
     * Positive test case for uploadFile method with optional parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {uploadFile} integration test with optional parameters")
    public void testUploadFileWithOptionalParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:uploadFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_UploadFile_optional.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#file");
    }

    /**
     * Positive test case for deleteFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {deleteFile} integration test with mandatory parameters",
            dependsOnMethods = {"testUploadFileWithMandatoryParameters"})
    public void testDeleteFileWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFile_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 204);
    }

    /**
     * Negative test case for deleteFile method.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {deleteFile} integration test with negative parameters",
            dependsOnMethods = {"testUploadFileWithMandatoryParameters"})
    public void testDeleteFileWithNegativeParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:deleteFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_deleteFile_negative.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 404);
    }

    /**
     * Positive test case for updateInsertFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {updateInsertFile} integration test with mandatory parameters",
            dependsOnMethods = {"testInsertFileWithMandatoryParameters"})
    public void testupdateInsertFileWithMandatoryParameters() throws IOException, JSONException {

        esbRequestHeadersMap.put("Action", "urn:updateInsertFile");
        RestResponse<JSONObject> esbRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", esbRequestHeadersMap, "esb_updateInsertFile_mandatory.json");
        final JSONObject esbResponse = esbRestResponse.getBody();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
        Assert.assertEquals(esbResponse.getString("kind").toString(), "drive#file");
    }

    /**
     * Positive test case for uploadFile method with mandatory parameters.
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test(groups = {"wso2.esb"}, description = "googleanalytics {uploadFile} integration test with Negative parameters")
    public void testuploadFileWithMandatoryParameters() throws IOException, JSONException {

        headersMap.put("Authorization", "Bearer " + accessToken);
        String esbEndPoint = multipartProxyUrl;
        MultipartFormdataProcessor multipartProcessor = new MultipartFormdataProcessor(esbEndPoint, headersMap);
        multipartProcessor.addFileToRequest("a", connectorProperties.getProperty("uploadFileName"), null, connectorProperties.getProperty("targetFileName"));
        RestResponse<JSONObject> esbRestResponse = multipartProcessor.processForJsonResponse();
        Assert.assertEquals(esbRestResponse.getHttpStatusCode(), 200);
    }
}