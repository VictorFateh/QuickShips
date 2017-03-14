package dev_t.cs161.quickship;

import android.util.Log;
import java.util.UUID;

public class quickShipController {

    public quickShipController() {
        quickShipModel player1 = new quickShipModel();
        quickShipModel player2 = new quickShipModel();
        quickShipBoard player1Board = player1.getPlayerGameBoard();
        quickShipBoard player2Board = player2.getPlayerGameBoard();
        player1.copyOpponentGameBoard(player2Board);
        player2.copyOpponentGameBoard(player1Board);
        player1.printMap_debug();
        player2.printMap_debug();
    }
}