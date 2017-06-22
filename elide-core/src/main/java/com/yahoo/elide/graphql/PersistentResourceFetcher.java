/*
 * Copyright 2017, Yahoo Inc.
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file in project root for terms.
 */
package com.yahoo.elide.graphql;

import com.yahoo.elide.ElideSettings;
import com.yahoo.elide.core.DataStoreTransaction;
import com.yahoo.elide.core.EntityDictionary;
import com.yahoo.elide.core.PersistentResource;
import com.yahoo.elide.core.RequestScope;
import com.yahoo.elide.core.exceptions.InvalidAttributeException;
import com.yahoo.elide.core.exceptions.UnknownEntityException;
import com.yahoo.elide.core.filter.expression.FilterExpression;
import com.yahoo.elide.core.pagination.Pagination;
import com.yahoo.elide.core.sort.Sorting;
import com.yahoo.elide.graphql.operations.UpdateOperation;
import com.yahoo.elide.graphql.sort.Sort;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import java.util.*;
import java.util.stream.Collectors;

import static com.yahoo.elide.graphql.ModelBuilder.ARGUMENT_OPERATION;

/**
 * Interacts with {@link PersistentResource} to fetch data for GraphQL.
 */
@Slf4j
public class PersistentResourceFetcher implements DataFetcher {
    private final ElideSettings settings;

    public PersistentResourceFetcher(ElideSettings settings) { this.settings = settings; }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        //args will contain a mapping from all the arguments we provide to the entries that it contains
        //for example, id="123" or sort="ascending"
        Map<String, Object> args = environment.getArguments();

        //we extract all the environment variables and dump in an 'Environment' object
        Environment context = new Environment(environment);

        //grab the current operation requested in user query or defaults to FETCH if no op argument is present
        RelationshipOp operation = (RelationshipOp) args.getOrDefault(ARGUMENT_OPERATION, RelationshipOp.FETCH);

        if (log.isDebugEnabled()) {
            logContext(operation, context);
        }

        switch (operation) {
            case FETCH:
                return fetchObject(context);

            case UPSERT:
                return createObject(context);

            case DELETE:
                return deleteObject(context);

            case REPLACE:
                return new UpdateOperation(context).execute();
        }

        throw new UnsupportedOperationException("Unknown operation: " + operation);
    }

    private void logContext(RelationshipOp operation, Environment environment) {
        List<Field> children = environment.field.getSelectionSet() != null
                ? (List) environment.field.getSelectionSet().getChildren()
                : new ArrayList<>();
        String requestedFields = environment.field.getName() + (children.size() > 0
                ? "(" + children.stream().map(Field::getName).collect(Collectors.toList()) + ")" : "");
        GraphQLType parent = environment.parentType;
        log.debug("{} {} fields for {} with parent {}<{}>",
                operation, requestedFields, environment.source, parent.getClass().getSimpleName(), parent.getName());
    }

    private Object deleteObject(Environment request) {
//        Set<Object> deleted = new HashSet<>();
//
//        if (!request.id.isPresent() && request.data.size() > 1) {
//            throw new WebApplicationException("Id argument specification with an additional list of id's to delete "
//                    + "is unsupported", HttpStatus.SC_BAD_REQUEST);
//        }
//
//        EntityDictionary dictionary = request.requestScope.getDictionary();
//
//        String loadType = request.field.getName();
//        // TODO: This works at the root-level, but will it blow up in nested deletes?
//        String idFieldName = dictionary.getIdFieldName(dictionary.getEntityClass(loadType));
//        String loadId = request.field.getArguments().stream()
//                .filter(arg -> ARGUMENT_DATA.equals(arg.getName()))
//                .findFirst()
//                .map(Argument::getChildren)
//                // TODO: Iterate over children and determine which contains id.
//                .map(List::toString) // TODO: place holder. remove this line.
////                    .map(arg -> {
////                        String specifiedId = arg.getValue().toString();
////                        if (id != null && !id.isEmpty() && !id.equals(specifiedId)) {
////                            throw new WebApplicationException("Specified non-matching id's as argument and data.",
////                                    HttpStatus.SC_BAD_REQUEST);
////                        }
////                        return specifiedId;
////                    })
//                .orElseGet(request.id::get);
//
//        if (loadId == null) {
//            throw new WebApplicationException("Did not specify id of object type to delete.",
//                    HttpStatus.SC_BAD_REQUEST);
//        }
//
//        PersistentResource deleteObject = load(loadType, loadId, request.requestScope);
//
//        if (deleteObject == null || deleteObject.getObject() == null) {
//            throw new WebApplicationException("Attempted to delete non-existent id.", HttpStatus.SC_BAD_REQUEST);
//        }
//
//        deleteObject.deleteResource();
//        deleted.add(deleteObject);
//
//        return deleted;
        return new HashSet<>(); //TODO: placeholder, remove this
    }

    private Object createObject(Environment request) {
//        EntityDictionary dictionary = request.requestScope.getDictionary();
//
//        GraphQLObjectType objectType;
////         String uuid = UUID.randomUUID().toString();
//        String uuid = request.id.map(String::toString).orElse(""); //verify
//
//        if (request.outputType instanceof GraphQLObjectType) {
//            // No parent
//            // TODO: These UUID's should not be random. They should be whatever id's are specified by the user so they
//            // can be referenced throughout the document
//            objectType = (GraphQLObjectType) request.outputType;
//            return PersistentResource.createObject(null, dictionary.getEntityClass(objectType.getName()),
//                    request.requestScope, uuid);
//        } else if (request.outputType instanceof GraphQLList) {
//            // Has parent
//            objectType = (GraphQLObjectType) ((GraphQLList) request.outputType).getWrappedType();
//            List<PersistentResource> container = new ArrayList<>();
//            for (Map<String, Object> input : request.data) {
//                Class<?> entityClass = dictionary.getEntityClass(objectType.getName());
//                // TODO: See above comment about UUID's.
//                PersistentResource toCreate = PersistentResource.createObject(null, entityClass, request.requestScope,
//                        uuid);
//                input.entrySet().stream()
//                        .filter(entry -> dictionary.isAttribute(entityClass, entry.getKey()))
//                        .forEach(entry -> toCreate.updateAttribute(entry.getKey(), entry.getValue()));
//                container.add(toCreate);
//            }
//            return container;
//        }
//        throw new IllegalStateException("Not sure what to create " + request.outputType.getName());
        return new HashSet<>(); //TODO: placeholder, remove this
    }

    private Object fetchObject(Environment request) {
        if (!request.data.isEmpty()) {
            // make exceptions more specific, this would be BadRequestException
            throw new BadRequestException("FETCH must not include data.");
        }

        Optional<FilterExpression> filters = Optional.empty();
        Optional<Sorting> sorting = Optional.empty();
        Optional<Pagination> pagination = Optional.empty();

        RequestScope requestScope = request.requestScope;
        if (request.outputType instanceof GraphQLList) {
            if ((request.id != null && !request.id.isEmpty())){
                if (request.filters.isPresent()) {
                    throw new WebApplicationException("You may not filter when loading by id");
                }
                if(request.sort.isPresent()) {
                    throw new WebApplicationException("You may not sort when loading by id");
                }
            }

            GraphQLObjectType graphQLType = (GraphQLObjectType) ((GraphQLList) request.outputType).getWrappedType();
            String entityType = graphQLType.getName();
            Class recordType = (Class) requestScope.getDictionary().getEntityClass(entityType);

            if (recordType == null) {
                throw new UnknownEntityException(entityType);
            }

            /* list of records accessed from internal db and returned */
            HashSet recordSet = new HashSet();
            if(!request.id.isEmpty())
            for(Object id : request.id) {
                if(id != null) recordSet.add(PersistentResource.loadRecord(recordType, (String)id, requestScope));
            }
            /* No 'ids' field is specified, return all the records with given root object */
            else {
                 Set records = PersistentResource.loadRecords(recordType, requestScope);

                /* handle sorting */
                if(request.sort.isPresent()) {
                    String sortArg = request.sort.get();
                    Sort sortInstance = new Sort(sortArg);
                    return sortInstance.sort(records, requestScope);
                }

                 /* handle pagination */
                if(request.first.isPresent()) {
                    int first, offset, start, end;
                    try{
                        first = Integer.parseInt(request.first.get());
                    } catch (NumberFormatException e) {
                        return e;
                    }
                    if(!request.offset.isPresent()) {
                        start = 0;
                        end = first;

                    } else {
                        try {
                            offset = Integer.parseInt(request.offset.get());
                        } catch (NumberFormatException e) {
                            return e;
                        }
                        start = first;
                        end = offset + first;
                    }
                    List<PersistentResource> paginatedList = new ArrayList<>(records);
                    return paginatedList.subList(start, end);
                }

                return records;
            }
            return recordSet;

        } else if (request.outputType instanceof GraphQLObjectType) {
            if (request.parentResource == null) {
                throw new IllegalStateException("Do we have a singleton root object?");
            }

            // we are loading a toOne relationship
            DataStoreTransaction tx = request.requestScope.getTransaction();
            PersistentResource resource = request.parentResource;
            String relationName = request.field.getName();
            Object obj =
                    tx.getRelation(tx, resource.getObject(), relationName, filters, sorting, pagination, requestScope);
            return new PersistentResource(obj, resource, requestScope.getUUIDFor(obj), requestScope);

        } else if (request.outputType instanceof GraphQLScalarType) {
            return fetchProperty(request);
        } else if (request.outputType instanceof GraphQLEnumType) {
            return fetchProperty(request);
        }

        throw new IllegalStateException("WTF is a " + request.outputType.getClass().getName() + " mate?");
    }

    protected Object fetchProperty(Environment request) {
        EntityDictionary dictionary = request.requestScope.getDictionary();
        PersistentResource resource = (PersistentResource) request.parentResource;
        Class<?> sourceClass = resource.getResourceClass();

        String fieldName = request.field.getName();
        if (dictionary.isAttribute(sourceClass, fieldName)) {
            return resource.getAttribute(fieldName);
        } else {
            log.debug("Tried to fetch property off of invalid loaded object.");
            throw new InvalidAttributeException(fieldName, resource.getType());
        }
    }

    public static Collection<PersistentResource> loadCollectionOf(String type, RequestScope requestScope) {
        Class recordType = (Class) requestScope.getDictionary().getEntityClass(type);
        return PersistentResource.loadRecords(recordType, requestScope);
    }

    public static PersistentResource load(String type, String id, RequestScope requestScope) {
        Class<?> recordType = requestScope.getDictionary().getEntityClass(type);
        return PersistentResource.loadRecord(recordType, id, requestScope);
    }
}