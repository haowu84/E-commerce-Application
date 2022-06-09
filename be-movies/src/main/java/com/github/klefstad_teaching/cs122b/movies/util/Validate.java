package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Validate
{
    public void validateMovieOrderBy(String orderBy)
    {
        if (orderBy == null)
            return;

        switch (orderBy.toUpperCase(Locale.ROOT))
        {
            case "TITLE":
            case "RATING":
            case "YEAR":
                break;
            default:
                throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void validatePersonOrderBy(String orderBy)
    {
        if (orderBy == null)
            return;

        switch (orderBy.toUpperCase(Locale.ROOT))
        {
            case "NAME":
            case "POPULARITY":
            case "BIRTHDAY":
                break;
            default:
                throw new ResultError(MoviesResults.INVALID_ORDER_BY);
        }
    }

    public void validateDirection(String direction)
    {
        if (direction == null)
            return;

        switch(direction.toUpperCase(Locale.ROOT))
        {
            case "ASC":
            case "DESC":
                break;
            default:
                throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
    }

    public void validateLimit(Integer limit)
    {
        if (limit == null)
            return;

        switch (limit)
        {
            case 10:
            case 25:
            case 50:
            case 100:
                break;
            default:
                throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
    }

    public void validateOffset(Integer offset)
    {
        if (offset == null)
            return;

        if (!(offset > 0))
        {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
    }
}
