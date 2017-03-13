package devt.cs161.quickbattleship;

import android.util.Log;

import java.util.UUID;

/**
 * Created by trinhnguyen on 3/12/17.
 */

public class quickbattleship_controller {

    public quickbattleship_controller() {
        quickbattleship_model player1 = new quickbattleship_model();
        quickbattleship_model player2 = new quickbattleship_model();
        quickbattleship_board player1Board = player1.getPlayerGameBoard();
        quickbattleship_board player2Board = player2.getPlayerGameBoard();
        player1.copyOpponentGameBoard(player2Board);
        player2.copyOpponentGameBoard(player1Board);
        player1.printMap_debug();
        player2.printMap_debug();
    }
}