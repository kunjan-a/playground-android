package com.example.testAndroid.com.example.asyncHttp.zeus.Response;

import com.example.testAndroid.com.example.asyncHttp.zeus.ZeusApi;
import com.google.myjson.Gson;
import com.google.myjson.annotations.SerializedName;

public class CompleteProfile extends PublicProfile
{
    @SerializedName(ZeusApi.KEY_EMAILS)
    private VerifiableEmail[] _verifiableEmails;
    @SerializedName(ZeusApi.KEY_MOBILES)
    private VerifiableMobile[] _verifiableMobiles;

    public VerifiableEmail[] getVerifiableEmails()
    {
        return _verifiableEmails;
    }

    public VerifiableMobile[] getVerifiableMobiles()
    {
        return _verifiableMobiles;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this, CompleteProfile.class);
    }

    private abstract class Verifiable
    {
        @SerializedName(ZeusApi.KEY_IS_VERIFIED)
        boolean _isVerified;

        public boolean isVerified()
        {
            return _isVerified;
        }

    }

    public class VerifiableEmail extends Verifiable
    {
        @SerializedName(ZeusApi.KEY_EMAILID)
        String _emailId;

        public String getEmailId()
        {
            return _emailId;
        }

        @Override
        public String toString()
        {
            return new Gson().toJson(this, VerifiableEmail.class);
        }
    }

    public class VerifiableMobile extends Verifiable
    {
        @SerializedName(ZeusApi.KEY_COUNTRY_CODE)
        String _countryCode;
        @SerializedName(ZeusApi.KEY_MOBILE)
        String _localMobileNumber;

        public String getCountryCode()
        {
            return _countryCode;
        }

        public String getLocalMobileNumber()
        {
            return _localMobileNumber;
        }

        @Override
        public String toString()
        {
            return new Gson().toJson(this, VerifiableMobile.class);
        }
    }
}
