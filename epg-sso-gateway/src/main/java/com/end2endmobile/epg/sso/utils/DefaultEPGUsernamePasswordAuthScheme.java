/*
 * This file is a part of End2End Payment Gateway (EPG).
 * Copyright (C) End2End VAS
 */
package com.end2endmobile.epg.sso.utils;

import com.end2endmobile.epg.core.domain.utils.PasswordUtils;
import org.apache.commons.lang.StringUtils;
import org.josso.auth.Credential;
import org.josso.auth.exceptions.SSOAuthenticationException;
import org.josso.auth.scheme.UsernamePasswordAuthScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default EPG username password authentication scheme.
 */
public class DefaultEPGUsernamePasswordAuthScheme extends UsernamePasswordAuthScheme {

    private static final Logger log = LoggerFactory.getLogger(DefaultEPGUsernamePasswordAuthScheme.class);

    public DefaultEPGUsernamePasswordAuthScheme() {
        super();
        setIgnorePasswordCase(Boolean.TRUE.toString());
        setIgnoreUserCase(Boolean.TRUE.toString());
    }

    public boolean authenticate() throws SSOAuthenticationException {
        log.debug("Authenticating user...");
        setAuthenticated(false);
        String username = getUsername(_inputCredentials);
        String password = getPassword(_inputCredentials);
        // Check if all credentials are present.
        if (StringUtils.isBlank(username)) {
            log.debug("Username not provided!");
            return false;
        }
        if (StringUtils.isBlank(password)) {
            log.debug("Password not provided!");
            return false;
        }
        String knownUsername = getUsername(getKnownCredentials());
        String expectedPassword = getPassword(getKnownCredentials());
        String salt = getSalt(getKnownCredentials());
        password = PasswordUtils.getPasswordHash(password, salt);
        if (!validateUsername(username, knownUsername) || !validatePassword(password, expectedPassword)) {
            return false;
        }
        log.debug("Principal authenticated: {}", username);
        setAuthenticated(true);
        return true;
    }

    protected String getSalt(Credential[] credentials) {
        SaltCredential c = getSaltCredential(credentials);
        if (c == null) return null;
        return (String) c.getValue();
    }

    protected SaltCredential getSaltCredential(Credential[] credentials) {
        for (Credential credential : credentials) {
            if (credential instanceof SaltCredential) {
                return (SaltCredential) credential;
            }
        }
        return null;
    }
}
