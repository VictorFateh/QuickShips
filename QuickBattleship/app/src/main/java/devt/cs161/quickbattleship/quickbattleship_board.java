package devt.cs161.quickbattleship;

import android.util.Log;

/**
 * Created by trinhnguyen on 3/12/17.
 */

public class quickbattleship_board {
    quickbattleship_board_slot[] completeBoard;

    public quickbattleship_board() {
        completeBoard = new quickbattleship_board_slot[100];
        for (int i = 0; i < 100; i++) {
            completeBoard[i] = new quickbattleship_board_slot();
        }
    }

    public void printBoard() {
        for (int i = 0; i < 100; i++) {
            if (completeBoard[i].isOccupied()) {
                Log.d("debug", "Gameslot("+i+"): is occupied.");
            }
            else {
                Log.d("debug", "Gameslot("+i+"): not occupied.");
            }
        }
    }
}
