# Battleship War

A graphical Battleship board game built with Java 21 and Swing, supporting
**Player vs Bot** and **Player vs Player** modes.
Each player secretly places 3 **mines** in addition to the standard fleet —
triggering a mine punishes the attacking player and hits 3 random cells on their board.

---

## Group members



---

## Requirements

- **Java 21** (or any JDK starting from version 17)
- **Maven 3.6+**

Verify your versions using:
```bash
java -version
mvn -version
```

---

## Build

From the project root (the folder containing `pom.xml`):

```bash
mvn package
```

This compiles all sources and produces a self-contained über-jar at:

```
target/battleship-3.0.jar
```

The über-jar bundles all dependencies so no external classpath is needed.

---

## Run

```bash
java -jar target/battleship-3.0.jar
```

The game window opens immediately. No command-line arguments are required.

---

## Description

Battleship War is a turn-based naval combat game for one or two players.

**Player vs Bot** — one human faces a computer opponent that uses a
**hunt-and-target strategy**: it fires at a sparse checkerboard pattern until
it scores a hit, then methodically attacks orthogonal neighbours until the
ship sinks, before returning to hunt mode.

**Player vs Player (local)** — two humans share the same computer. After
every shot a "pass the computer" privacy screen hides both boards until the
next player confirms they are ready, preventing accidental peeking and cheating.

**Mine mechanic** — during setup each player secretly plants 3 mines on
empty water cells on their own board. When the opponent shoots a mined cell
a `MINE_TRIGGER` result fires: The attacking player gets hit on 3 random tiles
as a punishment for hitting the hidden **Mine**.

---

### Cell colour key

| Colour | Meaning |
|--------|---------|
| Dark green | Water (un-shot) |
| Bright green `#` | Your own ship |
| Yellow `*` | Your mine (visible only on your board) |
| Dim green `o` | Miss |
| Red `X` | Hit |
| Dark red `X` | Sunk ship |
| Amber `*` | Mine blast zone |

### Fleet

| Ship | Size |
|------|------|
| Carrier | 5 |
| Battleship | 4 |
| Destroyer | 3 |
| Submarine | 3 |
| Patrol Boat | 2 |

---

## Implementation overview (This part is written by chatgbt)

### High-level components

```
┌─────────────────────────────────────────────────────────┐
│                        UI Layer                         │
│  MenuFrame   SetupFrame   GameFrame   Theme             │
└───────────────────────┬─────────────────────────────────┘
                        │ calls
┌───────────────────────▼─────────────────────────────────┐
│                      Game Model                         │
│  Board   Ship   Cell   BotAI   GameMode   ShotResult    │
└─────────────────────────────────────────────────────────┘
```

**`Board`** — owns the 10×10 `Cell` grid, the `Ship` list, and all mine
positions. Exposes `placeShip`, `placeMine`, `shoot`, `allShipsSunk`, and
query helpers. This is the main interface between the model and the UI.

**`BotAI`** — consults `Board.wasShotAt` and calls `Board.shoot` internally
to decide and execute each bot turn. It maintains its own hunt queue and
target queue; the UI only calls `BotAI.takeTurn()` and reads back the
chosen coordinates.

**`GameFrame`** — the top-level game window. It owns references to both
`Board` objects and the optional `BotAI`, and delegates all grid rendering
and shot processing to private helper methods. It does not contain any game
logic beyond calling `Board.shoot` and reading the returned `ShotResult`.

**`SetupFrame`** — drives the interactive fleet/mine placement phase. It
writes directly into a `Board` via `Board.placeShip` and `Board.placeMine`,
then passes the completed `Board` to `GameFrame`.

**`Theme`** — a stateless utility class. Every colour constant, font, and
Swing component factory lives here. No other class contains hard-coded
colour or font values.

**`MenuFrame`** — the splash and mode-selection screens. It constructs a
`SetupFrame` with the chosen `GameMode` when the player clicks SELECT.

### Third-party libraries

No third party libraries are used. The project uses only the Java 21 standard library (Java Swing / AWT for
the UI, `java.util` and `Random`).

### Notable programming techniques

**Enum-driven branching (`GameMode`, `ShotResult`)** — using enums instead
of boolean flags or integer constants makes every branch in `GameFrame` and
`SetupFrame` self-documenting and exhaustive. Java's `switch` expressions
produce a compile-time error if a case is missing, eliminating a whole
class of bugs.

/** 
**Sealed blast-radius chain reaction** = this explains the Mine aspect of the game (finsih this)
*/

**Factory-method design system (`Theme`)** — all Swing component creation
goes through static factory methods (`Theme.button`, `Theme.cellButton`,
`Theme.label`, etc.). Adding a hover effect to every button in one place
means a visual change requires editing exactly one method rather than
touching every screen class.

**Timer-based bot delay** — the bot's shot is triggered via
`javax.swing.Timer` with a 500 ms delay rather than `Thread.sleep`, keeping
the Event Dispatch Thread free and the UI fully responsive during the pause.

**Hunt/target AI with checkerboard seeding** — `BotAI` pre-builds a
shuffled list of every cell where `(row + col) % 2 == 0`. Because every
ship of length ≥ 2 must cover at least one such cell, this halves the
expected number of hunt shots compared to a fully random approach.

**Pattern matching in switch expressions (Java 21)** — `ShotResult` cases
are handled with Java 21 switch expressions throughout `GameFrame`, making
the result-to-visual mapping compact and exhaustive without nested
if/else chains.

---

## Human experience

### Workload distribution

| Task | Member |
|------|--------|
| Game model (`Board`, `Ship`, `Cell`, `ShotResult`, `GameMode`) | Student  |
| Bot AI (`BotAI`) | Student A |
| Main menu and setup UI (`MenuFrame`, `SetupFrame`) | Student |
| Game screen UI (`GameFrame`) | Student  |
| Design system (`Theme`) | Student  |
| Integration, testing, and README | All Students |

### How git was used (This part here is chatgbt edit this when I have time)

Each member worked on a dedicated feature branch (`feature/model`,
`feature/ui`) and opened a pull request when a component was complete.
The other member reviewed and merged. Commit messages follow the format
`[component] short description`, e.g. `[BotAI] add checkerboard hunt queue`.
The `main` branch was kept in a runnable state at all times.
No force-pushes were used on `main`; all integration happened through
merge commits so the full history of both contributors is visible.

### Challenges

**Student ** — Implementing the mine chain-reaction correctly was tricky.
The first attempt used a recursive call that could re-enter the same cell,
causing a `StackOverflowError` on dense mine layouts. The fix was to check
`mineDetonated` before recursing and set the flag immediately on entry,
turning the implicit recursion guard into an explicit one.

**Student ** — Swing's paint model made the CRT scanline overlay
(`Theme.paintScanlines`) difficult. Calling `repaint()` inside a
`MouseListener` caused visible flicker because Swing double-buffers per
component, not per window. The solution was to draw the overlay directly
inside each panel's `paintComponent` override rather than as a separate
glass-pane layer, which gave flicker-free rendering.

---

## Repository structure

```
.
├── .gitignore
├── README.md
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/example/battleship/
                ├── Main.java          Entry point
                ├── Theme.java         Design system (colours, fonts, factories)
                ├── MenuFrame.java     Splash + mode-selection UI
                ├── SetupFrame.java    Ship & mine placement UI
                ├── GameFrame.java     Main game window (PvP + PvBot)
                ├── GameMode.java      Enum: PLAYER_VS_PLAYER / PLAYER_VS_BOT
                ├── Board.java         10×10 grid, ship/mine placement, shooting
                ├── BotAI.java         Hunt/target AI
                ├── Ship.java          Ship model
                ├── Cell.java          Cell model
                └── ShotResult.java    Shot outcome enum
```

---

## External references

No external libraries, tutorials, or third-party source code were used.
The hunt/target AI strategy is a well-known Battleship heuristic; the
implementation here is original.
