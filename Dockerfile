FROM ubuntu:18.04
LABEL maintainer="github@lrsb.xyz"

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && apt-get install -q -y --fix-missing \
	openjdk-11-jdk \
	curl \
	xorg \
	openbox \
	firefox \
	git \
	xvfb \
	unzip

RUN curl -s -L https://bitbucket.org/meszarv/webswing/downloads/webswing-2.6.1.zip > webswing.zip && \
	unzip webswing.zip && \
	rm webswing.zip && \
	mv webswing-* webswing

COPY webswing.config webswing/webswing.config
COPY client/target/client-1.0.jar webswing/demo/ing-sw/ing-sw-2019-7-1.0.jar
COPY client/target/lib/ webswing/demo/ing-sw/lib/

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/

CMD Xvfb :99 & export DISPLAY=:99 && \
    cd webswing && java -jar webswing-server.war -p $PORT -s $PORT -h 0.0.0.0
