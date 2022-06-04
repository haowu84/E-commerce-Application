# E-commerce-Application with React, Spring Boot, MySQL

 - [Backend](#backend)
 - [Frontend](#frontend)
 - [Languages](#languages)
 - [Tools](#tools)
 - [Frameworks](#frameworks)

## Backend

### Backend: IDM Service
The Identity Management Service for user to Login and Register as well as access token creation, refreshing, and verification. 

### Backend: Movies Service
The Movie search Service allowing users to search for Movies and Persons using search parameters. 

### Backend: Billing Service
The Billing Service for user cart management and order creation, confirmation and history. 

### Backend: Gateway Service
The Gateway Service to bring together the `IDM`, `Movies` and `Billing` Service by either forwarding `IDM` Requests to `IDM` Service or Authenticating all other incoming requests with the `IDM` before forwarding them to either the `Movies` or `Billing` Services. 

## Frontend

### Frontend: Register
Start of Frontend. Develop the Register endpoint and make a call to our IDM Service. 

### Frontend: Search
Movies Search. Create a interactive search page to search through our movies. 

### Frontend: Full
Full implementation of our Backend services to our frontend through our gateway. 

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
