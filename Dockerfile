# Uses Open JDK as base image, the tag 8-jdk-alpine denotes the "version" we are using
FROM adoptopenjdk:11

# Set the working directory inside the container when run, here it is root
WORKDIR /app

# Take the jar from the build folder and add it as app.jar. This will require there to be a build already in that directory. Please modify the first path to
COPY build/libs/*.jar app.jar

# Invoke java executable and run the ap.jar file. There is only ONE CMD instruction in a Dockerfile and it is used as default to executing the container. The CMD form can vary, refer to the Docker Docs: Dockerfile Reference on formatting these shell commands.
ENTRYPOINT ["java", "-jar", "app.jar"]

# Remember how we mapped container ports to our host's port? This is how you expose the port you wish. It exposes PORT 8080.
EXPOSE 8080
