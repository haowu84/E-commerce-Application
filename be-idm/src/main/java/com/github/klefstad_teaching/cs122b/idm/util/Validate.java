package com.github.klefstad_teaching.cs122b.idm.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
public final class Validate
{
    public void validateEmail(String email)
    {
        if (!(6 <= email.length() && email.length() <= 32))
        {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        }

        Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)@([a-zA-Z0-9]+)[.]([a-zA-Z0-9]+)");
        if (!pattern.matcher(email).matches())
        {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
        }
    }

    public void validatePassword(char[] password)
    {
        if (!(10 <= String.valueOf(password).length() && String.valueOf(password).length() <= 20))
        {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);
        }

        Pattern pattern = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).*");
        if (! pattern.matcher(String.valueOf(password)).matches())
        {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }
    }

    public void validateRefreshToken(String refreshToken)
    {
        if (!(refreshToken.length() == 36))
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_LENGTH);
        }

        try
        {
            UUID.fromString(refreshToken);
        }
        catch (IllegalArgumentException exception)
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_FORMAT);
        }
    }
}
