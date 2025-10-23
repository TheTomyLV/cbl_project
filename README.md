## Game idea
A multiplayer game where players cooperate to kill enemies that come towards them.

## Advanced topic choices
### Networking
#### Network communication type
There are two types of communication in networking tcp and udp, we already knew the basics of how they work.
We decided to go with udp, as that is most used in real time games, which this is.
Udp is a connection, where packets sent can be lost and not delivered, as that is not checked when sending out a packet, unlike tcp where it waits when sending a packet to make sure it's delivered, which causes delays. Udp does not have this problem as it just sends the packet and forgets about it. 
We used this tutorial [A Guide to UDP In Java](https://www.baeldung.com/udp-in-java) to set up a basic server client communication. 

#### Connection setup
After that a basic engine was setup to render objects on screen.

Now we needed to figure out how to send these created objects over to other clients. We chose client authorative approach, which means that the server trusts the clients, that all the object logic is correct. This was chosen as server only authority would mean that player inputs would have delays until the server sends a confimation of their actions. 

We setup that the player that creates a server also connects to that server as a client, all other clients connect to this server. 

#### GameObject sending over the network
A snapshot of the state of gameObjects should only be sent once and no acknowledgment is needed for that, as if a package is not received, it will get replaced by a more recent one.

##### Writing GameObjects to byte array
For sending objects over the network we needed to find a way to convert these objects into a byte array, as that is the only accepted format to be sent over the network. We used DataOutputStream to write all the basic data types to a byte array [DataOutputStream in java](https://www.geeksforgeeks.org/java/dataoutputstream-in-java/) and added functions in GameObjects to write all the necessary data for correct display, this also required an implementation of unique identification of gameObjects, so that the server and client knows if that object is new or already added, we used UUID which generate a unique identification number for each gameObject, this was also used for Clients to identify them.


#### Reading GameObjects from byte array
Now we needed to find a way to read this byte array and reconstruct the received gameObjects, for that we used DataInputStream which does the same thing as DataOutputStream, but now it reads basic data types from the byte array. This implementation now allowed for players to be displayed on all client screens, for testing purposes we also made players shoot bullets, but these were just client instances, which in no way could interact with other clients. 

After that we added server objects, which would be enemies for example, as no player should own enemies. Also any object received from the network is just a basic GameObject, and there was no way to reconstruct what class it actually comes from. To fix this, so that the server or the client knows what object it has received loaded all classes that extend GameObject and assigned integer values to them, so that they can be sent over the netowork [Finding All Classes in a Java Package](https://www.baeldung.com/java-find-all-classes-in-package).

#### Network events
After that was the hardest thing to implement, interaction between network entities. As a player bullet changing enemy health would have no effect, and even if it did, if multiple players changed the same enemy health at the same time, which health values would be the correct one? None of them, as the damage taken should be all added up.

For this we implemented network events, which at high level are just function calls for the other side of the network, for example a player shoots a bullet and calls an event shoot, the server receives it and creates a server instance of a bullet. But the problem is that if an event is sent once over a udp packet, it could be lost and never executed. For this we needed an implementation that makes sure an event is executed once and only once. 

In basic we setup an event array that is sent over the network, when the other party receives it, it executes it and marks it as executed and starts sending back that the event has been executed. When the party that sent the request received the acknowledgment, it stops sending that request. But now there is a problem of what function to execute when an event is received, writing function in an if else or switch statement would make the code hard to maintain and use if there are alot of events. For this we used annotations which are a really powerful tool to automate this process, but more of that in the annotations section.

#### Finishing the game
With gameObject sending over the network and network events done, we could now finish the game, as all the required tools were now in place. Some notable uses of network events include shooting, which has 3 network event types ("shoot_pistol", "shoot_shotgun", "shoot_minigun") and these events just fire when a player presses left-click and also includes player position and rotation data to know where to spawn the bullets and when the server receives any of the events it creates a bullet pattern appropirate for the gun. There is also "player_hit" event which is sent from the server by the enemy and includes the damage the player should take and when the client receives it, it just subtracts that from it's health.

### Annotations
For the network events we knew that there was a way to mark functions to the correct network event type. And for that we found out about custom annotations, that can be added to functions for example. We used [Annotations in Java](https://www.geeksforgeeks.org/java/annotations-in-java/) to setup annotations for functions, but of course this does nothing if the program does not know about them or what to do with them. For this we needed to create a function simmular for finding all the classes that extend GameObject. 

When all the functions were found, they were automatically added to a hashmap that containts the event type and the function to execute in form of a handler. Implementing this also allowed us to send arguments with events, as when you know the type of event, you also know the function, and java has a method for getting all arguments of a function which allowed for a simmular approach to turning the network event arguments into a byte array and back. 

Turning the arguments to a byte array also requires us to know how that data type needs to be converted, for that we only added basic data types as arguments as int, float, boolean etc. and also Vector2 as adding that was trivial, as it just consists of 2 float values. Other data types could also be possible to be added like GameObject as it already has a function to turn it into a byte array, but we chose not to do it, as to not encourage excesive data sending over the network. But for example sending UUID would be nice, but we didn't implement it, as we didn't need it for our game.

### Final remark
We think the game engine has turned out great. There are definetly many aspects of it that can be improved/optimized/fixed. As this is the first networking we have done, there were many instances of the server or client crashing, because implementing the networking we did not think about concurrent modification much, and this can be improved greatly, this also counts for gameObject rendering as the swing thread is different from the engine thread.
Here are some things that could be added or improved:
- [ ] Networking packet structure, right now it is very messy as things were just added on top of it. In hindsight gameObjects should have also be sent as network events, just with a little modification of being non-important.
- [ ] Structure of client and server. Right now the server is a little too closely linked to it's client and should be completely seperated from it.
- [ ] Scene rendering. GameObjects might flicker sometimes for a frame. Also the structure of client gameObjects and server recieved ones should be handled better.
- [ ] Scenes were added to the engine, but right now they would function poorly with the given network structure, as there is no way to know what scene a client is in and server doesn't even come from a scene, but uses it's client scene to initalize objects, which goes back to the second point.
- [ ] The game scene should adapt to the screen size, so that it zooms in or out depending of the window size.

## How to play
    In the main menu, the host writes a port and starts a game.
    Other clients write a port and server ip address to join.
    There are 3 different weapon types, that you can select with 1-3.
    When the player dies, he respawns with 1 second invincibility frames.


## Todo - in priority
- [X] Write about advanced topics
    - [X] Networking
    - [X] Annotations

- [ ] Classed that need to be refactored
    - [X] Mouse.java - needs to have the correct position, as right now it's attached to the frame, but needs to be attached to the panel
    - [ ] Client.java
    - [ ] Server.java
    - [ ] Packet.java - needs to send the current scene and also add events
    - [X] AudioClip.java and AudioPlayer.java needs to be handled better, as the initial loading of the audio hangs the game
    - [X] Engine.java - needs to make adding and removing objects work
    - [X] Scene.java - needs to only paint gameObjects on update call
    - [X] Sprite.java - Needs to be able to get the image index for serialization

- [X] Setup
    - [x] Setup window
    - [x] Add join/create game button
    - [x] Make create game work
    - [x] Make join game work
    - [x] Make the start game work

- [X] UI
    - [X] UI is scaled properly

- [X] Game engine basics
    - [X] Object rendering
    - [X] Object translation
    - [X] Object rotation
    - [X] Camera
    - [X] Collisions
    - [X] Input
    - [X] Sounds
    - [X] Sprite assets are loaded in at start

- [X] Networking
    - [X] Client checks if it can connect to server
    - [X] Both players are rendered on screen
    - [X] Both players input work and they can see each other move around
    - [X] Players can shoot bullets and both players see the bullets
    - [X] Handle player disconnects
    - [X] Server owned entities

- [X] Enemy
    - [X] Enemies randomly spawn around for all clients
    - [X] Enemies can take damage
    - [X] Enemies can die
    - [X] Enemies move towards players
    - [X] Enemies can attack player

- [X] Enviorment