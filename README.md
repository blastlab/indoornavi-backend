

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

## Debugging

### VisualVM

Download and unpack VisualVM: https://github.com/oracle/visualvm/releases

Launch VisualVM with WildFly client libraries:

```bash
CORE_ID=$(docker-compose ps -q core)
docker cp ${CORE_ID}:/opt/jboss/wildfly /tmp/wildfly
./bin/visualvm -cp:a /tmp/wildfly/bin/client/jboss-cli-client.jar -J-Dmodule.path=/tmp/wildfly/modules
```

In VisualVM select File -> Add JMX Connection and pass parameters:

- Connection: service:jmx:remote+http://localhost:9990
- Use security credentials: check
- Username: admin
- Pasword: admin
- Do not require SSL connection: check

### async-profiler

Enter the core Docker container

```bash
docker-compose exec core bash
```

and run:

```bash
cd /opt
curl -OL https://github.com/jvm-profiling-tools/async-profiler/releases/download/v1.5/async-profiler-1.5-linux-x64.tar.gz
mkdir async-profiler
tar xf async-profiler-1.5-linux-x64.tar.gz -C async-profiler
cd async-profiler

PID=$(pgrep -f java)
./profiler.sh start ${PID}
# ...
./profiler.sh stop -f /tmp/flamegraph.svg ${PID}
```

Outside the Docker container run commands to extract SVG output graph:

```bash
CORE_ID=$(docker-compose ps -q core)
docker cp ${CORE_ID}:/tmp/flamegraph.svg .
```

Open `flamegraph.svg` file in browser for interactive graph.
