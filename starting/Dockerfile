​
FROM jenkins/jenkins:lts-jdk11
​

COPY plugins.txt /usr/share/jenkins/plugins.txt

RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/plugins.txt
​

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
​
COPY jenkins-casc.yaml /usr/local/jenkins-casc.yaml

ENV CASC_JENKINS_CONFIG /usr/local/jenkins-casc.yaml