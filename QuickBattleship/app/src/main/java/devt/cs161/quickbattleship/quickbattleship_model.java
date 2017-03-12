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
    private quickbattleship_board _playerGameBoard;
    private Map<String, quickbattleship_board> _opponentGameBoards;

    public quickbattleship_model() {
        _player_ID = UUID.randomUUID().toString();

        _playerGameBoard = new quickbattleship_board(_player_ID);
        _opponentGameBoards = new HashMap<>();
    }

    public void copyOpponentGameBoard(String opponent_ID, quickbattleship_board opponentGameBoard) {
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
}