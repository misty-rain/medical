package com.friendtime.http.builder;


import com.friendtime.http.request.RequestCall;

import java.util.Map;

/**
 * Created by wutao on 2016/2/1.
 * http 请求组装编译器抽象基类  主要包括 url、params、tag、headers
 */
public abstract class OKHttpRequestBuilder {
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, String> params;

    public abstract OKHttpRequestBuilder url(String url);

    public abstract OKHttpRequestBuilder tag(Object tag);

    public abstract OKHttpRequestBuilder params(Map<String, String> params);

    public abstract OKHttpRequestBuilder addParams(String key, String val);

    public abstract OKHttpRequestBuilder headers(Map<String, String> headers);

    public abstract OKHttpRequestBuilder addHeader(String key, String val);

    public abstract RequestCall build();
}
