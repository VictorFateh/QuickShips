package devt.cs161.quickbattleship;

import android.util.Log;

import java.util.UUID;

/**
 * Created by trinhnguyen on 3/12/17.
 */

public class quickbattleship_board {

    private String _player_ID;
    private quickbattleship_board_slot[] _completeBoard;

    public quickbattleship_board(String player_ID) {
        _player_ID = player_ID;
        _completeBoard = new quickbattleship_board_slot[100];
        for (int i = 0; i < 100; i++) {
            _completeBoard[i] = new quickbattleship_board_slot();
        }
    }

    public String getGame_ID() {
        return _player_ID;
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

    public boolean makeMove(int i) {
        quickbattleship_board_slot targetedSlot = _completeBoard[i];
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
        String returnString = "Player ID (" + _player_ID + "), ";
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