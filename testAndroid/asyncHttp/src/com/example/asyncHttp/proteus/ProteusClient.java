package com.example.asyncHttp.proteus;

import android.util.Log;
import com.example.asyncHttp.config.ClientType;
import com.example.asyncHttp.config.Environment;
import com.example.asyncHttp.config.ProteusConfig;
import com.example.asyncHttp.proteus.error.ProteusCallFailedException;
import com.example.asyncHttp.proteus.error.ProteusInvalidResponseException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.myjson.Gson;
import com.google.myjson.JsonParseException;
import com.google.myjson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpParams;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;

public abstract class ProteusClient
{
    static final String REQUEST_PROTOCOL = "https";
    private static final String STAGING_ENDPOINT = "proxy-staging-external.handler.domain.to";
    private static final String PRODUCTION_ENDPOINT = "proxy.handler.domain.to";
    private static final String REQUEST_BODY_CONTENT_TYPE = "application/json";
    private static final String RESPONSE_BODY_ENCODING = "UTF-8";
    private static final String LOGTAG = "proteus";
    private String _appDomain;
    private String _endPoint;
    private AsyncHttpClient _asyncHttpClient;
    private Gson _gson;

    protected ProteusClient(ProteusConfig config)
    {
        //todo: take a config object and configure async http client
        _asyncHttpClient = new AsyncHttpClient();
/*
        final HttpClient httpClient = _asyncHttpClient.getHttpClient();

        final HttpParams httpParams = httpClient.getParams();
        httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
*/
        _asyncHttpClient.addHeader("Content-Type", getRequestContentType());
        _gson = new Gson();
        setEnvironment(config.getEnvironment());
        setAppDomain(config.getClientType());
    }

    public <T> ListenableFuture<T> makeRequest(final ProteusRequest<T> request)
    {
        final SettableFuture<T> future = SettableFuture.create();
        try {
            final String url = ProteusRequestUrlBuilder.getUrl(this, request);
            if (request.isMethodPost()) {
                if (getRequestContentType().equals(REQUEST_BODY_CONTENT_TYPE)) {
                    final String args = _gson.toJson(request);
                    Log.d(LOGTAG,"Making post req on "+url+", contentType:"+getRequestContentType()+", args:"+args);
                    _asyncHttpClient
                        .post(null, url, new StringEntity(args), getRequestContentType(),
                              getResponseHandler(request, future));
                } else {
                    RequestParams requestParams;
                    try {
                        requestParams = request.getRequestParams();
                        Log.d(LOGTAG,"Making post req on "+url+", contentType:"+getRequestContentType()+", args:"+requestParams.toString());
                    } catch (Exception e) {
                        Log.e(LOGTAG,"Exception while retrieving request params.", e);
                        future.setException(e);
                        return future;
                    }
                    _asyncHttpClient.post(url, requestParams, getResponseHandler(request, future));
                }

            } else {
                Log.d(LOGTAG,"Making get req on "+ url);
                Executors.newFixedThreadPool(1).submit(new Runnable() {
                    @Override
                    public void run() {
                        _asyncHttpClient.get(null, url, getResponseHandler(request, future));
                    }
                });
            }
            return future;
        } catch (UnsupportedEncodingException e) {
            //todo: put a warning log here
            future.setException(e);
            return future;
        }
    }

    protected String getRequestContentType()
    {
        return REQUEST_BODY_CONTENT_TYPE;
    }

    String getEndpoint()
    {
        return _endPoint;
    }

    String getRequestProtocol()
    {
        return REQUEST_PROTOCOL;
    }

    String getAppDomain()
    {
        return _appDomain;
    }

    protected void setAppDomain(ClientType clientType)
    {
        _appDomain = clientType.getDomainSpecificPath();
    }

    public void setAppDomain(String appDomain)
    {
        _appDomain = appDomain;
    }

    public void setEndPoint(String endPoint)
    {
        _endPoint = endPoint;
    }

    protected void setEnvironment(Environment environment)
    {
        switch (environment) {
        case PRODUCTION:
            _endPoint = PRODUCTION_ENDPOINT;
            break;
        case STAGING:
            _endPoint = STAGING_ENDPOINT;
            break;
        }
    }


    protected abstract String getTenant();

    private <T> BaseJsonHttpResponseHandler getResponseHandler(final ProteusRequest<T> request,
                                                               final SettableFuture<T> future)
    {
        return new BaseJsonHttpResponseHandler<T>(RESPONSE_BODY_ENCODING)
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody, T response)
            {
                Log.d(LOGTAG,"Proteus req successful. statusCode:"+statusCode+", responseBody:"+responseBody+", headers:"+headers);
                future.set(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  String responseBody, T errorResponse)
            {
                Log.d(LOGTAG,"Proteus req failed. statusCode:"+statusCode+", responseBody:"+responseBody+", headers:"+headers);
                Throwable error;
                if (e != null) {
                    if (e instanceof JsonParseException) {
                        error = new ProteusInvalidResponseException(responseBody);
                    } else {
                        if (e instanceof HttpResponseException) {
                            HttpResponseException e1 = (HttpResponseException) e;
                            error = request.httpError(e1.getStatusCode(), "");
                        } else {
                            error = new ProteusCallFailedException(e);
                        }
                    }
                } else {
                    error = request.httpError(statusCode, responseBody);
                }
                future.setException(error);
            }

            @Override
            protected T parseResponse(String responseBody)
                throws JsonParseException, JsonSyntaxException
            {
                return request.parseResponseJson(responseBody);
            }

            @Override
            protected void postRunnable(Runnable r)
            {
                if (r != null) {
                    r.run();
                }
            }
        };
    }
}
