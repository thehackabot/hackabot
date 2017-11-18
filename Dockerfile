FROM maven:3.2-jdk-9-onbuild

CMD ["mvn", "exec:java"]
