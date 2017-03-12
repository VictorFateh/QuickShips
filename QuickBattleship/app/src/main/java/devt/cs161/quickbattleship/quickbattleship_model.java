package devt.cs161.quickbattleship;

import android.util.Log;

import java.util.UUID;

/**
 * Created by trinhnguyen on 3/11/17.
 */

public class quickbattleship_model {
    private UUID player_id;
    private quickbattleship_board gameBoard;

    public quickbattleship_model() {
        player_id = UUID.randomUUID();
        gameBoard = new quickbattleship_board();
        gameBoard.printBoard();
        //Log.d("debug", "UUID:" + player_id);
    }
}
