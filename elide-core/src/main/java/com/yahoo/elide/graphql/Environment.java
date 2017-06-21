/*
 * Copyright 2017, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package com.yahoo.elide.graphql;

import com.google.common.collect.ImmutableList;
import com.yahoo.elide.core.PersistentResource;
import com.yahoo.elide.core.RequestScope;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLType;

import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.yahoo.elide.graphql.ModelBuilder.ARGUMENT_ID;

/**
 * Encapsulate the GraphQL request environment.
 */
public class Environment {
    public static final List<Map<String, Object>> EMPTY_DATA = ImmutableList.of();
    public static final List EMPTY_IDS = ImmutableList.of();

    public final RequestScope requestScope;
    public final List id;
    public final Object source;
    public final PersistentResource parentResource;
    public final GraphQLType parentType;
    public final GraphQLType outputType;
    public final Field field;
    public final Optional<String> sort;
    public final List<Map<String, Object>> data;
    public final Optional<String> filters;

    public Environment(DataFetchingEnvironment environment) {
        if (environment.getFields().size() != 1) {
            throw new WebApplicationException("Resource fetcher contract has changed");
        }
        Map<String, Object> args = environment.getArguments();

        requestScope = (RequestScope) environment.getContext();
        source = environment.getSource();
        parentResource = isRoot() ? null : (PersistentResource) source;
        parentType = environment.getParentType();
        outputType = environment.getFieldType();
        field = environment.getFields().get(0);
        List ids = (List) args.get(ARGUMENT_ID);

        if(ids == null) this.id = EMPTY_IDS;
        else this.id = ImmutableList.copyOf(ids);

        filters = Optional.ofNullable((String) args.get(ModelBuilder.ARGUMENT_FILTER));
        sort = Optional.ofNullable((String) args.get(ModelBuilder.ARGUMENT_SORT));

        List<Map<String, Object>> data = (List<Map<String, Object>>) args.get(ModelBuilder.ARGUMENT_DATA);
        if (data == null) {
            this.data = EMPTY_DATA;
        } else {
            this.data = ImmutableList.copyOf(data);
        }
    }

    public boolean isRoot() {
        return source instanceof RequestScope;
    }
}
