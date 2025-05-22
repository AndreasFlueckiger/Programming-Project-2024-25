package Programming-Project-2024-25.Logic;

public class Cell {
    private boolean hit;
    private Ship ship;

    public Cell() {
        this.hit = false;
        this.ship = null;
    }

    public boolean hasShip() {
        return ship != null;
    }

    public void placeShip(Ship ship) {
        this.ship = ship;
    }

    public boolean hit() {
        if (!hit) {
            hit = true;
            if (ship != null) {
                ship.registerHit();
                return true;
            }
        }
        return false;
    }

    public boolean isHit() {
        return hit;
    }

    public Ship getShip() {
        return ship;
    }
}
