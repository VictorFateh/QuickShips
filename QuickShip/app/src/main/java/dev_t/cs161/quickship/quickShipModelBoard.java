package dev_t.cs161.quickship;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

public class quickShipModelBoard {

    private String mPlayerID;
    private String mPlayerName;
    private quickShipModelBoardSlot[] completeBoard;

    public quickShipModelBoard() {
        completeBoard = new quickShipModelBoardSlot[100];
        for (int i = 0; i < 100; i++) {
            completeBoard[i] = new quickShipModelBoardSlot();
        }
    }

    public quickShipModelBoard(String playerID) {
        mPlayerID = playerID;
        mPlayerName = "Player";
        completeBoard = new quickShipModelBoardSlot[100];
        for (int i = 0; i < 100; i++) {
            completeBoard[i] = new quickShipModelBoardSlot();
        }
    }

    public quickShipModelBoard(String playerID, String playerName) {
        mPlayerID = playerID;
        mPlayerName = playerName;
        completeBoard = new quickShipModelBoardSlot[100];
        for (int i = 0; i < 100; i++) {
            completeBoard[i] = new quickShipModelBoardSlot();
        }
    }

    public String getPlayerName() {
        return mPlayerName;
    }

    public void setPlayerName(String playerName) {
        this.mPlayerName = playerName;
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
        String returnString = "Player ID (" + mPlayerID + "), ";
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

    public String getPlayerID() {
        return mPlayerID;
    }

    public void setPlayerID(String playerID) {
        mPlayerID = playerID;
    }

    public int getOrientation(int index) {
        return completeBoard[index].getOrientation();
    }

    public void setOrientation(int index, int orientation) {
        completeBoard[index].setOrientation(orientation);
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

    public int getShipType(int index) {
        return completeBoard[index].getShipType();
    }

    public void setShipType(int index, int shipType) {
        completeBoard[index].setShipType(shipType);
    }

    public ArrayList<quickShipModelBoardSlot> getAllAnchorShips() {
        ArrayList<quickShipModelBoardSlot> tempShips = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (completeBoard[i].isAnchor()) {
                tempShips.add(completeBoard[i]);
            }
        }
        return tempShips;
    }

    public void removeShip(int anchorIndex) {
        for (int i = 0; i < 100; i++) {
            if (completeBoard[i].getAnchorIndex() == anchorIndex) {
                completeBoard[i] = new quickShipModelBoardSlot();
            }
        }
    }

    public void addShip(int anchorIndex, int shipType, int orientation) {
        completeBoard[anchorIndex] = new quickShipModelBoardSlot(anchorIndex, shipType, orientation);
        if (orientation == quickShipModelBoardSlot.VERTICAL) {
            switch (shipType) {
                case quickShipModelBoardSlot.TWO:
                    completeBoard[anchorIndex + 10] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.THREE_A:
                    completeBoard[anchorIndex + 10] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 20] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.THREE_B:
                    completeBoard[anchorIndex + 10] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 20] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.FOUR:
                    completeBoard[anchorIndex + 10] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 20] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 30] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.FIVE:
                    completeBoard[anchorIndex + 10] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 20] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 30] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 40] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;
            }
        } else if (orientation == quickShipModelBoardSlot.HORIZONTAL) {
            switch (shipType) {
                case quickShipModelBoardSlot.TWO:
                    completeBoard[anchorIndex + 1] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.THREE_A:
                    completeBoard[anchorIndex + 1] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 2] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.THREE_B:
                    completeBoard[anchorIndex + 1] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 2] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.FOUR:
                    completeBoard[anchorIndex + 1] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 2] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 3] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;

                case quickShipModelBoardSlot.FIVE:
                    completeBoard[anchorIndex + 1] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 2] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 3] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    completeBoard[anchorIndex + 4] = new quickShipModelBoardSlot(anchorIndex, shipType);
                    break;
            }
        }
    }

    public int chooseModeSelectedShip(int shipType) {
        for (int i = 0; i < 100; i++) {
            if (completeBoard[i].isAnchor() && completeBoard[i].getShipType() == shipType) {
                return i;
            }
        }
        return -1;
    }

    public quickShipModelBoardSlot getShipSlotAtIndex(int index) {
        return completeBoard[index];
    }

    public boolean isCollisionExist(int index, int shipType, int orientation) {
        if (orientation == quickShipModelBoardSlot.VERTICAL) {
            int index2 = index / 10;
            int yIndex = index2 % 10;
            //Log.d("DEBUG", "yIndex: " + yIndex);
            switch (shipType) {
                case quickShipModelBoardSlot.TWO:
                    //Log.d("DEBUG", "checking: " + index + ", " + (index + 10));
                    if (completeBoard[index].isOccupied() || (yIndex + 1 < 10 && completeBoard[index + 10].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.THREE_A:
                    //Log.d("DEBUG", "checking: " + index + ", " + (index + 10) + ", " + (index + 20));
                    if (completeBoard[index].isOccupied() || (yIndex + 1 < 10 && completeBoard[index + 10].isOccupied()) || (yIndex + 2 < 10 && completeBoard[index + 20].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.THREE_B:
                    //Log.d("DEBUG", "checking: " + index + ", " + (index + 10) + ", " + (index + 20));
                    if (completeBoard[index].isOccupied() || (yIndex + 1 < 10 && completeBoard[index + 10].isOccupied()) || (yIndex + 2 < 10 && completeBoard[index + 20].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.FOUR:
                    //Log.d("DEBUG", "checking: " + index + ", " + (index + 10) + ", " + (index + 20) + ", " + (index + 30));
                    if (completeBoard[index].isOccupied() || (yIndex + 1 < 10 && completeBoard[index + 10].isOccupied()) || (yIndex + 2 < 10 && completeBoard[index + 20].isOccupied()) || (yIndex + 3 < 10 && completeBoard[index + 30].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.FIVE:
                    //Log.d("DEBUG", "checking: " + index + ", " + (index + 10) + ", " + (index + 20) + ", " + (index + 30) + ", " + (index + 40));
                    if (completeBoard[index].isOccupied() || (yIndex + 1 < 10 && completeBoard[index + 10].isOccupied()) || (yIndex + 2 < 10 && completeBoard[index + 20].isOccupied()) || (yIndex + 3 < 10 && completeBoard[index + 30].isOccupied()) || (yIndex + 4 < 10 && completeBoard[index + 40].isOccupied())) {
                        return true;
                    }
                    break;
            }
        } else if (orientation == quickShipModelBoardSlot.HORIZONTAL) {
            int xIndex = index % 10;
            //Log.d("DEBUG", "xIndex: "+xIndex);
            switch (shipType) {
                case quickShipModelBoardSlot.TWO:
                    //Log.d("DEBUG", "checking: "+index+", "+(index+1));
                    if (completeBoard[index].isOccupied() || (xIndex + 1 < 10 && completeBoard[index + 1].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.THREE_A:
                    //Log.d("DEBUG", "checking: "+index+", "+(index+1)+", "+(index+2));
                    if (completeBoard[index].isOccupied() || (xIndex + 1 < 10 && completeBoard[index + 1].isOccupied()) || (xIndex + 2 < 10 && completeBoard[index + 2].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.THREE_B:
                    //Log.d("DEBUG", "checking: "+index+", "+(index+1)+", "+(index+2));
                    if (completeBoard[index].isOccupied() || (xIndex + 1 < 10 && completeBoard[index + 1].isOccupied()) || (xIndex + 2 < 10 && completeBoard[index + 2].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.FOUR:
                    //Log.d("DEBUG", "checking: "+index+", "+(index+1)+", "+(index+2)+", "+(index+3));
                    if (completeBoard[index].isOccupied() || (xIndex + 1 < 10 && completeBoard[index + 1].isOccupied()) || (xIndex + 2 < 10 && completeBoard[index + 2].isOccupied()) || (xIndex + 3 < 10 && completeBoard[index + 3].isOccupied())) {
                        return true;
                    }
                    break;

                case quickShipModelBoardSlot.FIVE:
                    //Log.d("DEBUG", "checking: "+index+", "+(index+1)+", "+(index+2)+", "+(index+3)+", "+(index+4));
                    if (completeBoard[index].isOccupied() || (xIndex + 1 < 10 && completeBoard[index + 1].isOccupied()) || (xIndex + 2 < 10 && completeBoard[index + 2].isOccupied()) || (xIndex + 3 < 10 && completeBoard[index + 3].isOccupied()) || (xIndex + 4 < 10 && completeBoard[index + 4].isOccupied())) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public boolean checkAllPlayerShipPlaces() {
        boolean size2exist = false;
        boolean size3aexist = false;
        boolean size3bexist = false;
        boolean size4exist = false;
        boolean size5exist = false;
        for (int i = 0; i < 100; i++) {
            if (completeBoard[i].isAnchor() && completeBoard[i].getShipType() == quickShipModelBoardSlot.TWO) {
                size2exist = true;
            }
            if (completeBoard[i].isAnchor() && completeBoard[i].getShipType() == quickShipModelBoardSlot.THREE_A) {
                size3aexist = true;
            }
            if (completeBoard[i].isAnchor() && completeBoard[i].getShipType() == quickShipModelBoardSlot.THREE_B) {
                size3bexist = true;
            }
            if (completeBoard[i].isAnchor() && completeBoard[i].getShipType() == quickShipModelBoardSlot.FOUR) {
                size4exist = true;
            }
            if (completeBoard[i].isAnchor() && completeBoard[i].getShipType() == quickShipModelBoardSlot.FIVE) {
                size5exist = true;
            }
        }
        if (size2exist && size3aexist && size3bexist && size4exist && size5exist) {
            return true;
        } else {
            return false;
        }
    }
}