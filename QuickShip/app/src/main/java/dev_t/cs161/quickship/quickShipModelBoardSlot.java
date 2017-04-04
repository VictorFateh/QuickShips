package dev_t.cs161.quickship;

import java.util.UUID;

public class quickShipModelBoardSlot {
    private boolean isHit;
    private boolean isOccupied;
    private boolean isAnchor;
    private Orientation mOrientation;
    private ShipType mShipType;
    private int mAnchorIndex;

    public quickShipModelBoardSlot() {
        setHit(false);
        setOccupied(false);
        setAnchor(false);
        setAnchorIndex(-1);
    }

    // For anchor spots
    public quickShipModelBoardSlot(int anchorIndex, ShipType shipType, Orientation orientation) {
        setHit(false);
        setAnchor(true);
        setOccupied(true);
        setAnchorIndex(anchorIndex);
        setShipType(shipType);
        setOrientation(orientation);
    }

    // For child of anchor spots
    public quickShipModelBoardSlot(int anchorIndex, ShipType shipType) {
        setHit(false);
        setAnchor(false);
        setOccupied(true);
        setAnchorIndex(anchorIndex);
        setShipType(shipType);
    }

    public int getAnchorIndex() {
        return mAnchorIndex;
    }

    public void setAnchorIndex(int anchorIndex) {
        mAnchorIndex = anchorIndex;
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

    public ShipType getShipType() {
        return mShipType;
    }

    public void setShipType(ShipType shipType) {
        mShipType = shipType;
    }
}