FROM jenkins/jenkins:lts-jdk11
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
COPY seedjob.groovy /usr/local/seedjob.groovy
COPY jenkins-casc.yaml /usr/local/jenkins-casc.yaml
ENV CASC_JENKINS_CONFIG /usr/local/jenkins-casc.yaml