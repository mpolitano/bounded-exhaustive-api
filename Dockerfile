FROM ubuntu:16.04

RUN apt-get update -y
RUN apt-get install -y build-essential
RUN apt-get install -y git
#RUN apt-get install -y ant
RUN apt-get install -y vim
#RUN apt-get install -y tree
RUN apt-get install -y openjdk-8-jdk
RUN mkdir /home/beapi

WORKDIR /home/user/beapi
COPY . /home/user/beapi/

RUN ./gradlew singleJar

