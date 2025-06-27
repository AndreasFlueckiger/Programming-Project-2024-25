# Battleship Programming Project

## Team:
Alexis Andreas Fleuckiger (AndreasFlueckiger)
Matteo Fina (MatteoFindus)
Marco Meneghetti (SirMarkusIT)
Leonardo Fabricio Reyna Salas (Superleoxx)

**Language:** Java 17  
**GUI:** Java Swing  
**Grid Size:** 10 × 10

---

## Instructions to run it
On the virtual machine is required Java 17 or later to run it. 
To start the program you need to compile the file Launcher.java that is memorized in src/main/ui/main.
You need to go on the location of the file and you need put on the terminal this command:
javac Launcher.java
java Launcher

---

This is a fully functional **2-player Battleship game** built using **Java Swing**. It includes ship placement, attack phase, power abilities (Air Attack and Scatter Bomb), and game state saving/loading.

---



## Overview

This project is a digital implementation of the classic **Battleship** strategy game with two game modes:

Players can choose between:
- **Player vs Player (PvP)** 
- **Player vs Bot (PvBot)**   
  (Bot available in Easy, Hard and learning difficulties levels)


---

## Special Powers (Modern Mode Only)

Each power can be used once per game. Only one power can be used per turn.

- **Scatter Bomb**: Hits a 3×3 area centered on a selected cell. Ends your turn.
- **Attacco Aereo**: Attacks all cells in a selected column. Ends your turn.


## Gameplay Flow

1. **Game Start**  
   Choose between PvP or PvBot.

2. **Username Input**  
   Usernames are entered for history tracking.

3. **Ship Placement**  
   - Player 1 places their fleet.
   - Screen prompts for Player 2 to place ships (to avoid cheating).
   - Ships are placed by clicking on the grid, with a button to toggle orientation.
   - Left Click: Place ship
   - Right Click: Rotate ship
   - R: Reset board
   - Esc: Unselect ship

4. **Save/Load**
   - Saves game state to .ser file using Java's ObjectOutputStream
   - Load the game from file using the "Load Game" option on the home/main screen

5. **Game Loop**  
   - Players take turns guessing coordinates.
   - The opponent confirms each hit or miss.
   - The tracking grid shows hits (O) and misses (X).
   - Powers can be used in Modern mode.

6. **Winning the Game**  
   The first player to sink all opponent ships wins.

---

## Bot Difficulty

- **Easy:** Bot picks random untried coordinates.
- **Hard:** Bot picks random untried coordinates and can use powers.
- **Learning:** Bot ..............

---

## Project Structure

- `src/main/`
  - `ui/`
    - `main/` – Launcher class (`Launcher.java`)
    - `initialScreen/` – Main menu, name form, screen layout
  - `logic/`
    - `attack/` – Attack logic and utilities (`Attack.java`, `AttackUtilities.java`)
    - `board/` – Grid, Cell, and Board logic
    - `powers/` – Power classes (`AirAttack`, `Scanner`, `ScatterBomb`) + `PowerManager`
    - `ships/` – Ship classes: `Battleship`, `Cruiser`, `Submarine`, etc.
    - `shippositioning/` – Ship placement logic, validation, selection boards
    - `victory/` – Victory screen UI, fireworks GIF, and result memory
  - `bot/` – AI players: `EasyBot`, `HardBot`, `LearningBot`
  - `rules/`
    - `designPatterns/` – `Observer` and `Observable`
    - `CtrlRules.java` – Core rule engine
  - `saveload/` – `SaveLoadManager.java` for saving/loading progress
  - `battleship/` – `BattleshipConfiguration.java` with global constants and enums



## Key Classes Overview

| Class                         | Role                              |
| ----------------------------- | --------------------------------- |
| `Launcher.java`               | Starts the GUI                    |
| `InitialScreen.java`          | Renders the main menu             |
| `NameForm.java`               | Allows players to input names     |
| `Board.java`, `Cell.java`     | Grid and cell management          |
| `Attack.java`                 | Performs attack operations        |
| `PowerManager.java`           | Activates and manages powers      |
| `AirAttack.java`              | Power that attacks a full line    |
| `ShipPlacementValidator.java` | Ensures valid placement           |
| `VictoryPanel.java`           | Displays win screen and animation |
| `LearningBot.java`            | Bot that improves from experience |
| `SaveLoadManager.java`        | Handles saving/loading game data  |



---

## Game Concept

Battleship is a turn-based strategy game played on two 10×10 grids per player.  
One grid is used to place the player's own fleet; the other is used to record attacks on the opponent.  
Players alternate turns by calling out grid coordinates (e.g., "B5"). The opponent responds with “hit” or “miss.” When all the tiles of a ship are hit, the ship is sunk. The first player to sink all of their opponent’s ships wins.

## Experience
On the workload distribution have worked Alexis, in fact he distributed work to the group and then we discussed weekly updates who did what and how the workload should be redistributed. 
Git was used only like a cloud repository to store the project without any usage of the coordination tools. The group faced some problems as removing directories after pivoting ideas.
In the project there isn't third party librariers.

<ul>
   <li>
       <b>Alexis<b>: My biggest challenge was designing the GUI from stratch and to incorporate the code written by other members of the group.
       <ol>
         <li>Observer Pattern (Design Pattern)
            Where?

            Observable / Observer interfaces

            Classes like CtrlRules notify Attack or ShipSelection when data changes.

            Why it’s complex:

            Requires understanding of loose coupling and event-driven architecture.

            Implements decoupled update propagation to multiple observers.

            Example:

            for (Observer o : lob) {
               o.notify(this);
            }
   </li>
         <li> MVC (Model-View-Controller) Architecture
            Where?

            Model: CtrlRules, PowerManager, ShipOptions

            View: Attack, ShipSelection, VictoryPanel

            Controller: RulesFacade coordinates actions and game state

            Why it’s complex:

            Enforces separation of concerns, requiring structure and discipline.

            Ensures testability, flexibility, and scalability.
   </li>
   <li>Serialization for Saving and Loading Game State
      Where?

      CtrlRules implements Serializable

      SaveLoadManager uses ObjectOutputStream / ObjectInputStream

      Why it’s complex:

      Requires correct versioning, transient fields, and class compatibility.

      Ensures full game state is stored/restored safely
   </li>
   </ol> 
   </li>
   <li>
       <b>Matteo<b>: My biggest challenge was the coding of the bots
   </li>
   <li>
       <b>Marco<b>: My biggest challenge was when I was coding the savment of the multiplayers results, here I applied a more complex way
       of serialisation and desesrialisation that we saw in the class, because I need not to lose any previous information.
   </li>
</ul>