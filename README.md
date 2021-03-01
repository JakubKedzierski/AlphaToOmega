# Pictionary

Pictionary for 4 players (2 players temporally for a tests on one machine)

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Requirement](#requirement)
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
* Java 8 (project was built with java version "1.8.0_281")
	
## Setup 
You can build projcet using Maven 3.6:

```
$ cd pictionary
$ mvn package
$ cd target
# to run server:
$ java -jar .\pictionaryServer.jar
# to run 2 clients app:
$java -jar .\pictionaryClient.jar
$java -jar .\pictionaryClient.jar
```


## Author:
Jakub Kędzierski

## Attribution:
Game Icon made by: iconixar from https://www.flaticon.com/free-icon/drawing_2867989?term=drawing&related_id=2867989
