package com.example.asyncHttp.proteus;

import com.example.asyncHttp.proteus.error.ProteusCallFailedException;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;
import com.google.myjson.JsonSyntaxException;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

public abstract class ProteusRequest<T>
{
    protected transient Map<String, String> _urlParams;

    protected ProteusRequest()
    {
        _urlParams = new HashMap<String, String>();
    }

    protected boolean isMethodPost()
    {
        return true;
    }

    protected abstract String getTenantVersion();

    protected abstract String getRequestSpecificPath();

    protected abstract Class<T> getResponseClass();

    Map<String, String> getUrlParams()
    {
        return _urlParams;
    }

    public T parseResponseJson(String responseBody)
        throws JsonParseException, JsonSyntaxException
    {
        return new Gson().fromJson(responseBody, getResponseClass());
    }

    public Throwable httpError(int statusCode, String responseBody)
    {
        return new ProteusCallFailedException(statusCode, responseBody);
    }

    public RequestParams getRequestParams() throws Exception
    {
        return null;
    }
}