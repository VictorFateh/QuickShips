package dev_t.cs161.quickship;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class quickShipModel {
    private String player_ID;
    private int _personalTurnCount;
    private quickShipBoard playerGameBoard;
    private Map<String, quickShipBoard> opponentGameBoards;

    public quickShipModel() {
        player_ID = UUID.randomUUID().toString();

        playerGameBoard = new quickShipBoard(player_ID);
        opponentGameBoards = new HashMap<>();
    }

    public quickShipBoard getPlayerGameBoard() {
        return playerGameBoard;
    }

    public void copyOpponentGameBoard(quickShipBoard opponentGameBoard) {
        String opponent_ID = opponentGameBoard.getPlayerID();
        opponentGameBoards.put(opponent_ID, opponentGameBoard);
    }

    public void printMap_debug() {
        Iterator it = opponentGameBoards.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Log.d("debug", pair.getKey() + " = " + pair.getValue() + "\n");
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public quickShipBoard printSingleBoard_debug(String opponent_ID) {
        return opponentGameBoards.get(opponent_ID);
    }

    public void testCode_debug() {
        String temp_id = UUID.randomUUID().toString();
        String temp_id2;
        quickShipBoard opponent_0 = new quickShipBoard(temp_id);
        copyOpponentGameBoard(opponent_0);

        temp_id = UUID.randomUUID().toString();
        quickShipBoard opponent_1 = new quickShipBoard(temp_id);
        copyOpponentGameBoard(opponent_1);

        temp_id = UUID.randomUUID().toString();
        temp_id2 = temp_id;
        quickShipBoard opponent_2 = new quickShipBoard(temp_id);
        copyOpponentGameBoard(opponent_2);

        temp_id = UUID.randomUUID().toString();
        quickShipBoard opponent_3 = new quickShipBoard(temp_id);
        copyOpponentGameBoard(opponent_3);

        temp_id = UUID.randomUUID().toString();
        quickShipBoard opponent_4 = new quickShipBoard(temp_id);
        copyOpponentGameBoard(opponent_4);

        copyOpponentGameBoard(opponent_4);

        temp_id = UUID.randomUUID().toString();
        quickShipBoard opponent_5 = new quickShipBoard(temp_id);
        copyOpponentGameBoard(opponent_5);

        printMap_debug();
        Log.d("debug", "\n**********************************************************************\n");
        Log.d("debug", printSingleBoard_debug(temp_id2).toString());
    }
}