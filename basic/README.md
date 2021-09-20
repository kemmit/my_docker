# JCasC in Docker

This tutorial should give you a very basic initial setup for you to build a Docker image based on the community Jenkins image with a minimal config-as-code setup.

## Prerequisite

The only prerequisite for this tutorial is that you have Docker installed and are able to run the community Jenkins image.

For more information about Docker, see [here](https://www.docker.com).

To install Jenkins in Docker, run command:

```bash
docker pull jenkins/jenkins:lts-jdk11
```

## Folder organization

There are 2 things that we need to do:

1. Install the [config-as-code](https://github.com/jenkinsci/configuration-as-code-plugin#getting-started) plugin to Jenkins (plugins.txt)

2. Setup the environment variable CASC_JENKINS_CONFIG to tell Jenkins where to find the YAML configuration (jenkins-casc.yaml)

For some useful comments in **Dockerfile** file:

```bash
# Dockerfile
​
FROM jenkins/jenkins:lts-jdk11
​
# copy the list of plugins we want to install
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
# run the install-plugins script to install the plugins
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
​
# disable the setup wizard as we will set up jenkins as code :)
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
​
# copy the config-as-code yaml file into the image
COPY jenkins-casc.yaml /usr/local/jenkins-casc.yaml
# tell the jenkins config-as-code plugin where to find the yaml file
ENV CASC_JENKINS_CONFIG /usr/local/jenkins-casc.yaml
```

The list of plugins can be listed in a plugins.txt file which looks as follows:

```bash
# plugins.txt
​
configuration-as-code
```

And a basic jenkins-casc.yaml file which can contain something basic like:

```bash
# jenkins-casc.yaml
​
jenkins:
  securityRealm:
    local:
      allowsSignup: false
      users:
        # create a user called admin
        - id: "admin"
          password: "admin"
  authorizationStrategy: loggedInUsersCanDoAnything
```

## Build

To build the docker image, you can use a PATH to build:

```bash
docker build D:\docker\my_docker\basic -t jenkins-casc:0.1
```

or use a URL to build:

```bash
docker build https://github.com/kemmit/my_docker#basic:master -t jenkins-casc:0.1
```

## Run

Run the docker image and map ports 49001:

```bash
docker run --rm --name jenkins-casc -p 49001:8080 jenkins-casc:0.1
```

Once you see the message Jenkins is fully up and running you can go to your browser <localhost:49001> and you should see your Jenkins instance running. Success!