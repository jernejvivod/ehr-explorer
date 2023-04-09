# EHR Explorer

EHR Explorer is an application used to visualize and preprocess the <a href="https://physionet.org/content/mimiciii/1.4/">MIMIC-III dataset</a>. 

## Setting Up the Environment

This section describes how to set up the environment for the EHR Explorer project.

### Initializing the Database and Adding the Data

The [db/db-init/init](db/db-init/init) folder contains the script [db/db-init/init/docker-create.sh](db/db-init/init/docker-create.sh) that can be used to automatically build and initialize a containerized PostgreSQL database. 
The script takes three arguments - the path to the MIMIC-III dataset on the host, the path to the data volume on the host, and the name of the container. For example:

```
$./docker-create.sh /home/jernej/mimic-iii-dataset-full/ /home/jernej/db-data-volume/ mimic-db-container
```

## TODO

```
sudo docker build -t ehr-explorer .
```


```
sudo docker run -p 5432:5432 --network host --name ehr-explorer ehr-explorer:latest
```