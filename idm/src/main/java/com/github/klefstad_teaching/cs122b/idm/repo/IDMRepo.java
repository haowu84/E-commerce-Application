package com.github.klefstad_teaching.cs122b.idm.repo;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.Role;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.List;

@Component
public class IDMRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public IDMRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public void insertUser(User user)
    {
        try
        {
            template.update(
                    "INSERT INTO idm.user (email, user_status_id, salt, hashed_password) " +
                            "Values (:email, :user_status_id, :salt, :hashed_password);",
                    new MapSqlParameterSource()
                            .addValue("email", user.getEmail(), Types.VARCHAR)
                            .addValue("user_status_id", user.getUserStatus().id(), Types.INTEGER)
                            .addValue("salt", user.getSalt(), Types.CHAR)
                            .addValue("hashed_password", user.getHashedPassword(), Types.CHAR)
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
        }
    }

    public User selectUser(String email)
    {
        try
        {
            return template.queryForObject(
                    "SELECT id, email, user_status_id, salt, hashed_password " +
                            "FROM idm.user " +
                            "WHERE email = :email;",
                    new MapSqlParameterSource()
                            .addValue("email", email, Types.VARCHAR),
                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }

    }

    public User selectUserFromRefreshToken(String token)
    {
        try
        {
            return template.queryForObject(
                    "SELECT u.id AS id, u.email, u.user_status_id, u.salt, u.hashed_password " +
                            "FROM idm.user u " +
                            "JOIN idm.refresh_token r ON u.id = r.user_id " +
                            "WHERE r.token = :token;",
                    new MapSqlParameterSource()
                            .addValue("token", token, Types.CHAR),
                    (rs, rowNum) ->
                            new User()
                                    .setId(rs.getInt("id"))
                                    .setEmail(rs.getString("email"))
                                    .setUserStatus(UserStatus.fromId(rs.getInt("user_status_id")))
                                    .setSalt(rs.getString("salt"))
                                    .setHashedPassword(rs.getString("hashed_password"))
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(IDMResults.USER_NOT_FOUND);
        }

    }

    public List<Role> selectRoles(String email)
    {
        return template.query(
                "SELECT r.name " +
                "FROM idm.user u " +
                "JOIN idm.user_role ur ON u.id = ur.user_id " +
                "JOIN idm.role r ON ur.role_id = r.id " +
                "WHERE u.email = :email;",
                new MapSqlParameterSource()
                        .addValue("email", email, Types.VARCHAR),
                (rs, rowNum) ->
                        Role.creator(rs.getString("name"))
        );
    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        template.update(
                "INSERT INTO idm.refresh_token (token, user_id, token_status_id, expire_time, max_life_time) " +
                        "Values (:token, :user_id ,:token_status_id, :expire_time, :max_life_time);",
                new MapSqlParameterSource()
                        .addValue("token", refreshToken.getToken(), Types.CHAR)
                        .addValue("user_id", refreshToken.getUserId(), Types.INTEGER)
                        .addValue("token_status_id", refreshToken.getTokenStatus().id(), Types.INTEGER)
                        .addValue("expire_time", Timestamp.from(refreshToken.getExpireTime()), Types.TIMESTAMP)
                        .addValue("max_life_time", Timestamp.from(refreshToken.getMaxLifeTime()), Types.TIMESTAMP)
        );
    }

    public RefreshToken selectRefreshToken(String token)
    {
        try
        {
            return template.queryForObject(
                    "SELECT id, token, user_id, token_status_id, expire_time, max_life_time " +
                            "FROM idm.refresh_token " +
                            "WHERE token = :token;",
                    new MapSqlParameterSource()
                            .addValue("token", token, Types.CHAR),
                    (rs, rowNum) ->
                            new RefreshToken()
                                    .setId(rs.getInt("id"))
                                    .setToken(rs.getString("token"))
                                    .setUserId(rs.getInt("user_id"))
                                    .setTokenStatus(TokenStatus.fromId(rs.getInt("token_status_id")))
                                    .setExpireTime(rs.getTimestamp("expire_time").toInstant())
                                    .setMaxLifeTime(rs.getTimestamp("max_life_time").toInstant())
            );
        }
        catch (DataAccessException e)
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    public void updateRefreshTokenTime(String token, Instant expire_time)
    {
        template.update(
                "UPDATE idm.refresh_token " +
                        "SET expire_time = :expire_time " +
                        "WHERE token = :token;",
                new MapSqlParameterSource()
                        .addValue("expire_time", Timestamp.from(expire_time), Types.TIMESTAMP)
                        .addValue("token", token, Types.CHAR)
        );
    }

    public void updateRefreshTokenStatus(String token, Integer token_status_id)
    {
        template.update(
                "UPDATE idm.refresh_token " +
                        "SET token_status_id = :token_status_id " +
                        "WHERE token = :token;",
                new MapSqlParameterSource()
                        .addValue("token_status_id", token_status_id, Types.INTEGER)
                        .addValue("token", token, Types.CHAR)
        );
    }

}
