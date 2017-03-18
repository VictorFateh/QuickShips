package dev_t.cs161.quickship;

import android.util.Log;

import java.util.UUID;

public class quickShipModel {
    private String player_ID;
    private int _personalTurnCount;
    private quickShipModelBoard playerGameBoard;
    private quickShipModelBoard mOpponentGameBoard;

    public quickShipModel() {
        player_ID = UUID.randomUUID().toString();
        playerGameBoard = new quickShipModelBoard(player_ID);
    }

    public quickShipModelBoard getPlayerGameBoard() {
        return playerGameBoard;
    }

    public void copyOpponentGameBoard(quickShipModelBoard opponentGameBoard) {
        mOpponentGameBoard = opponentGameBoard;
    }

    public void testCode_debug() {
        String temp_id = UUID.randomUUID().toString();
        String temp_id2;
        quickShipModelBoard opponent_0 = new quickShipModelBoard(temp_id);
        copyOpponentGameBoard(opponent_0);

        temp_id = UUID.randomUUID().toString();
        quickShipModelBoard opponent_1 = new quickShipModelBoard(temp_id);
        copyOpponentGameBoard(opponent_1);

        temp_id = UUID.randomUUID().toString();
        temp_id2 = temp_id;
        quickShipModelBoard opponent_2 = new quickShipModelBoard(temp_id);
        copyOpponentGameBoard(opponent_2);

        temp_id = UUID.randomUUID().toString();
        quickShipModelBoard opponent_3 = new quickShipModelBoard(temp_id);
        copyOpponentGameBoard(opponent_3);

        temp_id = UUID.randomUUID().toString();
        quickShipModelBoard opponent_4 = new quickShipModelBoard(temp_id);
        copyOpponentGameBoard(opponent_4);

        copyOpponentGameBoard(opponent_4);

        temp_id = UUID.randomUUID().toString();
        quickShipModelBoard opponent_5 = new quickShipModelBoard(temp_id);
        copyOpponentGameBoard(opponent_5);

        Log.d("debug", " Player: " + playerGameBoard);
        Log.d("debug", " Opponent: " + mOpponentGameBoard);
    }
}