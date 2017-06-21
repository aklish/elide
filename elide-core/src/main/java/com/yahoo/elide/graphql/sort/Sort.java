/*
 * Copyright 2017, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */

package com.yahoo.elide.graphql.sort;

import com.yahoo.elide.core.PersistentResource;
import com.yahoo.elide.core.RequestScope;

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
                " to ascending or descending");
    }

    /**
     * @param records set of {@link PersistentResource} objects which contain the actual data retrieved from db
     * @param requestScope Request Scope object
     * @return Sorted list based on sortArg and order
     */
    public List<PersistentResource> sort(Set<PersistentResource> records, RequestScope requestScope) {
        String sortRule = parseSortRule();
        ArrayList<PersistentResource> sortedList = new ArrayList(records);

        sortedList.sort((o1, o2) -> {
            String s1 = (String) PersistentResource.getValue(o1.getObject(), sortRule, requestScope);
            String s2 = (String) PersistentResource.getValue(o2.getObject(), sortRule, requestScope);
            return this.order ? s2.compareTo(s1) : s1.compareTo(s2);
        });

        return sortedList;
    }
}
