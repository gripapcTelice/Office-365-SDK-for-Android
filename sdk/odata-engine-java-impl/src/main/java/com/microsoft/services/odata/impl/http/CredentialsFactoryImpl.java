package com.microsoft.services.odata.impl.http;

import com.microsoft.services.odata.interfaces.Credentials;
import com.microsoft.services.odata.interfaces.CredentialsFactory;

/**
 * The type Credentials factory impl.
 */
public class CredentialsFactoryImpl implements CredentialsFactory {

    private Credentials mCredentials;

    @Override
    public Credentials getCredentials() {
        return mCredentials;
    }

    /**
     * Set credentials.
     *
     * @param credentials the credentials
     */
    public void setCredentials(Credentials credentials){
        mCredentials = credentials;
    }
}
