package dev_t.cs161.quickship;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class quickShipModelBoardSlot {
    private boolean isHit;
    private boolean isOccupied;
    private boolean isAnchor;
    private int mOrientation;
    private int mShipType;
    private int mAnchorIndex;
    private String emoji;
    static final int HORIZONTAL = 0;
    static final int VERTICAL = 1;

    static final int TWO = 3;
    static final int THREE_A = 4;
    static final int THREE_B = 5;
    static final int FOUR = 6;
    static final int FIVE = 7;

    public quickShipModelBoardSlot() {
        setHit(false);
        setOccupied(false);
        setAnchor(false);
        setAnchorIndex(-1);
        emoji="";
    }

    // For anchor spots
    public quickShipModelBoardSlot(int anchorIndex, int shipType, int orientation) {
        setHit(false);
        setAnchor(true);
        setOccupied(true);
        setAnchorIndex(anchorIndex);
        setShipType(shipType);
        setOrientation(orientation);
        emoji="";
    }

    // For child of anchor spots
    public quickShipModelBoardSlot(int anchorIndex, int shipType) {
        setHit(false);
        setAnchor(false);
        setOccupied(true);
        setAnchorIndex(anchorIndex);
        setShipType(shipType);
        emoji="";
    }
    public void setEmoji(String newEmoji) {
        emoji = newEmoji;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getAnchorIndex() {
        return mAnchorIndex;
    }

    public void setAnchorIndex(int anchorIndex) {
        mAnchorIndex = anchorIndex;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
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

    public int getShipType() {
        return mShipType;
    }

    public void setShipType(int shipType) {
        mShipType = shipType;
    }
}