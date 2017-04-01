package dev_t.cs161.quickship;

import android.util.Log;

import java.util.UUID;

public class quickShipModel {

    private String mPlayerID;
    private quickShipModelBoard mPlayerGameBoard;
    private quickShipModelBoard mOpponentGameBoard;
    private int mTurnCount;

    public quickShipModel() {
        mPlayerID = UUID.randomUUID().toString();
        mPlayerGameBoard = new quickShipModelBoard(mPlayerID);
        mOpponentGameBoard = new quickShipModelBoard();
        mTurnCount = 0;
    }

    public quickShipModel(quickShipModelBoard opponentGameBoard) {
        mPlayerID = UUID.randomUUID().toString();
        mPlayerGameBoard = new quickShipModelBoard(mPlayerID);
        mOpponentGameBoard = opponentGameBoard;
        mTurnCount = 0;
    }

    public quickShipModelBoard getPlayerGameBoard() {
        return mPlayerGameBoard;
    }

    public void setPlayerGameBoard(String playerID, quickShipModelBoard playerGameBoard) {
        mPlayerGameBoard = playerGameBoard;

    }

    public void setPlayerGameBoard(quickShipModelBoard playerGameBoard) {
        mPlayerGameBoard = playerGameBoard;
    }

    public quickShipModelBoard getOpponentGameBoard() {
        return mOpponentGameBoard;
    }

    public void setOpponentGameBoard(quickShipModelBoard opponentGameBoard) {
        mOpponentGameBoard = opponentGameBoard;
    }

    public String getPlayerID() {
        return mPlayerID;
    }

    public void setPlayerID(String playerID) {
        mPlayerID = playerID;
    }

    public void testCodeDebug() {
        String temp_id = UUID.randomUUID().toString();
        quickShipModelBoard opponent0 = new quickShipModelBoard(temp_id);
        setOpponentGameBoard(opponent0);

        Log.d("debug", " Player: " + mPlayerGameBoard);
        Log.d("debug", " Opponent: " + mOpponentGameBoard);
    }
}