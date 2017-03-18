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

    public void setOccuppied(int index) {
        completeBoard[index].setOccupied(true);
    }


    // If the move is a hit, return true
    public boolean makeMove(int i) {
        quickShipModelBoardSlot targetedSlot = completeBoard[i];
        targetedSlot.setHit(true);
        if (targetedSlot.isOccupied()) {
            return true;
        }
        else {
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
}