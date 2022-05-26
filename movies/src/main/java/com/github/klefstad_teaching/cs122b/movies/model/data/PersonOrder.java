package com.github.klefstad_teaching.cs122b.movies.model.data;

import java.util.Locale;

public enum PersonOrder {
    NAME(" ORDER BY p.name, p.id "),
    POPULARITY(" ORDER BY p.popularity, p.id "),
    BIRTHDAY(" ORDER BY p.birthday, p.id "),
    NAME_DESC(" ORDER BY p.name DESC, p.id "),
    POPULARITY_DESC(" ORDER BY p.popularity DESC, p.id "),
    BIRTHDAY_DESC(" ORDER BY p.birthday DESC, p.id ");

    private final String sql;

    PersonOrder(String sql)
    {
        this.sql = sql;
    }

    public String toSql()
    {
        return sql;
    }

    public static PersonOrder fromString(String orderBy, String direction)
    {
        if (orderBy == null)
        {
            if (direction == null)
            {
                return NAME;
            }

            switch(direction.toUpperCase(Locale.ROOT))
            {
                case "ASC":
                    return NAME;
                case "DESC":
                    return NAME_DESC;
                default:
                    return null;
            }
        }

        switch (orderBy.toUpperCase(Locale.ROOT))
        {
            case "NAME":
                if (direction == null)
                {
                    return NAME;
                }

                switch(direction.toUpperCase(Locale.ROOT))
                {
                    case "ASC":
                        return NAME;
                    case "DESC":
                        return NAME_DESC;
                    default:
                        return null;
                }
            case "POPULARITY":
                if (direction == null)
                {
                    return POPULARITY;
                }

                switch(direction.toUpperCase(Locale.ROOT))
                {
                    case "ASC":
                        return POPULARITY;
                    case "DESC":
                        return POPULARITY_DESC;
                    default:
                        return null;
                }
            case "BIRTHDAY":
                if (direction == null)
                {
                    return BIRTHDAY;
                }

                switch(direction.toUpperCase(Locale.ROOT))
                {
                    case "ASC":
                        return BIRTHDAY;
                    case "DESC":
                        return BIRTHDAY_DESC;
                    default:
                        return null;
                }
            default:
                return null;
        }
    }
}
