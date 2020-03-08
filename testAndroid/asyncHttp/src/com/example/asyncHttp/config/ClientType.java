package com.example.asyncHttp.config;

public enum ClientType
{
    GO_TO("go.to"), TALK_TO("domain.to");

    private final String _domainSpecificPath;

    ClientType(String domainSpecificPath)
    {
        _domainSpecificPath = domainSpecificPath;
    }

    public String getDomainSpecificPath()
    {
        return _domainSpecificPath;
    }
}
