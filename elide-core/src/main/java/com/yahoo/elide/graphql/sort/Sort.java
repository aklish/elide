/*
 * Copyright 2017, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */

package com.yahoo.elide.graphql.sort;

import com.yahoo.elide.core.PersistentResource;
import com.yahoo.elide.core.RequestScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Sort {
    private String sortArg;
    private boolean order; /* true for ascending, false for descending */

    public Sort(String sortArg) {
        this.sortArg = sortArg;
        this.order = true; /* sort in ascending order by default */
    }

    /**
     * TODO: THIS IS NOT DRY, WHAT A SIGH! Refactor alert!!
     */
    private String parseSortRule() throws IllegalArgumentException {
        char firstCharacter = sortArg.charAt(0);
        if (firstCharacter == '-') {
            this.order = false;
            return sortArg.substring(1);
        }
        if (firstCharacter == '+') {
            // json-api spec supports asc by default, there is no need to explicitly support +
            return sortArg.substring(1);
        }
        throw new IllegalArgumentException("Please check your sorting argument, allowed arguments are '+' and '-' corresponding" +
                " to descending or ascending");
    }

    /**
     * @param records set of {@link PersistentResource} objects which contain the actual data retrieved from db
     * @param requestScope Request Scope object
     * @return Sorted list based on sortArg and order
     */
    public void sort(List<PersistentResource> records, RequestScope requestScope) {
        String sortRule = parseSortRule();

        records.sort((o1, o2) -> {
            Object val1 = PersistentResource.getValue(o1.getObject(), sortRule, requestScope);
            Object val2 = PersistentResource.getValue(o2.getObject(), sortRule, requestScope);
            if(val1 instanceof String && val2 instanceof String)
                return this.order ? val2.toString().compareTo(val1.toString()) : val1.toString().compareTo(val2.toString());
            else /* we only allow String and Integer field definitions (See ModelBuilder) */
                return this.order ? (Integer) val1 - (Integer) val2 : (Integer) val2 - (Integer) val1;
        });
    }
}
