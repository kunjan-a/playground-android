package com.example.asyncHttp.proteus.error;

public class ProteusInvalidResponseException extends Exception
{
    private final String _responseBody;

    public ProteusInvalidResponseException(String responseBody)
    {
        super(responseBody);
        _responseBody = responseBody;
    }

    public String getResponseBody()
    {
        return _responseBody;
    }
}
