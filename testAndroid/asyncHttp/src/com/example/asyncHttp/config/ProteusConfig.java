package com.example.asyncHttp.config;

public class ProteusConfig
{
    public Environment getEnvironment()
    {
        return _environment;
    }

    private final ClientType _clientType;
    private final Environment _environment;

    public ClientType getClientType()
    {
        return _clientType;
    }

    public ProteusConfig(ClientType clientType, Environment environment)
    {
        _clientType = clientType;
        _environment = environment;
    }
}
