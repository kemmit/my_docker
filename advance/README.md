# JCasC in Docker

In the [basic example](https://github.com/kemmit/my_docker/tree/master/basic) should have given you a basic setup. To develop further, in this tutorial, we are going to add some things to this setup:

1. Add another user dev that is not an administrator and setup permissions using the [matrix-auth plugin](https://plugins.jenkins.io/matrix-auth/)
2. Create some initial Jenkins pipelines

## Add a dev user and permissions

First we need to install the [matrix-auth plugin](https://plugins.jenkins.io/matrix-auth/). To add plugins simply add them to the **plugins.txt**

```bash
configuration-as-code
matrix-auth
```

Second we can add the configurations to the config-as-code YAML file

```bash
jenkins:
  securityRealm:
    local:
      allowsSignup: false
      users:
        - id: "admin"
          password: "admin"
        - id: "dev"
          password: "dev"
  authorizationStrategy:
    globalMatrix:
      permissions:
        - "Job/Build:dev"
        - "Job/Cancel:dev"
        - "Job/Read:dev"
        - "Job/Workspace:dev"
        - "Overall/Administer:admin"
        - "Overall/Read:authenticated"
        - "Run/Replay:dev"
        - "Run/Update:dev"
```
## Create some initial pipeline jobs

The Jenkins config-as-code plugin uses the Jenkins [job-dsl plugin](https://plugins.jenkins.io/job-dsl/) plugin for handling jobs in Jenkins. 
First let's add this plugin to **plugins.txt** as well as the pipeline plugins that we need to create some pipeline jobs in Jenkins.

```bash
configuration-as-code
job-dsl
blueocean
workflow-job
workflow-cps
matrix-auth
```

Second let's add the configuration to the config-as-code YAML file. Split the job-dsl groovy scripts to create jobs away from the YAML, and this can be done by specifying a path to a file that will exist inside our Jenkins Docker image

```bash
# specify the seedjob groovy script that will create the  pipelines for us
jobs:
  - file: /usr/local/seedjob.groovy
```

The full config-as-code YAML file here:

```bash
jenkins:
  securityRealm:
    local:
      allowsSignup: false
      users:
        - id: "admin"
          password: "admin"
        - id: "dev"
          password: "dev"
  authorizationStrategy:
    globalMatrix:
      permissions:
        - "Job/Build:dev"
        - "Job/Cancel:dev"
        - "Job/Read:dev"
        - "Job/Workspace:dev"
        - "Overall/Administer:admin"
        - "Overall/Read:authenticated"
        - "Run/Replay:dev"
        - "Run/Update:dev"
  numExecutors: 1
jobs:
  - file: /usr/local/seedjob.groovy
```

Here is an example seedjob.groovy script to show you some of the things you can do:

```bash
pipelines = ["first-pipeline", "another-pipeline"]
pipelines.each { pipeline ->
    println "Creating pipeline ${pipeline}"
    create_pipeline(pipeline)
}
def create_pipeline(String name) {
    pipelineJob(name) {
        definition {
            cps {
                sandbox(true)
                script("""
pipeline {
    agent any
    stages {
        stage("Hello") {
            steps {
                echo "Hello from pipeline ${name}"
            }
        }
        stage("Goodbye") {
            steps {
                echo "Goodbye from pipeline ${name}"
            }
        }
    }
}
""")
            }
        }
    }
}
```

Finally we need to update our Dockerfile to copy the seedjob.groovy file into it:

```bash
# Dockerfile
​
FROM jenkins/jenkins:lts-jdk11
​
# copy the list of plugins we want to install
COPY plugins.txt /usr/share/jenkins/plugins.txt
# run the install-plugins script to install the plugins
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/plugins.txt
​
# disable the setup wizard as we will set up jenkins as code :)
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
​
# copy the seedjob file into the image
COPY seedjob.groovy /usr/local/seedjob.groovy
# copy the config-as-code yaml file into the image
COPY jenkins-casc.yaml /usr/local/jenkins-casc.yaml
# tell the jenkins config-as-code plugin where to find the yaml file
ENV CASC_JENKINS_CONFIG /usr/local/jenkins-casc.yaml
```

>Note: Please remove comments and blank lines in files to run properly

## Build

To build the docker image, you can use a PATH to build:

```bash
docker build D:\docker\my_docker\advance -t jenkins-casc:0.2
```

or use a URL to build:

```bash
docker build https://github.com/kemmit/my_docker#advance:master -t jenkins-casc:0.2
```

## Run

Run the docker image and map ports 49001:

```bash
docker run --rm --name jenkins-casc -p 49001:8080 jenkins-casc:0.2
```

Once you see the message Jenkins is fully up and running you can go to your browser <localhost:49001> and you should see your Jenkins instance running. Login with user admin and password admin and you should see your two pipelines. You can also login as user dev with password dev if you want the experience of a developer.