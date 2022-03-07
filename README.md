[![Build Status](https://travis-ci.org/apetenchea/SpaceInvaders.svg?branch=master)](https://travis-ci.org/apetenchea/SpaceInvaders)

# SpaceInvaders - a multiplayer game made from scratch.
I've always wondered what is going on behind a multiplayer game, so I decided to make one in order to find out!
Inspired by the well known classic, up to 3 players may team up in an online battle agains the invaders.

The required Java version is [**Java 8**](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) or newer.

## Building
Using maven:
```
mvn package
```

Just compiling the source into *.class* files:
1. Go to where the source files are (src/main/java)
2. Run:
  ```Shell
  mkdir target
  ```
3. Replace <i>path_to_gson</i> with the path to <i>gson-2.8.0.jar</i> and run::
  ```Shell
  javac -d target -classpath path_to_gson -sourcepath . spaceinvaders/SpaceInvaders.java
  ```

## Usage
The game consists of two parts: a client and a server.

### Server
Starting up:
```
java -jar spaceinvaders*.jar server port [verbose]
```
The script [server.sh](https://github.com/apetenchea/SpaceInvaders/blob/master/server.sh) contains an example of how to start a server on your machine, on port 5412. If you add the ***verbose*** argument, the logging is going to be more verbose.
Using the *.jar* file is recommended. Because when running a `.jar` Java handles resourced differently from when you are running *.class* files, a small modification in the code is required in the second case.
In the class [Config](https://github.com/apetenchea/SpaceInvaders/blob/master/src/main/java/spaceinvaders/Config.java), there is a field:
```Java
private static final transient Boolean JAR_FILE = true;
```
This field must be set to `false` in case the target is not a *.jar* file.

### Client
Starting up:
```
java -jar spaceinvaders*.jar client [verbose]
```

The script [client.sh](https://github.com/apetenchea/SpaceInvaders/blob/master/client.sh) contains an example of how to run the client. If you add ***verbose*** as an argument, the logging is going to be more verbose. In order to play the game, you must choose an username, how many players your team shall you team have, and then connect to a running server.

## Configuration files
- [app.json](https://github.com/apetenchea/SpaceInvaders/blob/master/src/main/resources/config/app.json) contains runtime information about the application. In case of a lan party, the ***lanGame*** field should be set to ***true***, thus configuring the game to prefer the UDP protocol over TCP.
- [client.json](https://github.com/apetenchea/SpaceInvaders/blob/master/src/main/resources/config/client.json) contains the client's configuration.
- [game.json](https://github.com/apetenchea/SpaceInvaders/blob/master/src/main/resources/config/game.json) is used to configure the gameplay.
- [resources.json](https://github.com/apetenchea/SpaceInvaders/blob/master/src/main/resources/config/resources.json) is used to locate resources.

## Code
The code follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) and is checked using [checkstyle](http://checkstyle.sourceforge.net/).

## Documenation
Documenation is generated using [javadoc](http://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html).
The script [getDoc.sh](https://github.com/apetenchea/SpaceInvaders/blob/master/getDoc.sh) handles this.

## Credits for the materials used in this game
Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a>, <a href="http://www.flaticon.com/authors/madebyoliver" title="Madebyoliver">Madebyoliver</a>, <a href="http://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a>, <a href="http://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> are licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a>.
Some of the original icons were edited by [luciamoga](https://github.com/luciamoga).

## Screenshot
![Screenshot.png](https://github.com/apetenchea/SpaceInvaders/blob/master/screenshot.png)
