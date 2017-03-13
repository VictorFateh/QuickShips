package devt.cs161.quickbattleship;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by trinhnguyen on 3/11/17.
 */

public class quickbattleship_model {
    private String _player_ID;
    private int _personalTurnCount;
    private quickbattleship_board _playerGameBoard;
    private Map<String, quickbattleship_board> _opponentGameBoards;

    public quickbattleship_model() {
        _player_ID = UUID.randomUUID().toString();

        _playerGameBoard = new quickbattleship_board(_player_ID);
        _opponentGameBoards = new HashMap<>();
    }

    public quickbattleship_board getPlayerGameBoard() {
        return _playerGameBoard;
    }

    public void copyOpponentGameBoard(quickbattleship_board opponentGameBoard) {
        String opponent_ID = opponentGameBoard.getPlayerID();
        _opponentGameBoards.put(opponent_ID, opponentGameBoard);
    }

    public void printMap_debug() {
        Iterator it = _opponentGameBoards.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Log.d("debug", pair.getKey() + " = " + pair.getValue() + "\n");
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public quickbattleship_board printSingleBoard_debug(String opponent_ID) {
        return _opponentGameBoards.get(opponent_ID);
    }

    public void testCode_debug() {
        String temp_id = UUID.randomUUID().toString();
        String temp_id2;
        quickbattleship_board opponent_0 = new quickbattleship_board(temp_id);
        copyOpponentGameBoard(opponent_0);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_1 = new quickbattleship_board(temp_id);
        copyOpponentGameBoard(opponent_1);

        temp_id = UUID.randomUUID().toString();
        temp_id2 = temp_id;
        quickbattleship_board opponent_2 = new quickbattleship_board(temp_id);
        copyOpponentGameBoard(opponent_2);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_3 = new quickbattleship_board(temp_id);
        copyOpponentGameBoard(opponent_3);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_4 = new quickbattleship_board(temp_id);
        copyOpponentGameBoard(opponent_4);

        copyOpponentGameBoard(opponent_4);

        temp_id = UUID.randomUUID().toString();
        quickbattleship_board opponent_5 = new quickbattleship_board(temp_id);
        copyOpponentGameBoard(opponent_5);

        printMap_debug();
        Log.d("debug", "\n**********************************************************************\n");
        Log.d("debug", printSingleBoard_debug(temp_id2).toString());
    }
}