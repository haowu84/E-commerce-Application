# E-commerce-Application with React, Spring Boot, MySQL

 - [Backend](#backend)
 - [Frontend](#frontend)
 - [Languages](#languages)
 - [Tools](#tools)
 - [Frameworks](#frameworks)

## Backend

### IDM Service
The Identity Management Service for user to Login and Register as well as access token creation, refreshing, and verification. 

### Movies Service
The Movie search Service for user to search for movies and persons using search parameters. 

### Billing Service
The Billing Service for user to manage cart and order creation, confirmation and history. 

### Gateway Service
The Gateway Service to bring together the `IDM`, `Movies` and `Billing` Service by either forwarding `IDM` Requests to `IDM` Service or Authenticating all other incoming requests with the `IDM` before forwarding them to either the `Movies` or `Billing` Services. 

## Frontend

### Login Page 

<table>
  <thead>
    <tr>
      <th align="left" width="1100">📄&nbsp;&nbsp;Login</th>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates user login. It should be the first thing a user sees when they first visit your website. Users must be logged in before performing any other actions on your website. If the login process succeeds, the user will be redirected to the home page.</td>
    </tr>
  </tbody>
</table>

### Register Page 

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Register</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates user account registration. If the register process succeeds, the user will be redirected to the login page.</td>
    </tr>
  </tbody>
</table>

### Search Page 

<table>
  <thead>
    <tr>
      <th colspan="2"  align="left" width="1100">📄&nbsp;&nbsp;Search</th>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td  colspan="2" align="left" >This view allows a logged-in user to search for movies. It should include a search bar, filters, pagination, and a results area. It will make a REST call to the <code>/movies/search</code> endpoint of your Movies microservice. This page view should include at least the following elements:</td>
    </tr>
    <tr>
      <td align="left">Search Bar</td><td align="left" >This area is where users can enter text pertaining to movies they want to view.</td>
    </tr>
    <tr></tr>
    <tr>
      <td align="left" >Filters</td><td align="left" >Users will be able to search for movies by <code>title</code>, <code>year</code>, <code>director</code>, and / or <code>genre</code>.</td>
    </tr>
    <tr></tr>
    <tr>
      <td align="left" rowspan="9">Pagination</td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to select the sort by <code>title</code> (default selection), <code>rating</code>, or <code>year</code></td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to select the order by <code>asc</code> (default selection) or <code>desc</code></td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to select the amount of movies to list <code>10</code> (default selection), <code>25</code>, <code>50</code> or <code>100</code></td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to go to the <code>next</code> or <code>previous</code> page of the search results</td> 
    <tr></tr>
    <tr>
      <td align="left" >Results Area</td> <td align="left" >All results from a search query will be displayed here as a table. The table will have columns corresponding to each movie's title, year, and director. A new search should update these results.</td>
    </tr>
  </tbody>
</table>


### Movie Detail Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Movie Detail</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
     <td align="left" >This page allows a user to view all the details of a single movie. The user must be allowed to add the movie to their shopping cart with a specified quantity.</td>
    </tr>
  </tbody>
</table>


### Shopping Cart Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Shopping Cart</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates a user viewing the items they have in their cart. Cart items should display the title, quantity, and total price of each movie (quantity * price). The total amount (sum of all costs) must be displayed. Users must be allowed to update the quantity of each item in their cart or remove items entirely. Updating the cart should also update the displayed cart items and prices. This page should provide a link to <code>Checkout Page</code> using Stripe for when the user is ready to finalize their purchase.</td>
    </tr>
  </tbody>
</table>

### Order History Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Order History</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates a user viewing all of their previous purchases (It will show max 5 as this is what the endpoint will show). Each history entry should display the date and amount paid for a given order.</td>
    </tr>
  </tbody>
</table>

## Checkout Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Checkout</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page will show the <code>CheckoutForm.jsx</code> provided by Stripe found here: <a href="https://stripe.com/docs/payments/quickstart">Custom Payment Flow</a>. (start by creating our <code>PaymentIntent</code> (by calling our <code>GET /order/payment</code> endpoint) to get <code>paymentIntentId</code> and <code>clientSecret</code> and then after the payment is complete (by calling <code>retrievePaymentIntent(clientSecret)</code> promise) call <code>POST /order/complete</code> endpoint to complete the order.</td>
    </tr>
  </tbody>
</table>


## Languages

### Java OpenJDK
Version 8 \
[Adoptium Download](https://adoptium.net/?variant=openjdk8) \
[Azul Zulu Download](https://www.azul.com/downloads/?version=java-8-lts&package=jdk)

### SQL
Version 8.0.x \
[MySQL Community Server Download](https://dev.mysql.com/downloads/mysql/)

### Javascript
Version 16.14.x LTS \
[Node.js Download](https://nodejs.org/en/)

### JSON
The primary object representation for communication between our services and our frontend \
[JSON](https://www.json.org/json-en.html)

### HTML
[React JSX](https://reactjs.org/docs/introducing-jsx.html)

### CSS
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
