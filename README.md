# EHR Explorer

EHR Explorer is an application used to work with the <a href="https://physionet.org/content/mimiciii/1.4/">MIMIC-III dataset</a>. 

## Setting Up the Environment

This section describes how to set up the environment for the EHR Explorer project.

### Initializing the Database and Adding the Data

The [db/db-init/init](db/db-init/init) folder contains the script [db/db-init/init/docker-create.sh](db/db-init/init/docker-create.sh) that can be used to automatically build and initialize a containerized PostgreSQL database. 
The script takes three arguments - the path to the MIMIC-III dataset on the host, the path to the data volume on the host, and the name of the container. For example:
```
$ ./docker-create.sh /home/jernej/mimic-iii-dataset-full/ /home/jernej/db-data-volume/ mimic-db-container
```

We can start the created container using:
```
$ sudo docker start mimic-db-container
```

The database is now accessible at `jdbc:postgresql://localhost:5432/mimic` using the default Postgres username and password (postgres/postgres).

## Deploying the Project Locally in a Docker Container

The project can easily be deployed locally in a Docker container by first creating the artifacts using the `mvn package` goal.

We can then build the Docker image by running:
```
$ sudo docker build -t ehr-explorer .
```

We can then build and run the Docker container by running the following command:
```
$ sudo docker create --network host --name ehr-explorer ehr-explorer:latest
```

The `--network host` option is necessary to allow our ehr-explorer deployment to connect to the containerized database deployment.

We can then start our container using:
```
$ sudo docker start ehr-explorer
```

We can check that our deployment is running with:
```
curl -X GET http://localhost:8080/ehr-explorer-core/ping
```
