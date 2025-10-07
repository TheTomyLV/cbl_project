## Game idea
A multiplayer game where players cooperate to kill enemies that come towards them.

## Advanced topic choices
### Networking (current choice)
Create 2 players that can join the same grid world and can see each other
### Build tools (current choice)
Learn more about build tools, different types, how they work and other things.
### Git managment
How git works, explain most important things to make it work
### Enemy agents
Explore how enemy agents work
### Enemy pathfinding
Explore how enemy pathfinding works

## Todo - in priority
- [ ] Setup
    - [ ] Setup window
    - [ ] Add join/create lobby button
    - [ ] Add lobby screen
    - [ ] Make create lobby work
    - [ ] Make join lobby work
    - [ ] Display players on lobby screen
    - [ ] Make the start game work

- [ ] Game engine basics
    - [ ] Object rendering
    - [ ] Object translation
    - [ ] Object rotation
    - [ ] Camera
    - [ ] Collisions
    - [ ] Input
    - [ ] Sounds

- [ ] Networking
    - [ ] Both players are rendered on screen
    - [ ] Both players input work and they can see each other move around
    - [ ] Players can shoot bullets and both players see the bullets

- [ ] Enemies(Enemies is an abstract class that can be extended, for example add stronger units, ranged units etc.)
    - [ ] Enemies randomly spawn around for all clients
    - [ ] Enemies can take damage
    - [ ] Enemies can die
    - [ ] Enemies move towards players
    - [ ] Enemies can attack player

- [ ] Game loop
    - [ ] Score
    - [ ] End screen
    - [ ] "pause" menu

- [ ] Enviorment
    - [ ] Grid based enviorment