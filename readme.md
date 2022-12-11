# Demo Apache Ranger plugin

This git-repo includes two Java projects, related to:
- server-side implementation of Ranger plugin, which is going to work under Ranger Admin Tomcat service
- client-side implementation of Ranger plugin, which is represented as a simple REST API with Ranger client authorizer usage

### Details

- server-side part is compiled and tested against Apache Ranger v2.0.0 (ADPS, v1.0.2_b7-1)
- REST API uses Vert.x, Hibernate JPA & the H2 embedded database
- a naive logger is used in the server-side Ranger plugin part, due to the existing bug in Ranger Admin v2.0.0, related to the Apache Tomcat logging process

### How to build each Java project?

```sh
mvn clean package -U -Dmaven.test.skip=true -Drat.skip=true -X
```

### Apply servicedef to Ranger

```sh
curl -vk -u "admin:<ranger-admin-password>" -X POST \
    -H "Accept: application/json" -H "Content-Type: application/json" \
    --data @/sourcedir/servicedef.json \
   "http://<ranger-admin-host>:<ranger-admin-port>/service/plugins/definitions"
```

### Remove servicedef from Ranger

```sh
curl -vk -u "admin:<ranger-admin-password>" -X DELETE \
    -H "Accept: application/json" \
    -H "Content-Type: application/json" \
    "http://<ranger-admin-host>:<ranger-admin-port>/service/public/v2/api/servicedef/name/<ranger-service-name>"
```

### Get a list of installed services from Ranger with paging/offset

```sh
curl -vk -u "admin:<ranger-admin-password>" -X GET \
"http://localhost:6080/service/public/v2/api/servicedef?pageSize=25&startIndex=0" | jq
```

It's needed to define a new Ranger service for testing our demo plugin.

### Metadata query

```sh
curl -v --header "test-ranger-user: $testuser" http://<rest-api-host>:<rest-api-port>/api/v1/metadata
```

The metadata REST endpoint is the only endpoint which isn't handled by authorization logic.

### Test queries

```sh
curl -v --header "test-ranger-user: $testuser" http://<rest-api-host>:<rest-api-port>/api/v1/data

curl -v --header "test-ranger-user: $testuser" http://<rest-api-host>:<rest-api-port>/api/v1/datetime
```

**$testuser** is a test user, which was imported by usersync service of Apache Ranger and which was defined in the service policy.

### ADPS (Arenadata Platform Security, v1.0.2_b7-1) details

If you're using the ADPS distro from Arenadta, Ranger plugins location is here:

```sh
sudo ls -lah /usr/lib/ranger-admin/ews/webapp/WEB-INF/classes/ranger-plugins/
```

Here, you need to create a new directory and upload the JAR artifact to it & restart Ranger Admin service:

```sh
scp test-ranger-plugin-0.1.0.jar myuser@rangerhost:.

sudo mkdir /usr/lib/ranger-admin/ews/webapp/WEB-INF/classes/ranger-plugins/testrestapi

sudo cp -v ~/test-ranger-plugin-0.1.0.jar /usr/lib/ranger-admin/ews/webapp/WEB-INF/classes/ranger-plugins/testrestapi/test-ranger-plugin-0.1.0.jar

sudo ls -lah /usr/lib/ranger-admin/ews/webapp/WEB-INF/classes/ranger-plugins/testrestapi/

sudo systemctl restart ranger-admin
systemctl status ranger-admin
```

Also, it's needed to set the next value for **ranger.supportedcomponents** . By default, the ADPS distro prevents custom plugins setup. First, edit next file:

```sh
sudo vim /etc/ranger/admin/conf/ranger-admin-site.xml
```

And set next value:

```xml
<property>
    <name>ranger.supportedcomponents</name>
    <value />
</property>
```
