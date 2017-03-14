package devt.cs161.quickship;

import android.util.Log;
import java.util.UUID;

public class quickShipBoard {

    private String _playerID;
    private quickShipBoardSlot[] _completeBoard;

    public quickShipBoard(String player_ID) {
        _playerID = player_ID;
        _completeBoard = new quickShipBoardSlot[100];
        for (int i = 0; i < 100; i++) {
            _completeBoard[i] = new quickShipBoardSlot();
        }
    }

    public String getPlayerID() {
        return _playerID;
    }

    public void printBoard_debug() {
        for (int i = 0; i < 100; i++) {
            if (_completeBoard[i].isOccupied()) {
                Log.d("debug", "Gameslot(" + i + "): occupied.");
            } else {
                Log.d("debug", "Gameslot(" + i + "): unoccupied.");
            }
        }
    }

    // If the move is a hit, return true
    public boolean makeMove(int i) {
        quickShipBoardSlot targetedSlot = _completeBoard[i];
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
            if (_completeBoard[i].isOccupied() && !_completeBoard[i].isHit()) {
                gameOver = false;
            }
        }
        return gameOver;
    }

    @Override
    public String toString() {
        String returnString = "Player ID (" + _playerID + "), ";
        for (int i = 0; i < 5; i++) {
            if (_completeBoard[i].isOccupied()) {
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