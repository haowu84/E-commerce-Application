package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.model.GatewayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Component
public class GatewayRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public int[] insert(List<GatewayRequest> requests)
    {
        MapSqlParameterSource[] arrayOfSources = requests.stream()
                .map(
                        request ->
                                new MapSqlParameterSource()
                                        .addValue("ip_address", request.getIpAddress() , Types.VARCHAR)
                                        .addValue("call_time", Timestamp.from(request.getCallTime()), Types.TIMESTAMP)
                                        .addValue("path", request.getPath(), Types.VARCHAR)
                )
                .toArray(MapSqlParameterSource[]::new);

        return this.template.batchUpdate(
                "INSERT INTO gateway.request (ip_address, call_time, path) " +
                        "VALUES (:ip_address, :call_time, :path);",
                arrayOfSources
        );
    }

    public Mono<int[]> insertRequests(List<GatewayRequest> requests)
    {
        return Mono.fromCallable(() -> insert(requests));
    }
}
