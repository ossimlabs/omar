Steps:

1.  Build jar -  `./gradlew assemble`
2.  Build image - `docker build -t oldmar-app:latest .`
3.  Run image - `docker run --rm -p 8080:8080 oldmar-app`
