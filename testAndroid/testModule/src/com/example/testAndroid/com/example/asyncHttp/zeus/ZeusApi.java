package com.example.testAndroid.com.example.asyncHttp.zeus;

import com.google.common.base.Strings;
import com.google.myjson.Gson;
import com.google.myjson.annotations.SerializedName;

import java.util.regex.Pattern;

/**
 * This class contains all the Zeus api specific details as maintained on CR
 *
 * @see <a href="https://c.internal.domain.com/display/TA/Zeus+proxy">Zeus HTTP Proxy API</a>
 */
public abstract class ZeusApi
{
    public static final String KEY_NAME = "name";
    public static final String KEY_FNAME = "firstName";
    public static final String KEY_LNAME = "lastName";
    public static final String KEY_EMAILS = "emails";
    public static final String KEY_MOBILES = "mobiles";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_GUID = "guid";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_IMAGE_URI = "imageUri";
    public static final String KEY_VERSION = "version";
    public static final String KEY_EMAILID = "email";
    public static final String KEY_IS_VERIFIED = "isVerified";
    public static final String KEY_COUNTRY_CODE = "countryCode";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_EXPIRATION_MSEC = "expirationMsec";

    public static class EmailId
    {
        private static final Pattern _basicEmailIdPattern = Pattern
            .compile("[^@|^\\ |]+@[^@|^\\ ]+\\.[^@|^\\ ]+");

        @SerializedName("domainName")
        private final String _domainName;

        @SerializedName("userName")
        private String _username;

        /**
         * @throws InvalidEmailIdException if email does not meet the pattern something@something.something
         */
        public EmailId(String email) throws InvalidEmailIdException
        {
            if (!_basicEmailIdPattern.matcher(email).matches()) {
                throw new InvalidEmailIdException("Invalid email id. email:" + email);
            }
            final String[] parts = email.split("@", 2);
            _username = parts[0];
            _domainName = parts[1];
        }

        public EmailId(String username, String domainName)
        {
            if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(domainName)) {
                throw new IllegalArgumentException(
                    "All arguments need to be non-empty strings. username:" + username +
                    " domainName:" + domainName);
            }
            this._username = username;
            this._domainName = domainName;
        }

        public String getUsername()
        {
            return _username;
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

            EmailId emailId = (EmailId) o;

            if (!_domainName.equals(emailId._domainName)) {
                return false;
            }
            if (!_username.equals(emailId._username)) {
                return false;
            }

            return true;
        }

        public String getEmailString()
        {
            return _username + '@' + _domainName;
        }

        public String getDomainName()
        {
            return _domainName;
        }

        public static class InvalidEmailIdException extends Exception
        {
            public InvalidEmailIdException(String invalidEmailId)
            {
                super("Invalid email id:" + invalidEmailId);
            }
        }

        @Override
        public int hashCode()
        {
            int result = _domainName.hashCode();
            result = 31 * result + _username.hashCode();
            return result;
        }


        @Override
        public String toString()
        {
            return getEmailString();
        }


    }

    public static class Name
    {
        @SerializedName(KEY_FNAME)
        private String _firstName;
        @SerializedName(KEY_LNAME)
        private String _lastName;

        public Name(String firstName, String lastName)
        {
            this._firstName = firstName;
            this._lastName = lastName;
        }

        public String getFirstName()
        {
            return _firstName;
        }

        public void setFirstName(String firstName)
        {
            this._firstName = firstName;
        }

        public String getLastName()
        {
            return _lastName;
        }

        public void setLastName(String lastName)
        {
            this._lastName = lastName;
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

            Name name = (Name) o;

            if (_firstName != null ? !_firstName.equals(name._firstName) :
                name._firstName != null) {
                return false;
            }
            if (_lastName != null ? !_lastName.equals(name._lastName) : name._lastName != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = _firstName != null ? _firstName.hashCode() : 0;
            result = 31 * result + (_lastName != null ? _lastName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            return new Gson().toJson(this, Name.class);
        }
    }

}
