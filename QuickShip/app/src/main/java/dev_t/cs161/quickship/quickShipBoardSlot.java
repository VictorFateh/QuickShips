package dev_t.cs161.quickship;

public class quickShipBoardSlot {
    private boolean isHit;
    private boolean isOccupied;

    public quickShipBoardSlot() {
        setHit(false);
        setOccupied(false);
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}