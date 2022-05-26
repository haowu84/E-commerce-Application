package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    public final IDMRepo repo;

    @Autowired
    public IDMAuthenticationManager(IDMRepo repo)
    {
        this.repo = repo;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    public User selectAndAuthenticateUser(String email, char[] password)
    {
        User user = repo.selectUser(email);
        byte[] encodedPassword = hashPassword(password, user.getSalt());
        String base64EncodedHashedPassword = Base64.getEncoder().encodeToString(encodedPassword);

        if (!base64EncodedHashedPassword.equals(user.getHashedPassword()))
        {
            throw new ResultError(IDMResults.INVALID_CREDENTIALS);
        }

        if (user.getUserStatus().value().equals("Locked"))
        {
            throw new ResultError(IDMResults.USER_IS_LOCKED);
        }

        if (user.getUserStatus().value().equals("Banned"))
        {
            throw new ResultError(IDMResults.USER_IS_BANNED);
        }

        user.setRoles(repo.selectRoles(user.getEmail()));

        return user;
    }

    public void createAndInsertUser(String email, char[] password)
    {
        byte[] salt = genSalt();
        byte[] encodedPassword = hashPassword(password, salt);

        User user = new User()
                .setEmail(email)
                .setUserStatus(UserStatus.ACTIVE)
                .setSalt(Base64.getEncoder().encodeToString(salt))
                .setHashedPassword(Base64.getEncoder().encodeToString(encodedPassword));

        repo.insertUser(user);
    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        repo.insertRefreshToken(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token)
    {
        RefreshToken refreshToken = repo.selectRefreshToken(token);

        if (refreshToken.getTokenStatus().value().equals("Expired"))
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        if (refreshToken.getTokenStatus().value().equals("Revoked"))
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        }

        return refreshToken;
    }

    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
        repo.updateRefreshTokenTime(token.getToken(), token.getExpireTime());
    }

    public void expireRefreshToken(RefreshToken token)
    {
        repo.updateRefreshTokenStatus(token.getToken(), 2);
    }

    public void revokeRefreshToken(RefreshToken token)
    {
        repo.updateRefreshTokenStatus(token.getToken(), 3);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {
        return repo.selectUserFromRefreshToken(refreshToken.getToken());
    }
}
