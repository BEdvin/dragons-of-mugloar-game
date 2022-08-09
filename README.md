# Dragons of Mugloar game implementation

### Reference Documentation
For further reference, please consider the following sections:

* [Game REST API documentation](https://www.dragonsofmugloar.com/doc/)
* [Game task description](https://www.dragonsofmugloar.com/)

### Get started
The following guides illustrate how to use some features concretely:

#### Run application

* Using maven. Go to the root project directory and run following command: `./mvnw spring-boot:run -Dspring-boot.run.profiles={profile}`. Available profiles: `dev, prod`
* Using executable jar. Use maven `package` lifecycle to build executable jar. Then use following command from project root directory: `java -jar -Dspring.profiles.active={profile} target/game-0.0.1.jar`. Available profiles: `dev, prod`
* Make sure uou are using java 18.

#### Play game

* Open your browser and go to url: [http://localhost:8082/api/play-game](http://localhost:8082/api/play-game)
* Login page will appear. Use the following credentials to log get logged int:
  * user: `gameUser`
  * password: `password`
* Once you successfully logged in, the game will start to play.
* Check the page about status of the game. Each step is printed using SSE(server send events)
* In the end of game the final message will appear noticing about success or lose.
* Each time page is refreshed, the new game is started to play.
* There is H2 console available using the following url: [http://localhost:8082/h2-console](http://localhost:8082/h2-console)
  * JDBC URL: `jdbc:h2:mem:gamedb`
  * User Name: `gameUser`
  * Password: `password`

#### Available game settings

* Game contains several setting in application-dev/prod.yml files. Here are available game settings:
  * `goal` - Number of score to win the game. It's possible to win with goal 3000, maybe more.
  * `turnTolerance` - Is used to reduce number of turns in one round (one set of fetched messages).
  * `failureTolerance` - I's used to defining a moment when tasks selection strategy is switched to the easiest tasks strategy. E.g. if value is 2 then strategy is switched if there are 2 failed tasks in a sequence.
  * `minLives` - The number describes when to try to heal. If value is 2, then it's mean that after task completion player has only 2 lives it will try to buy healing item.
  * `gameStrategy` - There are implemented two different strategies to play the game:
    * `easiestTaskGameStrategy` - each round algorithm will take the easiest tasks with the best probability.
    * `highestRewardGameStrategy` - first the best reward having tasks are selected, but if there are several failures in sequence (`failureTolerance`), then it switches back to select easiest tasks and try to buy some artifact depending on gold balance.

#### Run tests

* Run unit tests use command from root project directory: `./mvnw test `
* Run unit and integration tests together use command from root project directory: `./mvnw verify`
* Unit tests are also run in `package` lifecycle as well as both (unit and integration tests) run in `install` and `deploy` lifecycle.
