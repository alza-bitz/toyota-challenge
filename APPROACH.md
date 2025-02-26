# Problems

1. Retrieve the top 10 movies with a minimum of 500 votes with the ranking determined by: (numVotes/averageNumberOfVotes) * averageRating
 
2. For these 10 movies, list the persons who are most often credited and list the
different titles of the 10 movies.

# Approach

For each problem:

1. Understand the language and the problem
2. Understand the data model and how it maps to the problem (or vice versa): what parts do I need
3. Solve the problem first in Spark SQL non-streaming, using PySpark & Jupyter notebook for quick cycles
4. Then adapt to Spark dataframes api non-streaming
5. Then adapt to Spark structured streaming
6. Then convert to Java / Spring with unit tests (example and/or property-based) & docs a.k.a. "productionised"

Ideally steps 3-5 would be round-tripped in a dev feedback loop to build up the solution in stages.

Ideally steps 3-4 should take into account 

- The data will be streamed (even if using non-streaming sql or api etc)
- The Spark structured streaming limitations, in order to avoid "suprises" later on.

# Assumptions, Risks
1. That a Spark SQL and/or dataframes solution can be adapted to structured streaming (as long as it's constraints are met)
2. That a continuous, event-based stream can be materialized from files somehow (the source data is files)
3. That I can have some control over when events are produced, such that otherwise the process waits for an event?

# Problem 1.

## Language
"movies"
"votes"
"ranking"
"rating"

## Questions
1. For 'averageNumberOfVotes' is average of all rows ok, or must it be average of rows with >= 500 votes. (I am assuming average of rows with >= 500 votes)
2. For 'top 10 movies' is id enough or must it be title name? (I am assuming id is enough because the title is only mentioned in the second problem "list the
different titles")

## Data Model
The data looks like an export of normalized tables
https://zindilis.com/posts/imdb-non-commercial-datasets-schema

ok.
"name" refers to people.
"title" refers to movies.

tconst is an ID for a title, and
nconst is an ID for a name.

## Thoughts

In a streaming solution, there are events. What are the events in this case?

Consider each line of the source files is likely a row from a db table.

As in, they are "current" records of  entities, not a "historical" log of events.
