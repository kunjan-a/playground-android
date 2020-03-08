package com.example.testAndroid.com.example.asyncHttp.zeus.Response;

import com.example.testAndroid.com.example.asyncHttp.zeus.ZeusApi;
import com.google.myjson.Gson;
import com.google.myjson.annotations.SerializedName;

public class AuthToken
{
    @SerializedName(ZeusApi.KEY_TOKEN)
    private String _token;
    @SerializedName(ZeusApi.KEY_EXPIRATION_MSEC)
    private long _expirationMsec;

    public String getToken()
    {
        return _token;
    }

    public long getExpirationMsec()
    {
        return _expirationMsec;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this, AuthToken.class);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AuthToken authToken = (AuthToken) o;

        if (_expirationMsec != authToken._expirationMsec) {
            return false;
        }
        return _token.equals(authToken._token);

    }

    @Override
    public int hashCode()
    {
        int result = _token.hashCode();
        result = 31 * result + (int) (_expirationMsec ^ (_expirationMsec >>> 32));
        return result;
    }
}

