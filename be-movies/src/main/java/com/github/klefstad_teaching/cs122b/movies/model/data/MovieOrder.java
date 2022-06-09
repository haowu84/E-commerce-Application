package com.github.klefstad_teaching.cs122b.movies.model.data;

import java.util.Locale;

public enum MovieOrder {
    TITLE(" ORDER BY m.title, m.id "),
    RATING(" ORDER BY m.rating, m.id "),
    YEAR(" ORDER BY m.year, m.id "),
    TITLE_DESC(" ORDER BY m.title DESC, m.id "),
    RATING_DESC(" ORDER BY m.rating DESC, m.id "),
    YEAR_DESC(" ORDER BY m.year DESC, m.id ");

    private final String sql;

    MovieOrder(String sql)
    {
        this.sql = sql;
    }

    public String toSql()
    {
        return sql;
    }

    public static MovieOrder fromString(String orderBy, String direction)
    {
        if (orderBy == null)
        {
            if (direction == null)
            {
                return TITLE;
            }

            switch(direction.toUpperCase(Locale.ROOT))
            {
                case "ASC":
                    return TITLE;
                case "DESC":
                    return TITLE_DESC;
                default:
                    return null;
            }
        }

        switch (orderBy.toUpperCase(Locale.ROOT))
        {
            case "TITLE":
                if (direction == null)
                {
                    return TITLE;
                }

                switch(direction.toUpperCase(Locale.ROOT))
                {
                    case "ASC":
                        return TITLE;
                    case "DESC":
                        return TITLE_DESC;
                    default:
                        return null;
                }
            case "RATING":
                if (direction == null)
                {
                    return RATING;
                }

                switch(direction.toUpperCase(Locale.ROOT))
                {
                    case "ASC":
                        return RATING;
                    case "DESC":
                        return RATING_DESC;
                    default:
                        return null;
                }
            case "YEAR":
                if (direction == null)
                {
                    return YEAR;
                }

                switch(direction.toUpperCase(Locale.ROOT))
                {
                    case "ASC":
                        return YEAR;
                    case "DESC":
                        return YEAR_DESC;
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
