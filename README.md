# Battleship Programming Project
# The ReadMe still Needs to be updated and Written properly in Markdown



**Language:** Java 17  
**GUI:** Java Swing  
**Grid Size:** 10 × 10

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

## Main Classes and Responsibilities

src/
├── main/
│ ├── Main.java # Launcher
│ ├── battleship/ # Configuration
│ ├── logic/
│ │ ├── attack/ # Attack phase & utilities
│ │ ├── board/ # Board and Cell logic
│ │ ├── powers/ # Power system (AirAttack, ScatterBomb)
│ │ ├── shippositioning/ # Placement GUI and logic
│ │ ├── ships/ # Ship types (Battleship, Cruiser, etc.)
│ │ └── victory/ # Victory screen
│ ├── rules/ # Core logic (CtrlRules, RulesFacade)
│ ├── saveload/ # Save/Load functionality
│ └── ui/initialScreen/ # Initial player name screen

---

## Game Concept

Battleship is a turn-based strategy game played on two 10×10 grids per player.  
One grid is used to place the player's own fleet; the other is used to record attacks on the opponent.  
Players alternate turns by calling out grid coordinates (e.g., "B5"). The opponent responds with “hit” or “miss.” When all the tiles of a ship are hit, the ship is sunk. The first player to sink all of their opponent’s ships wins.
