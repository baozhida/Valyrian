package com.xxx.testcase;

/**
 * Copyright (C) 2006-2017 Tuniu All rights reserved
 */

/**
 * TODO: description
 * Date: 2017-08-02
 *
 * @author baozhida
 */

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.xxx.utils.http.client.HttpClientRequest;
import com.xxx.utils.http.client.HttpClientResponse;
import com.xxx.utils.http.client.HttpClientUtil;
import com.xxx.utils.http.client.RequestType;

import io.qameta.allure.Description;

public class HttpTest {
    //样板

    @Test
    @Description("Some detailed test description")
    public void testHttpGet() throws IOException {
        HttpClientRequest request = new HttpClientRequest();
        request.setType(RequestType.GET);

        //----普通的接口
        request.setUrl("http://xxx.com");
        Map<String,String> pMap = new HashMap<String, String>();
        pMap.put("1", "apis");
        Map<String,String> qMap = new HashMap<String, String>();
        qMap.put("code", "1101");
        request.setPath(pMap);
        request.setQuery(qMap);
        HttpClientResponse response = HttpClientUtil.sendRequest(request);
        System.out.println("111111  " + response.getStateCode());
        System.out.println("222222  "+request.getUrl());
        System.out.println("333333  "+response.getResponseBody().toString());



    }


}
