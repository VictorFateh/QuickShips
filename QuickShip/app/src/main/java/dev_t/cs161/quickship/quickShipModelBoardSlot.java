package dev_t.cs161.quickship;

public class quickShipModelBoardSlot {
    private boolean isHit;
    private boolean isOccupied;
    private boolean isAnchor;
    private boolean isSet;
    private Orientation mOrientation;
    private Direction mDirection;
    private ShipType mShipType;
    private int mAnchorIndex;

    public quickShipModelBoardSlot() {
        setHit(false);
        setOccupied(false);
        setAnchor(false);
        setSet(false);
        setAnchorIndex(-1);
        setOrientation(Orientation.HORIZONTAL);
    }

    public int getAnchorIndex() {
        return mAnchorIndex;
    }

    public void setAnchorIndex(int anchorIndex) {
        mAnchorIndex = anchorIndex;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
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

    public Direction getDirection() {
        return mDirection;
    }

    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    public ShipType getShipType() {
        return mShipType;
    }

    public void setShipType(ShipType shipType) {
        mShipType = shipType;
    }
}