package com.example.testAndroid.com.example.asyncHttp.zeus.Response;

import com.example.testAndroid.com.example.asyncHttp.zeus.ZeusApi;
import com.google.myjson.Gson;
import com.google.myjson.annotations.SerializedName;

public class PublicProfile
{
    @SerializedName(ZeusApi.KEY_NAME)
    private ZeusApi.Name _name;
    @SerializedName(ZeusApi.KEY_GUID)
    private String _guid;
    @SerializedName(ZeusApi.KEY_IMAGE_URI)
    private String _imageUri;
    @SerializedName(ZeusApi.KEY_VERSION)
    private String version;

    public ZeusApi.Name getName()
    {
        return _name;
    }

    public String getGuid()
    {
        return _guid;
    }

    public String getImageUri()
    {
        return _imageUri;
    }

    public String getVersion()
    {
        return version;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this, PublicProfile.class);
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

        PublicProfile that = (PublicProfile) o;

        if (_guid != null ? !_guid.equals(that._guid) : that._guid != null) {
            return false;
        }
        if (version != null ? !version.equals(that.version) : that.version != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _guid != null ? _guid.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}

