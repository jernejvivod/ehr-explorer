# EHR Explorer

EHR Explorer provides tools for working with various EHR datasets and integrates functionality designed specifically
for working with the <a href="https://physionet.org/content/mimiciii/1.4/">MIMIC-III dataset</a>.

### Also see [EHR Explorer API](https://github.com/jernejvivod/ehr-explorer-api), [EHR Explorer Client](https://github.com/jernejvivod/ehr-explorer-client), and [Classification With Embeddings](https://github.com/jernejvivod/classification-with-embeddings).

## Quickstart

This section is intended to provide a concise guide on how to quickly set up and run the project.

### Setting Up the Environment

This section describes how to set up the environment for the EHR Explorer project.

#### Initializing the Database and Adding the Data

The [db/db-init/init](db/db-init/init) folder contains the
script [db/db-init/init/docker-create.sh](db/db-init/init/docker-create.sh) that can be used to automatically build and
initialize a containerized PostgreSQL database.
The script takes three arguments - the path to the MIMIC-III dataset on the host, the path to the data volume on the
host, and the name of the container. For example:

```bash
$ ./docker-create.sh /home/jernej/mimic-iii-dataset-full/ /home/jernej/db-data-volume/ mimic-db-container
```

We can start the created container using:

```bash
$ sudo docker start mimic-db-container
```

The database is now accessible at `jdbc:postgresql://localhost:5432/mimic` using the default Postgres username and
password (postgres/postgres).

#### Deploying the Project Locally in a Docker Container

When cloning the repository, make sure to use the `--recurse-submodules` flag.

The project can easily be deployed locally in a Docker container by first creating the artifacts using the `mvn package`
goal.

We can then build the Docker image by running:

```bash
$ sudo docker build -t ehr-explorer .
```

We can then build and run the created Docker container by running the following command:

```bash
$ sudo docker create --network host --name ehr-explorer ehr-explorer:latest
```

The `--network host` option is necessary to allow our ehr-explorer deployment to connect to the containerized database
deployment.

We can then start our container using:

```bash
$ sudo docker start ehr-explorer
```

We can check that our deployment is running with:

```bash
curl -X GET http://localhost:8080/ehr-explorer-core/ping
```

## Functionality Overview

This section describes the core functionality of the EHR Explorer application. Here, you'll find a brief overview of the
features and capabilities that the application provides.

The EHR explorer implements a REST API for preprocessing and extracting data from EHR datasets. Some of the
functionality is general and can be applied to a custom-provided relational database.
Some of the functionality is specifically targeted at the well-known MIMIC-III dataset.

JPA entities for the MIMIC-III dataset are also provided in the `mimi-iii-entity` module.

## Non-MIMIC-III Related Functionality

EHR Explorer provides functionality that can be used with a custom relational database. The `core` module does not
depend on the MIMIC-III dataset JPA entities. The user can provide their own JPA entities and `persistence.xml` to use
with the provided functionality. Make sure that in this case the `mimic-iii-target-extraction` module artifact is not
deployed.

## Propositionalization

Propositionalization in machine learning is the process of transforming relational data into a propositional or
attribute-value form, which is more suitable for many machine learning algorithms. Relational data typically consists of
tables or datasets that have relationships between different entities or instances.
Propositionalization involves creating a new dataset where each instance corresponds to a unique combination of
attributes and values.

EHR Explorer supports the use of the Wordification algorithm to produce a propositionalization of the provided database.
It supports advanced features such as the possibility to create composite columns
(columns that are constructed by combining columns of different tables), the possibility of specifying a value
transformation to be applied to the columns, the possibility of limiting the data to be considered based on values of a
column of the root entity containing dates which is particularly useful for evaluating machine learning algorithms as we
may not want to train the algorithms on information that would not yet be available in a real-world scenario.

### Wordification

Wordification is a propositionalization technique for Relational Data Mining (RDM) that transforms a relational database
into a corpus of text documents. It constructs simple, easy-to-understand features that act as "words" in the
transformed
Bag-Of-Words representation. Each original instance is transformed into a "document" represented as a Bag-Of-Words (BOW)
vector of weights of simple features, which correspond to individual attribute values of the target table and related
tables.
The main hypothesis of the Wordification approach is that the use of this simple representation bias is suitable for
achieving good results in classification tasks. Wordification has several advantages, including a simple implementation,
accuracy comparable to competitive methods, and greater scalability.

Wordification constructs features of the form `table_name@column_name@column_value`. It can take into account
interactions by constructing aggregate features of the form
`table_name1@column_name_i@column_value_i@@table_name1@column_name_j@column_value_j` for pairs of columns for a table
row.

#### Computing Wordification Using EHR Explorer

Wordification can be computed by sending a POST request using the `/propositionalization/wordification` path.

A sample of the full request body is given below.
Please see [EHR Explorer API](https://github.com/jernejvivod/ehr-explorer-api) for the complete OpenAPI specification.

TODO make data make sense.

```json
{
  "rootEntitiesSpec": {
    "rootEntity": "AdmissionsEntity",
    "idProperty": "hadmId",
    "ids": [
      0
    ]
  },
  "propertySpec": {
    "entries": [
      {
        "entity": "AdmissionsEntity",
        "properties": [
          "insurance"
        ],
        "propertyForLimit": "admitTime",
        "compositePropertySpecEntries": [
          {
            "propertyOnThisEntity": "inTime",
            "propertyOnOtherEntity": "dob",
            "foreignKeyPath": [
              [
                "IcuStaysEntity",
                "PatientsEntity"
              ]
            ],
            "compositePropertyName": "ageAtAdmission",
            "combiner": "DATE_DIFF"
          }
        ]
      }
    ],
    "rootEntityAndLimeLimit": [
      {
        "rootEntityId": 0,
        "timeLim": "2023-04-28T13:21:56.851Z"
      }
    ]
  },
  "compositeColumnsSpec": {
    "entries": [
      {
        "foreignKeyPath1": [
          "string"
        ],
        "property1": "admitTime",
        "foreignKeyPath2": [
          "string"
        ],
        "property2": "dob",
        "compositeName": "ageDecades",
        "combiner": "DATE_DIFF"
      }
    ]
  },
  "valueTransformationSpec": {
    "entries": [
      {
        "entity": "AdmissionsEntity",
        "property": "string",
        "transform": {
          "kind": "ROUNDING",
          "roundingMultiple": 20,
          "dateDiffRoundType": "YEAR"
        }
      }
    ]
  },
  "concatenationSpec": {
    "concatenationScheme": "ZERO"
  }
}
```

The values associated with the `rootEntitiesSpec` key specify the information about the root entities to be considered
when computing Wordfication. It is used to specify the root entity (table) to be used when computing Wordification,
the ID property of that table, and the IDs of the root entities to consider. If the list of IDs is not specified, all
entities are considered.

The values associated with the `propertySpec` key specify information about the properties of the entities to consider.
The actual properties for particular entities are listed under the `entries` key.
We also specify the property used to limit the values by a column holding date columns. We can also specify composite
properties. We must specify the path to the entity holding the value to be combined with the value on this entity.

We also specify the date/time limits for the entities (for each ID) using the `rootEntityAndTimeLimit` property.

We can also specify composite columns that will be part of a new table called `composite`. We need to specify the paths
to the entities containing the specified properties.

We can also specify value transformations that are to be applied to a particular column of a particular entity using the
values associated with the `valueTransformerSpec` key.

Finally, we can specify the type of concatenation of Wordificatoin features to use. Using `"ZERO"` results in the
plain `column@table@value` features while using for example `"ONE"` results in the creation of combined features of the
form `table_name1@column_name_i@column_value_i@@table_name1@column_name_j@column_value_j`.

An example of the response body is given below.

TODO better example

```json
[
  {
    "rootEntityId": 0,
    "timeLim": "2023-04-29T07:43:22.189Z",
    "words": [
      "string"
    ]
  }
]
```

We obtain a list of values where the value associated with the `rootEntityId` key represents the ID of the root entity
on which Wordification was performed.

The value associated with the `timeLim` key represents the date limit that was
applied when computing Wordification for the entity (specified in the request).

The list of string values associated with the `words` key is the actual features obtained using Wordification for
this root entity.

## Clinical Text Extraction

Electronic health records can store vast amounts of clinical data, including physician notes, diagnostic reports, and
discharge summaries. The EHR Explorer app offers a tool for extracting clinical text data from EHR databases.
It allows users to retrieve text linked to particular database entities. It is also possible to limit the retrieved text
by a date column.

### Extracting Clinical Text Using EHR Explorer

Clinical text extraction can be performed by sending a POST request using the `/clinical-text/extract` path.

A sample of the full request body is given below.
Please see [EHR Explorer API](https://github.com/jernejvivod/ehr-explorer-api) for the complete OpenAPI specification.

TODO add better example, fix ordering

```json
{
  "foreignKeyPath": [
    [
      "AdmissionsEntity",
      "NoteEventsEntity"
    ]
  ],
  "textPropertyName": "text",
  "clinicalTextEntityIdPropertyName": "rowId",
  "clinicalTextDateTimePropertiesNames": [
    "string"
  ],
  "rootEntityDatetimePropertyForCutoff": "outTime",
  "rootEntitiesSpec": {
    "rootEntity": "AdmissionsEntity",
    "idProperty": "hadmId",
    "ids": [
      0
    ]
  },
  "clinicalTextExtractionDurationSpec": {
    "firstMinutes": 0
  }
}
```

The root entities are specified as in Wordification.

The `foreignKeyPath` key is used to specify the entity path from the root entity to the entity containing the column
holding the clinical text. We then specify the text property name of the entity as well as the ID property.

The `clinicalTextDateTimePropertiesNames` key is used to specify a list of the date columns by which to sort the data.
The data is first sorted by the first specified column and any ties are resolved by considering the next specified
properties in the order they were declared.

We can specify the property of the root entity to be used to limit the data to a particular duration from the first
record. The duration is specified in minutes.

An example of the response body is given below.

```json
[
  {
    "rootEntityId": 0,
    "text": "string"
  }
]
```

We obtain a list of values where the value associated with the `rootEntityId` key represents the ID of the root entity
for which clinical text extraction was performed.

## Extracting IDs

EHR Explorer also supports retrieval of database rows' ID values that pass specified filtering criteria. This can be
useful for extracting data that matches that filtering criteria which can be used for various machine learning tasks.

### Extracting IDs using EHR Explorer

ID extraction can be performed by sending a POST request using the `/ids` path.

A sample of the full request body is given below.
Please see [EHR Explorer API](https://github.com/jernejvivod/ehr-explorer-api) for the complete OpenAPI specification.

TODO better example, what is the purpose of the propertyVal being an object?

```json
{
  "entityName": "string",
  "idProperty": "string",
  "filterSpecs": [
    {
      "foreignKeyPath": [
        [
          "AdmissionsEntity",
          "NoteEventsEntity"
        ]
      ],
      "propertyName": "string",
      "comparator": "LESS",
      "propertyVal": {}
    }
  ]
}
```

Along with the name of the entity and the name of its ID property, we can specify filters by which the entities will be
filtered. For each filter, an optional path to the related entity is specified. We specify the name of the property
by which we want to perform the filtering and the comparison to use as well as the value with which we want to compare
the values in the column.

An example of the response body is given below.

```json
[
  "string"
]
```

The response contains a list of IDs for which the associated data passes the specified filtering criteria.

## Extracting Table Statistics

It is possible to extract some basic table statistics using EHR Explorer.

### Extra Basic Table Statistics Using EHR Explorer

We can extract the number of rows of a database table, the number of non-null values in each column, and the number of
unique values in each column.

Basic table statistics extraction can be performed by sending a GET request using the `/stats` path (statistics for all
entities/tables) or the `/stats/{entityName}` path (statistics for the specified entity/table).

An example of the response body is given below.

TODO better example

```json
[
  {
    "entityName": "AdmissionsEntity",
    "numEntries": 0,
    "propertyStats": [
      {
        "propertyName": "admission_type",
        "numNull": 10128,
        "numUnique": 3
      }
    ]
  }
]
```

The response contains a list of data corresponding to computed statistics for a table in the dataset. It contains the
number of records in the table as well as the number of non-null and unique values for each column.

## Functionality Related to the MIMIC-III Dataset

EHR Explorer also provides functionality that is specifically designed for use with the MIMIC-III dataset. It allows the
user to extract the class values for various classification tasks applicable to the dataset.

### Extracting Class Values for Various MIMIC-III-related Classification Tasks

EHR Explorer can be used to extract class values for various MIMIC-III-related classification tasks. This functionality
is provided by the `mimic-iii-target-extraction` module which is dependent on the `mimic-iii-entity` module containing
the JPA entities for the MIMIC-III dataset.

Class value extraction can be performed by sending a POST request using the `/target` path.

Currently, EHR Explorer supports extracting target values for three different classification objectives. It also
supports filtering the entities based on the minimum age the associated patient (`PatientsEntity` instance).

An example of the request body for the target extraction tasks is given below.

```json
{
  "targetType": "PATIENT_DIED_DURING_ADMISSION",
  "ids": [
    0
  ],
  "ageLim": 18
}
```

We specify the classification objective with the value associated with the `targetType` key. We specify the IDs of the
root entities representing the entries in the relevant tables.
All records will be considered if we don't specify the IDs.

The root entities are the entities with which the records representing associated events to which we want to assign
classes, such as hospital and ICU admissions, are associated. This allows us to get data associated
with each root entity (for example the patient) up to the date of the event, as the response also includes the cut-off
date value for the data for that particular event.

We can optionally specify the minimum age of the associated patients with a value associated with the `ageLim` key.

An example response for this request is given below.

```json
[
  {
    "rootEntityId": 0,
    "targetEntityId": 0,
    "targetValue": 0,
    "dateTimeLimit": "2023-04-29T08:08:31.407Z"
  }
]
```

The value associated with the `rootEntityId` key specifies the ID of the root entity (record) to which the records we
are assigning classes to are related. The actual root entities (tables)
for each objective are stated below where each objective is discussed in more detail.

The value associated with the `targetEntityId` key specifies the ID of the target entity (record) which is the entity
that is assigned the actual class.

The value associated with the `targetValue` property specifies the determined class of the target entity. A value of 0
represents a negative class while any value greater than 0 represents a positive
class.

The value associated with the `dateTimeLimit` key represents the limit for the values in tables associated with the root
entity. This means that any record in the database that is associated with the
root entity and has a date column happened before the event represented by the target entity (record) if the value of
the date column is less than this value.

#### Patient Died During Admission

For this objective we want to assign a class value to the entries in the `admissions` table (`AdmissionsEntity`
instances) based on whether the hospital admission resulted in the death of the
patient (class value of 1) or not (class value of 0).

For this objective, the root entity and the target entity refer to the same table `admissions` (`AdmissionsEntity`).
This may change in the future so that the root entity refers to the `patients` table (`PatientsEntity`).

#### Hospital Readmission Happened

For this objective, we want to assign class values to the entries in the `admissions` table (`AdmissionsEntity`
instances) based on whether another hospital admission with certain
characteristics happened after the current one.

Namely, we consider the following admissions as having a positive class:

- If another admission happened after the current one and the number of days between the admissions were less than the
  specified amount.
- If the patient died after being discharged within the specified number of days after the discharge.

In all other cases, the `admissions` entry is assigned a negative class (class 0).

For this objective, the root entity refers to the `PatientsEntity` entity (table `patients`) and the target entity refers
to the `AdmissionsEntity` (table `admissions`).

#### ICU Readmission Happened

For this objective, we want to assign class values to the entries in the `icustays` table (`IcuStaysEntity` instances)
based on whether another ICU admission with certain characteristics
happened after the current one.

Namely, we consider the following ICU admissions as having a positive class:

- If a Patient was transferred to a low-level ward from the ICU, but returned to ICU again (assign class value of 1).
- If the patient was discharged from the hospital, but returned to the ICU within a specified number of days (assign
  class value of 2).
- If the patient was transferred to a low-level ward from the ICU, and later died in the hospital (assign class value of
  3).
- if the patient died after being discharged less than the specified number of days after the discharge (assign class
  value of 4).

In all other cases, the `icustays` entry is assigned a negative class (class 0).

For this objective the root entity refers to the `PatientsEntity` entity (table `patients`) and the target entity refers
to the `IcuStaysEntity` (table `icustays`).

## Using [EHR Explorer Client](https://github.com/jernejvivod/ehr-explorer-client) to Query the Data

The EHR Explorer Client is a related project that provides the means to query the data provided by EHR Explorer
using a command-line interface. It facilitates the retrieval of data for various tasks. See the project's repository
at https://github.com/jernejvivod/ehr-explorer-client for more information.
