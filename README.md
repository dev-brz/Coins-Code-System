# Coins Code

This file provides an overview of Coins Code application.

## Description

Coins-Code is a payment system that simplifies process of payment by simply exchanging desired amount of funds between
two parties through usage of valid transaction codes.

## Structure

This system is currently divided into two applications described below.

### Coins Code API

This is an application written in Spring Framework. It provides logic for the system, mainly related to handling coins
transactions.

#### Running application

In order to run this application, open "coins-code-api" directory and execute `./mvnw spring-boot:run` command.

#### Stack

- Java 21
- Spring 6 (Boot, Web, Data JPA, Security, Batch)
- Hibernate
- H2 Database
- Liquibase, Lombok, MapStruct, Swagger
- JUnit 5, Mockito, ArchUnit

### Coins Code UI

This is an application written in Angular. It provides UI for interaction between users and the system.

#### Running application

In order to run this application, follow steps described in "README.md" file located inside "coins-code-ui" directory.

#### Stack

- Angular 17
- TypeScript, HTML, SCSS
- Angular Material
- RxJS
- NgRx Signals
- Karma, Jasmine

## Configuration

This section provides configuration steps which might need to be performed in order to make application work properly.

### Environmental variables

CD_USER_DEFAULT_PASSWORD: This is the default users password which will be used while creating sample users. It must be
set before starting the API.