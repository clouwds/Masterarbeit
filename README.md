# newsviz-frontend-spring

## Project setup
#### Requirements

- Maven 3 
  (Download Source ZIP from: https://maven.apache.org/download.cgi)
- Java 1.8 
  (Download from: https://www.oracle.com/technetwork/java/javase/downloads/index.html)
- Apache
- MySQL
- Tomcat 

I recommend using "xampp" for Apache, MySQL and Tomcat (https://www.apachefriends.org/de/download.html)

#### Environment Variables

Set up the environment variables for Tomcat, Maven and Java JDK correctly.
It might be necessary to also add npm to the path.
```
CATALINA_HOME -> tomcat directory
JAVA_HOME -> JDK directory
M2_Home -> maven directory
```

Add all of them to "Path", e.g 

```
%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%CATALINA_HOME%\bin;
```


## Get started

#### Maven Build

To install npm and node, if not installed yet, and build the project navigate to root directory "newsviz-ma-spring" and run:
```
mvn clean install 
```

#### Backend

1. Start Apache

2. Start MySQL 
   - 2.1. open "http://localhost/phpmyadmin/" in Browser
   - 2.2. create database "news_data"
   - 2.3. if not exists, create user "root" with password ""
   
3. Run JAR with Run Configuration or from command line
    - 3.1 Create Run Configuration for JAR Application 
        - Path to JAR: Path to the packaged war (in /target directory), e.g. D:\w\newsviz-ma-spring\backend\target\backend-1.0-SNAPSHOT.war
        - Working directory: root directory "newsviz-ma-spring", e.g. D:\w\newsviz-ma-spring
    
    - 3.2 Run JAR from Commandline
    
        ```
        java -jar backend-1.0-SNAPSHOT.war
        ```

#### Frontend

Start DEV Server
```
npm run dev
```

Stop Dev Server
```
Strg + C
```
