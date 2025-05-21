# Battleship Programming Project
# The ReadMe still Needs to be updated and Written properly in Markdown



**Language:** Java 17  
**GUI:** Java Swing  
**Grid Size:** 10 × 10

---

## Overview

This project is a digital implementation of the classic **Battleship** strategy game with two main game modes:

- **Classic Mode:** Follows traditional rules with a full fleet of ships.
- **Modern Mode:** Uses a smaller fleet and includes special powers.

Players can choose between:
- **Player vs Player (PvP)** — Classic or Modern mode
- **Player vs Bot (PvBot)** — Classic mode only  
  (Bot available in Easy and Hard difficulty levels)

---

## Ship Types

| Version  | Ships                                                                 |
|----------|------------------------------------------------------------------------|
| Classic  | 1 × Portaaerei (4 tiles), 2 × Torpediniere (3 tiles), 3 × Incrociatore (2 tiles), 4 × Sottomarino (1 tile) |
| Modern   | 1 × Portaaerei (4), 1 × Torpediniere (3), 1 × Incrociatore (2), 1 × Sottomarino (1) |

Ships can be placed horizontally or vertically, but not diagonally, and they cannot overlap.

---

## Special Powers (Modern Mode Only)

Each power can be used once per game. Only one power can be used per turn.

- **Scatter Bomb**: Hits a 3×3 area centered on a selected cell. Ends your turn.
- **Scanner**: Reveals whether ships are present in a 3×3 area. Ends your turn.
- **Attacco Aereo**: Attacks all cells in a selected column. Ends your turn.

---

## Modern Mode Bomb Mechanic

In modern mode, each board contains two hidden "bomb tiles."  
If a player hits a bomb, two random shots are automatically fired at the opponent’s board.

---

## Gameplay Flow

1. **Game Start**  
   Choose between PvP or PvBot, then select either Classic or Modern mode.

2. **Username Input (PvP only)**  
   Usernames are entered for history tracking.

3. **Ship Placement**  
   - Player 1 places their fleet.
   - Screen prompts for Player 2 to place ships (to avoid cheating).
   - Ships are placed by clicking on the grid, with a button to toggle orientation.

4. **Game Loop**  
   - Players take turns guessing coordinates.
   - The opponent confirms each hit or miss.
   - The tracking grid shows hits (O) and misses (X).
   - Powers can be used in Modern mode.

5. **Winning the Game**  
   The first player to sink all opponent ships wins.

---

## Bot Difficulty

- **Easy:** Bot picks random untried coordinates.
- **Hard:** Bot uses probability logic to guess likely ship positions.

---

## Main Classes and Responsibilities

| Class               | Description                                | Authors               |
|---------------------|--------------------------------------------|------------------------|
| `Main.java`         | Launches the application                   | Alexis                 |
| `GameModeSelector`  | Handles mode and difficulty selection       | Alexis                 |
| `GameFrame`         | Main game window with grids and controls    | Alexis                 |
| `BoardPanel`        | Visual component for each player’s grid     | Alexis, Theo           |
| `Powers.java`       | Contains power logic implementation         | Marco, Theo            |
| `GameController`    | Core game logic and state management        | Leo, Marco, Alexis     |
| `Ship.java`         | Ship data structure and methods             | Marco                  |
| `GameHistory`       | Tracks and saves player win/loss stats      | Marco, Theo            |

---

## Game Concept

Battleship is a turn-based strategy game played on two 10×10 grids per player.  
One grid is used to place the player's own fleet; the other is used to record attacks on the opponent.  
Players alternate turns by calling out grid coordinates (e.g., "B5"). The opponent responds with “hit” or “miss.” When all the tiles of a ship are hit, the ship is sunk. The first player to sink all of their opponent’s ships wins.
