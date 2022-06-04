# CS122B - Projects in Databases and Web Applications

 - [Homework](#homework)
 - [Backend](#backend)
 - [Frontend](#frontend)
 - [Activities](#activities)
 - [Languages](#languages)
 - [Tools](#tools)
 - [Frameworks](#frameworks)

## Homework

### Homework 1: Setup Service
Empty service to practice setting up project files and enviroment \
[Starter Template](https://github.com/klefstad-teaching/CS122B-HW1-Setup-Service-Starter)

### Homework 2: Basic Service
Basic Spring Boot service to learn the basics of Spring. \
[Starter Template](https://github.com/klefstad-teaching/CS122B-HW2-Basic-Service-Starter)

## Backend

### Backend 1: IDM Service
The Identity Management Service for User to Login and Register as well as AccessToken creation, refreshing, and verification. \
[Starter Template](https://github.com/klefstad-teaching/CS122B-BE1-IDM-Service-Starter)

### Backend 2: Movies Service
The Movie search Service allowing Users to search for Movies and Persons using search parameters. \
[Starter Template](https://github.com/klefstad-teaching/CS122B-BE2-Movies-Service-Starter)

### Backend 3: Billing Service
The Billing Service for User cart management and order creation, confirmation and history. \
[Starter Template](https://github.com/klefstad-teaching/CS122B-BE3-Billing-Service-Starter)

### Backend 4: Gateway Service
The Gateway Service to bring together the `IDM`, `Movies` and `Billing` Service by either forwarding `IDM` Requests to `IDM` Service or Authenticating all other incoming requests with the `IDM` before forwarding them to either the `Movies` or `Billing` Services. \
[Starter Template](https://github.com/klefstad-teaching/CS122B-BE4-Gateway-Service-Starter)

## Frontend

### Frontend: Starter
Starter code that will be used through all three parts of the front end. \
[Starter Template](https://github.com/klefstad-teaching/CS122B-FE-Starter)

### Frontend 1: Register
Start of Frontend. Develop the Register endpoint and make a call to our IDM Service. \
[Documentation](https://github.com/klefstad-teaching/CS122B-FE-Starter/blob/main/FRONTEND-1.md)

### Frontend 2: Search
Movies Search. Create a interactive search page to search through our movies. \
[Documentation](https://github.com/klefstad-teaching/CS122B-FE-Starter/blob/main/FRONTEND-2.md)

### Frontend 3: Full
Full implementation of our Backend services to our frontend through our gateway. \
[Documentation](https://github.com/klefstad-teaching/CS122B-FE-Starter/blob/main/FRONTEND-3.md)

## Activities

### Activity 1: Spring
An intro into the basics of Spring \
[Repository](https://github.com/klefstad-teaching/CS122B-A1-Spring)

### Activity 2: Security
Service security using: Password Salt + Hashing, JSON Web Tokens, and Refresh Tokens. \
[Repository](https://github.com/klefstad-teaching/CS122B-A2-Security)

### Activity 3: Database
Connecting to SQL Databases through Spring's NamedParameterJDBCTemplate. \
[Repository](https://github.com/klefstad-teaching/CS122B-A3-Database)

### Activity 4: SQL
Advanced SQL query creation with dynamic queries. \
[Repository](https://github.com/klefstad-teaching/CS122B-A4-SQL)

### Activity 5: Stripe
Communication with the Stripe API with the Stripe SDK for managing user payments. \
[Repository](https://github.com/klefstad-teaching/CS122B-A5-Stripe)

### Activity 6: Reactor
Reactor Core with dealing with Mono and Flux calls. \
[Repository](https://github.com/klefstad-teaching/CS122B-A6-Reactor)

## Languages

### Java OpenJDK
We are using the **OpenJDK** of Java. \
Version 8 \
[Adoptium Download](https://adoptium.net/?variant=openjdk8) \
[Azul Zulu Download](https://www.azul.com/downloads/?version=java-8-lts&package=jdk)

### SQL
MySQL is the Dialect for this project. \
Version 8.0.x \
[MySQL Community Server Download](https://dev.mysql.com/downloads/mysql/)

### Javascript
Project is written to target Node.js. \
Version 16.14.x LTS \
[Node.js Download](https://nodejs.org/en/)

### JSON
The primary object representation we will be using for communication between our services and our frontend \
[JSON](https://www.json.org/json-en.html)

### HTML
While we will not be using raw html, React uses `JSX` that represent html components. \
[React JSX](https://reactjs.org/docs/introducing-jsx.html)

### CSS
CSS can either be written as css files and imported directly, or made with `styled-components`. \
[styled-components](https://styled-components.com/)

## Tools

### JetBrains Toolbox
Manages all JetBrains applications. You can use this to download `IntelliJ IDEA Ultimate` and `WebStorm`. \
[Download](https://www.jetbrains.com/toolbox-app/)

### IntelliJ IDEA Ultimate
IDE for developing Java projects with internal support for database management. \
[Website](https://www.jetbrains.com/idea/)

### WebStorm
IDE for developing Javascript projects. \
[Website](https://www.jetbrains.com/webstorm/)

### Postman
A platform for creating REST calls to our services for testing. \
Note: The web client will not work as it does not allow calls to `localhost` please download the app instead. \
[Download](https://www.postman.com/downloads/) 

### GitHub Desktop
A UI for Git that works with GitHub projects. \
[Download](https://desktop.github.com/) 

## Frameworks

### Spring Boot
A Java framework that helps develop web applications. \
[Website](https://spring.io/projects/spring-boot) 

### React
A Javascript framework that helps with web UI development by helping us easily control the DOM of the website. \
[Website](https://reactjs.org/) 

## CS122B Dependencies

### CS122B-Parent
Holds the Parent POM for Maven to keep all dependency versions consistent. \
[Repository](https://github.com/klefstad-teaching/CS122B-Parent)

### CS122B-Core
Holds common modules that are used through the project, such as Managers, Filters, and all Result constants for the Backend Endpoints. \
[Repository](https://github.com/klefstad-teaching/CS122B-Core)
