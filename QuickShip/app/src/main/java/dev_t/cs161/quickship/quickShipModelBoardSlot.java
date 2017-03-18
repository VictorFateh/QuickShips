package dev_t.cs161.quickship;

public class quickShipModelBoardSlot {
    private boolean isHit;
    private boolean isOccupied;
    private boolean isAnchor;
    private boolean isVertical;
    private boolean isHorizontal;
    private int shipType;

    public quickShipModelBoardSlot() {
        setHit(false);
        setOccupied(false);
        setAnchor(false);
        setVertical(false);
        setHorizontal(false);
        setShipType(-1);
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public int getShipType() {
        return shipType;
    }

    public void setShipType(int shipType) {
        this.shipType = shipType;
    }

    public boolean isAnchor() {
        return isAnchor;
    }

    public void setAnchor(boolean anchor) {
        isAnchor = anchor;
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