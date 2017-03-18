package dev_t.cs161.quickship;

import android.util.Log;

public class quickShipModelBoard {

    private String playerID;
    private quickShipModelBoardSlot[] completeBoard;

    public quickShipModelBoard(String player_ID) {
        playerID = player_ID;
        completeBoard = new quickShipModelBoardSlot[100];
        for (int i = 0; i < 100; i++) {
            completeBoard[i] = new quickShipModelBoardSlot();
        }
    }

    public String getPlayerID() {
        return playerID;
    }

    public void printBoard_debug() {
        for (int i = 0; i < 100; i++) {
            if (completeBoard[i].isOccupied()) {
                Log.d("debug", "Gameslot(" + i + "): occupied.");
            } else {
                Log.d("debug", "Gameslot(" + i + "): unoccupied.");
            }
        }
    }

    // If the move is a hit, return true
    public boolean makeMove(int i) {
        quickShipModelBoardSlot targetedSlot = completeBoard[i];
        targetedSlot.setHit(true);
        if (targetedSlot.isOccupied()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkGameOver() {
        boolean gameOver = true;
        for (int i = 0; i < 100; i++) {
            if (completeBoard[i].isOccupied() && !completeBoard[i].isHit()) {
                gameOver = false;
            }
        }
        return gameOver;
    }

    @Override
    public String toString() {
        String returnString = "Player ID (" + playerID + "), ";
        for (int i = 0; i < 5; i++) {
            if (completeBoard[i].isOccupied()) {
                if (i != 4) {
                    returnString += i + " occupied, ";
                } else {
                    returnString += i + " occupied";
                }
            } else {
                if (i != 4) {
                    returnString += i + " unoccupied, ";
                } else {
                    returnString += i + " unoccupied";
                }
            }
        }
        return returnString;
    }

    public boolean isHorizontal(int index) {
        return completeBoard[index].isHorizontal();
    }

    public void setHorizontal(int index, boolean horizontal) {
        completeBoard[index].setHorizontal(horizontal);
    }

    public boolean isVertical(int index) {
        return completeBoard[index].isVertical();
    }

    public void setVertical(int index, boolean vertical) {
        completeBoard[index].setVertical(vertical);
    }

    public boolean isAnchor(int index) {
        return completeBoard[index].isAnchor();
    }

    public void setAnchor(int index, boolean anchor) {
        completeBoard[index].setAnchor(anchor);
    }

    public boolean isHit(int index) {
        return completeBoard[index].isHit();
    }

    public void setHit(int index, boolean hit) {
        completeBoard[index].setHit(hit);
    }

    public boolean isOccupied(int index) {
        return completeBoard[index].isOccupied();
    }

    public void setOccupied(int index, boolean occupied) {
        completeBoard[index].setOccupied(occupied);
    }

    public Direction getDirection(int index) {
        return completeBoard[index].getDirection();
    }

    public void setDirection(int index, Direction direction) {
        completeBoard[index].setDirection(direction);
    }

    public ShipType getShipType(int index) {
        return completeBoard[index].getShipType();
    }

    public void setShipType(int index, ShipType shipType) {
        completeBoard[index].setShipType(shipType);
    }
}