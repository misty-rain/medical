package com.friendtime.http.builder;



import com.friendtime.http.config.FileInput;
import com.friendtime.http.request.OKHttpPostFormRequest;
import com.friendtime.http.request.RequestCall;
import com.friendtime.http.utils.Logger;
import com.friendtime.http.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wutao on 2016/2/1.
 * post 表单式请求组装器
 */
public class OKHttpPostFormBuilder extends OKHttpRequestBuilder {

    private final String TAG = OKHttpPostFormBuilder.class.getSimpleName();
    private List<FileInput> files = new ArrayList<>();

    @Override
    public RequestCall build() {
        Logger.d(TAG, Utils.parseParamsStr(url,params));
        return new OKHttpPostFormRequest(url, tag, params, headers, files).build();
    }

    public OKHttpPostFormBuilder addFile(List<FileInput> list) {
       this.files = list;
        return this;
    }

    @Override
    public OKHttpPostFormBuilder url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public OKHttpPostFormBuilder tag(Object tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public OKHttpPostFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public OKHttpPostFormBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public OKHttpPostFormBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }


    @Override
    public OKHttpPostFormBuilder addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }

}
