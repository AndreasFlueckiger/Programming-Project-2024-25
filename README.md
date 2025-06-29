# Battleship Programming Project

## Team

Alexis Andreas Fleuckiger (AndreasFlueckiger)<br>
Matteo Fina (MatteoFindus)<br>
Marco Meneghetti (SirMarkusIT)<br>
Leonardo Fabricio Reyna Salas (Superleoxx)

## Distribution of the workload
<ul>
<li> <b> GUI</b> Alexis</li>
<li> <b> Powers</b> Marco</li>
<li> <b> CtrlRules</b> Matteo and Alexis</li>
<li> <b> Ships</b> Marco and Alexis</li>
<li> <b> Attack logic</b> Matteo</li>
<li> <b> Bots</b> Matteo</li>
</ul>

---

**Language:** Java 17  
**GUI:** Java Swing  
**Build Tool:** Maven  
**Grid Size:** 10 × 10

---

## Instructions to run it

### Prerequisites
- Java 17 or later
- Maven 3.6 or later

### Quick Start (Recommended)

#### On macOS/Linux:
```bash
./run.sh
```

#### On Windows:
```cmd
run.bat
```

These scripts will automatically:
- Check if Java and Maven are installed
- Build the project if needed
- Launch the game

### Manual Running with Maven

1. **Clone or download the project**

2. **Navigate to the project directory**
   ```bash
   cd Programming-Project-2024-25
   ```

3. **Compile and run the project**
   ```bash
   # Compile the project
   mvn clean compile
   
   # Run the game
   mvn exec:java -Dexec.mainClass="main.ui.main.Launcher"
   ```

### Alternative: Run the JAR file

1. **Build the executable JAR**
   ```bash
   mvn clean package
   ```

2. **Run the JAR file**
   ```bash
   java -jar target/battleship-game-1.0.0.jar
   ```

### Development

- **Compile:** `mvn compile`
- **Test:** `mvn test`
- **Package:** `mvn package`
- **Clean:** `mvn clean`

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
- **Learning:** Bot tries to understand the pattern of the ship and uses the powers.

---

## Powers

- **Air Attack:** works on the row and despite the user coordinate the program choose a random row.
- **Scatter bomb:** this power has an effect on a 3x3 area, but like the Air Attack despite the input of the player choose the area of hitting randomly.

There is a bug for the use of the powers in a player vs player match, if the first player uses a power the next player to use the same needs to click another power than he/she can use the power that previously doesn't work properly.

---

## Project Structure

- `src/main/java/`
  - `main/ui/`
    - `main/` – Launcher class (`Launcher.java`)
    - `initialScreen/` – Main menu, name form, screen layout
  - `main/logic/`
    - `attack/` – Attack logic and utilities (`Attack.java`, `AttackUtilities.java`)
    - `board/` – Grid, Cell, and Board logic
    - `powers/` – Power classes (`AirAttack`, `Scanner`, `ScatterBomb`) + `PowerManager`
    - `ships/` – Ship classes: `Battleship`, `Cruiser`, `Submarine`, etc.
    - `shippositioning/` – Ship placement logic, validation, selection boards
    - `victory/` – Victory screen UI, fireworks GIF, and result memory
  - `main/bot/` – AI players: `EasyBot`, `HardBot`, `LearningBot`
  - `main/rules/`
    - `designPatterns/` – `Observer` and `Observable`
    - `CtrlRules.java` – Core rule engine
  - `main/saveload/` – `SaveLoadManager.java` for saving/loading progress
  - `main/battleship/` – `BattleshipConfiguration.java` with global constants and enums
- `src/main/resources/` – Game resources (images, data files)
- `target/` – Compiled classes and JAR files (generated by Maven)

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
Players alternate turns by calling out grid coordinates (e.g., "B5"). The opponent responds with "hit" or "miss." When all the tiles of a ship are hit, the ship is sunk. The first player to sink all of their opponent's ships wins.

## Different (high-level) components and interfaces between components

<ol>
         <li>Observer Pattern (Design Pattern)
            Where?

            Observable / Observer interfaces

            Classes like CtrlRules notify Attack or ShipSelection when data changes.

            Why it's complex:

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

            Why it's complex:

            Enforces separation of concerns, requiring structure and discipline.

            Ensures testability, flexibility, and scalability.
   </li>
   <li>Serialization for Saving and Loading Game State
      Where?

      CtrlRules implements Serializable

      SaveLoadManager uses ObjectOutputStream / ObjectInputStream

      Why it's complex:

      Requires correct versioning, transient fields, and class compatibility.

      Ensures full game state is stored/restored safely
   </li>
   </ol>

## Experience

On the workload distribution have worked Alexis, in fact he distributed work to the group and then we discussed weekly updates who did what and how the workload should be redistributed.
Git was used only like a cloud repository to store the project without any usage of the coordination tools. The group faced some problems as removing directories after pivoting ideas.
In the project there isn't third party librariers.

<ul>
   <li>
       <b>Alexis<b>: My biggest challenge was designing the GUI from stratch and to incorporate the code written by other members of the group.
   </li>
   <li>
       <b>Matteo<b>: My biggest challenge was to create the bots and make them work on different levels of difficulties.
   </li>
   <li>
       <b>Marco<b>: My biggest challenge was when I was coding the savement of the multiplayers results, here I applied a more complex way of serialisation and desesrialisation that we saw in the class, because I need not to lose any previous information.
   </li>
</ul>
