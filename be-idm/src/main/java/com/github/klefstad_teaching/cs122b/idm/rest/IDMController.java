package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.core.base.ResultResponse;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.model.request.AuthenticateRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.RefreshRequest;
import com.github.klefstad_teaching.cs122b.idm.model.request.RegisterAndLoginRequest;
import com.github.klefstad_teaching.cs122b.idm.model.response.LoginAndRefreshResponse;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }

    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(@RequestBody RegisterAndLoginRequest request)
    {
        validate.validateEmail(request.getEmail());
        validate.validatePassword(request.getPassword());

        authManager.createAndInsertUser(request.getEmail(), request.getPassword());

        return ResultResponse.response(IDMResults.USER_REGISTERED_SUCCESSFULLY);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginAndRefreshResponse> login(@RequestBody RegisterAndLoginRequest request)
    {
        validate.validateEmail(request.getEmail());
        validate.validatePassword(request.getPassword());

        User user = authManager.selectAndAuthenticateUser(request.getEmail(), request.getPassword());
        String accessToken = jwtManager.buildAccessToken(user);
        RefreshToken refreshToken = jwtManager.buildRefreshToken(user);
        authManager.insertRefreshToken(refreshToken);

        LoginAndRefreshResponse response = new LoginAndRefreshResponse()
                .setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken.getToken());

        return response.toResponse();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginAndRefreshResponse> refresh(@RequestBody RefreshRequest request)
    {
        validate.validateRefreshToken(request.getRefreshToken());

        RefreshToken refreshToken = authManager.verifyRefreshToken(request.getRefreshToken());

        if (jwtManager.hasExpired(refreshToken))
        {
            authManager.expireRefreshToken(refreshToken);
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        jwtManager.updateRefreshTokenExpireTime(refreshToken);

        User user = authManager.getUserFromRefreshToken(refreshToken);
        String accessToken = jwtManager.buildAccessToken(user);

        LoginAndRefreshResponse response = new LoginAndRefreshResponse()
                .setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN)
                .setAccessToken(accessToken);

        if (jwtManager.needsRefresh(refreshToken))
        {
            authManager.revokeRefreshToken(refreshToken);

            RefreshToken newRefreshToken = jwtManager.buildRefreshToken(user);
            authManager.insertRefreshToken(newRefreshToken);

            response.setRefreshToken(newRefreshToken.getToken());

            return response.toResponse();
        }

        authManager.updateRefreshTokenExpireTime(refreshToken);

        response.setRefreshToken(refreshToken.getToken());

        return response.toResponse();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ResultResponse> authenticate(@RequestBody AuthenticateRequest request)
    {
        jwtManager.verifyAccessToken(request.getAccessToken());

        return ResultResponse.response(IDMResults.ACCESS_TOKEN_IS_VALID);
    }
}
