## Game idea
A multiplayer game where players cooperate to kill enemies that come towards them.

## Advanced topic choices
### Networking (current choice)
Create 2 players that can join the same grid world and can see each other
### Build tools (current choice)
Learn more about build tools, different types, how they work and other things.
### Enemy agents
Explore how enemy agents work
### Enemy pathfinding
Explore how enemy pathfinding works
### Game engine
Learn how a game engine functions

## Todo - in priority
- [ ] Classed that need to be refactored
    - [ ] Mouse.java - needs to have the correct position, as right now it's attached to the frame, but needs to be attached to the panel
    - [ ] Client.java
    - [ ] Server.java
    - [ ] Packet.java - needs to send the current scene and also add events
    - [ ] AudioClip.java and AudioPlayer.java needs to be handled better, as the initial loading of the audio hangs the game
    - [ ] Engine.java - needs to make adding and removing objects work
    - [ ] Scene.java - needs to only paint gameObjects on update call
    - [X] Sprite.java - Needs to be able to get the image index for serialization

- [ ] Setup
    - [x] Setup window
    - [x] Add join/create lobby button
    - [ ] Add lobby screen
    - [x] Make create lobby work
    - [x] Make join lobby work
    - [ ] Display players on lobby screen
    - [x] Make the start game work

- [ ] UI
    - [ ] UI is scaled properly

- [ ] Game engine basics
    - [X] Object rendering
    - [X] Object translation
    - [X] Object rotation
    - [X] Camera
    - [ ] Collisions
    - [X] Input
    - [ ] Sounds
    - [ ] Better camera
    - [X] Sprite assets are loaded in at start

- [ ] Networking
    - [X] Both players are rendered on screen
    - [X] Both players input work and they can see each other move around
    - [ ] Players can shoot bullets and both players see the bullets
    - [ ] Handle player disconnects
    - [ ] Server owned entities

- [ ] Enemies(Enemies is an abstract class that can be extended, for example add stronger units, ranged units etc.)
    - [ ] Enemies randomly spawn around for all clients
    - [ ] Enemies can take damage
    - [ ] Enemies can die
    - [ ] Enemies move towards players
    - [ ] Enemies can attack player

- [ ] Game loop
    - [ ] Score
    - [ ] End screen

- [ ] Enviorment
    - [ ] Grid based enviorment