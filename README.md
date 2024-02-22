# Coins Code
This file provides an overview of Coins Code application.
## Description
Coins-Code is a payment system that simplifies process of payment by simply exchanging desired amount of funds between two parties through usage of valid transaction codes. 
## Structure
This system is currently divided into two applications described below, which use external systems listed within "External Services" section.
### Coins Code API
This is an application written in Spring Framework. It provides logic for the system, mainly related to handling coins transactions.
#### Stack
- Spring Framework 6
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
### Coins Code UI
This is an application written in Angular. It provides UI for interaction between users and the system.
#### Stack
- Angular 17
- Angular Material
### External services
This section provides overview of other systems used by this system.
#### MySQL Database
Coins Code API uses MySQL database.