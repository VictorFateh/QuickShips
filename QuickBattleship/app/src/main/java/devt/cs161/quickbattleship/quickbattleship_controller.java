package devt.cs161.quickbattleship;

import android.util.Log;

import java.util.UUID;

/**
 * Created by trinhnguyen on 3/12/17.
 */

public class quickbattleship_controller {
    quickbattleship_model model;

    public quickbattleship_controller() {
        model = new quickbattleship_model();
    }

    public void testCode() {
        String temp_id = UUID.randomUUID().toString();
        String temp_id2;
        quickbattleship_board opponent_0 = new quickbattleship_board(temp_id);
        model.copyOpponentGameBoard(temp_id, opponent_0);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_1 = new quickbattleship_board(temp_id);
        model.copyOpponentGameBoard(temp_id, opponent_1);

        temp_id = UUID.randomUUID().toString();
        temp_id2 = temp_id;
        quickbattleship_board opponent_2 = new quickbattleship_board(temp_id);
        model.copyOpponentGameBoard(temp_id, opponent_2);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_3 = new quickbattleship_board(temp_id);
        model.copyOpponentGameBoard(temp_id, opponent_3);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_4 = new quickbattleship_board(temp_id);
        model.copyOpponentGameBoard(temp_id, opponent_4);

        model.copyOpponentGameBoard(opponent_4.getGame_ID(), opponent_4);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_5 = new quickbattleship_board(temp_id);
        model.copyOpponentGameBoard(temp_id, opponent_5);

        model.printMap_debug();
        Log.d("debug", "\n**********************************************************************\n");
        Log.d("debug", model.printSingleBoard_debug(temp_id2).toString());
    }
}
