# SM engine

## Prerequisites
* Java8
* Maven3
* [Heroku CLI](https://devcenter.heroku.com/articles/heroku-cli#download-and-install)
* Git
* Docker
* [Postman App](https://www.getpostman.com/)
Currently deploying Docker-based apps on heroku is in beta and **Expose port** is not supported yet, so we use embedded ldap server using UnboundID LDAP SDK.

## Configuration
Usually you are not required to modify configurations except **spring.data.mongodb.uri**.
You can find configurations in `crud/src/main/resources/application.properties`.`crud/src/main/resources/application-heroku.properties`, `crud/src/test/resources/application-test.properties` and  `ldap/src/test/resources/application-test.properties`.
You can find jwt configurations with **jwt** prefix.
You can edit mongo url with **spring.data.mongodb.uri**.

You can find example ldap ldif file  in `crud/src/main/resources/test.ldif`.
Currently it will load ldap configurations using latest enabled ldap configuration in mongo and you can use endpoint `PUT /ldap-configurations` if necessary.

## Setup Mongo Data
Import test data (`test_files/*.json`) to your MongoDB database
  If you use a GUI client such as MongoBooster, it's easy to import JSON files. MongoBooster has an Import button right on the toolbar.
You must setup data in mongo for local/heroku deployment otherwise you cannot test apis in postman.

## Local Deployment
You could run tests and it will use embedded ldap server and make sure port **8888** is not occupied before test.
It will also use embedded mongo during test and if you do not want to use please remove dependency `de.flapdoodle.embed:de.flapdoodle.embed.mongo` in `crud/pom.xml` and update **spring.data.mongodb.uri** in `crud/src/test/resources/application-test.properties`.
``` bash
mvn clean test or mvn clean test jacoco:report
```
The coverage test reports will be generated at: `crud/target/site/jacoco/index.html` and `ldap/target/site/jacoco/index.html`.

You could test api locally with embedded ldap server directly.
``` bash
mvn clean package -DskipTests
java -jar crud/target/api.jar
```

If you want to test docker based ldap server please move to docker folder and start openldap docker service.
``` bash
docker-compose up
```
You can also use `docker-compose up --build --force-recreate` to force recreate clean docker environment.

You can then open `http://<docker container ip 127.0.0.1 or 192.168.99.100 using docker toolbox>:8080` and login with Login DN=`cn=admin,dc=example,dc=org` and Password=`sm` to view ldap tree using phpldapadmin.
You can now run ldap service using docker profile(you may have to update ldap.url in `crud/src/main/resources/application-doceker.properties`)
``` bash
#run again if configuration is modified
mvn clean package -DskipTests 
java -jar crud/target/api.jar  --spring.profiles.active=docker
```
You must run `docker ldap configuration` request under ` /ldap-configurations`  folder in postman and update url to match your docker container ip.
This will make sure you will use docker ldap configurations.

## Heroku Deployment
Make sure you install all required tool in prerequisites.
``` bash
heroku login 
# Move to source folder contains Procfile and run command 
git init 
git add .
git commit -m "init"
heroku create
heroku addons:create mongolab
git push heroku master
heroku open
```
You can get the MongoDB URI by `heroku config:get MONGODB_URI` and you must import test data `./test_files/*.json` to your MongoDB heroku instance.

## Verification
Import postman collection `docs/sm-engine-crud-api_postman_collection.json` with environment variable **URL**(`http://localhost:8090/api/v1` for local application or `https://<heroku app name>.herokuapp.com/api/v1` for heroku app).
You can check example environment in `docs/sm-engine-crud-api_postman_environment.json`.

You can test requests with follow order: 
`{{URL}}/login with Admin role` in `/login` folder-> get token from response and set **TOKEN** environment variable in postman and verify other endpoints.
I recommend to verify `/headers/evaluate` folder at first to avoid test data issue.
Currently token will expire after 100 days so if you set up ldap/mongo rightly you may use **ROTOKEN** from example environment directly otherwise please use token after login with RO role user.

I also prepare more valid/invalid requests to help you verify application in **Invalid** folder.
Please go to http://editor.swagger.io/ to check the swagger definition `docs/swagger.yaml`.

Please note it will use form login from spring ldap security so you can input `ben/benspassword` in form and validate routes using browser at same time.

Live heroku url `https://blooming-sands-54354.herokuapp.com`.

You can validate create/update modules or policies to create or update referenced entities at same time and related postman requests have been updated.

- ***NOTE***: Here is the pre-defined list of username/password:
    - ***username***: ro_user1, ***password***: secret, ***role***: RO
    - ***username***: ro_user2, ***password***: secret, ***role***: RO
    - ***username***: rw_user1, ***password***: secret, ***role***: RW
    - ***username***: rw_user2, ***password***: secret, ***role***: RW
    - ***username***: admin_user1, ***password***: secret, ***role***: Admin
    - ***username***: admin_user2, ***password***: secret, ***role***: Admin