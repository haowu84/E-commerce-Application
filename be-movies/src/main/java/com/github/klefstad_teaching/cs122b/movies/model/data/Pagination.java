package com.github.klefstad_teaching.cs122b.movies.model.data;

public class Pagination {
    private static final String TEN = " LIMIT 10 ";
    private static final String TWENTY_FIVE = "  LIMIT 25 ";
    private static final String FIFTY = "  LIMIT 50 ";
    private static final String ONE_HUNDRED ="  LIMIT 100 ";

    public static String toString(Integer limit, Integer page)
    {
        if (page == null)
        {
            page = 1;
        }

        if (limit == null)
        {
           limit = 10;
        }

        String offset = String.format(" OFFSET %d ", ((page - 1) * limit));

        switch (limit)
        {
            case 10:
                return TEN + offset;
            case 25:
                return TWENTY_FIVE + offset;
            case 50:
                return FIFTY + offset;
            case 100:
                return ONE_HUNDRED + offset;
            default:
                return null;
        }
    }
}
