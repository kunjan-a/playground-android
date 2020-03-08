package com.example.asyncHttp.proteus;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import java.util.Map;

public class ProteusRequestUrlBuilder
{
    private static Escaper _urlEscaper = UrlEscapers.urlFormParameterEscaper();

    public static <T> String getUrl(ProteusClient client, ProteusRequest<T> request)
    {
        return client.getRequestProtocol() + "://" +
               client.getEndpoint() + "/" +
               client.getAppDomain() + "/" +
               client.getTenant() + "/" +
               request.getTenantVersion() + "/" +
               request.getRequestSpecificPath() + "?" +
               getUrlParamsString(request.getUrlParams());
    }

    private static String getUrlParamsString(Map<String, String> urlParams)
    {
        StringBuilder urlParamsString = new StringBuilder("");
        for (String paramKey : urlParams.keySet()) {
            urlParamsString.append(_urlEscaper.escape(paramKey)).append("=")
                           .append(_urlEscaper.escape(urlParams.get(paramKey))).append("&");
        }
        if (urlParamsString.length() > 0) {
            return urlParamsString.substring(0, urlParamsString.length() - 1);
        } else {
            return "";
        }
    }
}
