# Pictionary

Pictionary for 4 players (2 players temporally for a tests on one machine)

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Requirement](#requirement)
* [Documentation](#documentation)
* [Setup](#setup)
* [Attribution](#attribution)
* [Author](#author)

## General info
This project is simple pictionary game. There are 2 types of player: 
- host: try to present word and draw it on a board 
- listener: try to guess what the word is
There are 2 rounds and the winner of the game is player who gets more points.
To start game you need to start server and then run 2 clients app.
	
## Technologies
Project is created with:
* Java JDK 8
* JavaFx
* client-server architecture
* socket programming
* projekt lombok: https://projectlombok.org/
* Jackson: https://github.com/FasterXML/jackson
* JUnit

## Requirement
* Windows 10
* Java JDK 8 (project was built with java version "1.8.0_281")

## Documentation
* JavaDocs documentation: https://jakubkedzierski.github.io/Pictionary/apidocs/index.html
* Maven summary documentation: https://jakubkedzierski.github.io/Pictionary/summary.html

## Setup 
# Release
Download and run project .jar files from release: https://github.com/JakubKedzierski/Pictionary/releases/tag/v1.0-beta

Remember that you have to run firstly server:
```
$java -jar .\pictionaryServer.jar
```
and then clients app. Present realase is made for 2 players (you have to run 2 clients app to start game)

Open new console and start 1. client
```
$java -jar .\pictionaryClient.jar
```
Open new console and start 2. client
```
$java -jar .\pictionaryClient.jar
```

# Maven
You can build projcet using Maven 3.6:

```
$ cd pictionary
$ mvn package
$ cd target
# to run server:
$ java -jar .\pictionaryServer.jar
# to run 2 clients app (each in new console):
$java -jar .\pictionaryClient.jar
$java -jar .\pictionaryClient.jar
```


## Author:
Jakub Kędzierski

## Attribution:
Game Icon made by: iconixar from https://www.flaticon.com/free-icon/drawing_2867989?term=drawing&related_id=2867989
