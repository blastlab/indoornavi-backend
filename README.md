

# IndoorNavi JEE app

## Installation for Windows 10

1. Get Docker for Windows: https://download.docker.com/win/stable/InstallDocker.msi
2. Install it and run
3. Clone repository: git@gitlab.blastlab.local:ble/serviceblbnavi.git
4. Run console in folder where you cloned project
5. Type: docker-compose up

## Important notes

1. We are using Wildfly 10.0.0 because we faced unexpected problems with integration tests (RestAssured) on 10.1.0

## DoD

Definition of Done for issues is as follows:

- Created unit tests where suitable - for code with logic. Unit tests are not required for plain methods without business logic,
for example endpoints only executing DAO methods and returning the result.
- Created integration tests. For backend these are REST API tests.
- Documented REST API. This documentation is generated automatically using Swagger.
All endpoints, parameters, DTOs and possible HTTP status codes should be described.
- Code passed code review, was integrated with other services (like fronted),
functionality was accepted by Product Owner (if suitable) and branch was merged to `development`.

Only issue for which code fulfills above rules can be found as done.

## Obsługa
##### Odpalenie aplikacji

``` docker-compose up ```

##### Odpalenie testów jednostkowych

``` mvn test ```

##### Odpalenie testów integracyjnych

``` mvn verify ```

##### Deployment aplikacji

``` mvn package ```

##### Submodule init
```$ git submodule update --init```
