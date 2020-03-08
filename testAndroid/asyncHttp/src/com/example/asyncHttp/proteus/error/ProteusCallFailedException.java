package com.example.asyncHttp.proteus.error;

public class ProteusCallFailedException extends Exception
{
    public ProteusCallFailedException()
    {
        super();
    }

    public ProteusCallFailedException(Throwable e)
    {
        super(e);
    }

    public ProteusCallFailedException(int statusCode, String responseBody)
    {
        super("{statusCode:" + statusCode + ", responseBody:" + responseBody + "}");
    }
}
