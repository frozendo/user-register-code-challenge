# User Register - Code Challenge

This project is a part of a software challenge, and its main requirements include:

- The API must have two endpoints:
  - `GET /api/users` lists all users from the database.
  - `POST /api/users` create new users.
- Users with **admin** role can list and create users.
- Other roles can list all users, but can't create.
- The process of authorization must use [Open Policy Agent (OPA)](https://www.openpolicyagent.org/).
- Users without authentication have no access to the application.
- The application must be run on the Kubernetes cluster.

## Stack

- Gradle
- Flyway
- Java 17
- Spring
- Open Policy Agent (OPA)
- Docker
- Kubernetes

## How To Run Application

Follow the steps outlined below to run the application.

### Prerequisites

- [Docker](https://docs.docker.com/engine/install/) installed
- [kubectl](https://kubernetes.io/pt-br/docs/reference/kubectl/) installed
- [Kind](https://kind.sigs.k8s.io/) installed

> It's also possible to run the application using minikube,
> but we need to make some adjustments in next steps.

### Prepare application

This application already has a valid image on [Docker Hub](https://hub.docker.com/repository/docker/frozendo90/user-register/general) that can be utilized on Kubernetes.
However, if we prefer to generate a new image, we can use these following commands:

1. Compile the application using the gradle wrapper:
```
./gradlew clean build
```

2. Compile a new Docker image with `docker build`:
```
docker build -t frozendo90/user-register:latest .
```

3. Push the new image to a Docker registry, such as Docker Hub.
```
docker push frozendo90/user-register:latest
```

With this, we have a new image on the registry that can be used in Kubernetes.

> When we run `./gradlew clean build`, the application is compiled, and all tests are executed.
> Alternatively, if we want to run only the tests, we can use `./gradlew test`.

### Run Application

First, the following commands using Kind to run the Kubernetes cluster locally.
We can also run on Minikube. See [this section](https://github.com/frozendo/user-register-code-challenge?tab=readme-ov-file#using-minikube).

1. Configure the environment. This shell script starts a Postgres database on Docker and creates a Kubernetes cluster with Kind.
```
sh ./setup/environment.sh
```

2. After this, we can execute the script to configure Kubernetes and create all the components.
```
sh ./setup/create-components.sh
```

3. To execute requests to our API in Kubernetes, we need to enable Ingress on Kind. First, run the script below
```
sh ./setup/enable_ingress.sh
```

4. Now we need to configure our machine by obtaining the Kind container IP.
```
docker container inspect challenge-cluster-control-plane --format '{{ .NetworkSettings.Networks.kind.IPAddress }}'
```

5. Using the IP obtained in the last command, map this IP to the application host in `/etc/hosts` file.
```
vi /etc/hosts

# add this new line
<your_IP> app.user-register
```

With this, the application is running and able to receive requests.

#### Using minikube

"We don't need to run the `./setup/environment.sh` script when using Minikube. So, before executing the cluster, start a Postgres container:"
```
docker run --name postgres-db -p 5432:5432 -e POSTGRES_PASSWORD=root -e POSTGRES_USER=postgres --rm -d postgres:14-alpine
```

Now, we can initiate Minikube and configure Ingress to work using this [documentation](https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/).

With minikube running and Ingress enabled, execute the script `sh ./setup/create-components.sh` to configure all the necessary components.

## Using application

"The application creates the user `alice@test.com` upon startup, allowing us to make requests with this user.

List all users:
```
curl -H 'Authorization: alice@test.com' 'http://app.user-register/api/users'
```

Create a new user:
```
curl -X POST -H 'Authorization: alice@test.com' -H 'content-type: application/json' 'http://app.user-register/api/users' --data '{"email": "bob@test.com", "name": "Bob", "role": "COMMON"}'
```
