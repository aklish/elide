# GraphQL Support for Elide

## Overview

The next development iteration for Elide focuses on seamless conversion from REST to a graphQL endpoint by simply mounting the provided endpoint to a specified path. Using GraphQL can be a more relevant approach in cases when, say, the user API is not using REST specific services like HATEOAS. Among others, it provides the following key advantages - 
- Single endpoint to access all the data
- No need to tailor specific endpoints for each view/collection
- Flexibility to access only the data the client needs in a single request

Elide utilizes [graphql-java's](http://graphql-java.readthedocs.io/en/latest/index.html) execution strategies to execute a graphql  Query/Mutation against an elide Schema. 

## Structure

The main files are located under the package `graphql` within `elide-core`
```
+-- elide-core
.
.
.
+-- graphql
   +-- operations
|       -- UpdateOperation.java
   +-- sort
|       -- Sort.java
+-- Environment.java
+-- GraphQLConversionUtils.java
+-- GraphQLEndpoint.java
+-- ModelBuilder.java
+-- MutableGraphQLInputObjectType.java
+-- NonEntityDictionary.java
+-- PersistentResourceFetcher.java
+-- RelationshipOp.java
```
The tests are located under the package `graphql` within `elide-core` > `test` 

## Classes

First let's go over the supported operations for graphql queries/mutations. These are defined under `RelationshipOp.java` as below - 
```
public enum RelationshipOp {
    FETCH,
    DELETE,
    ADD,
    REPLACE;
}
```
Refer to the `GraphQL.md` for a thorough explanation of the functionality of each operation. 