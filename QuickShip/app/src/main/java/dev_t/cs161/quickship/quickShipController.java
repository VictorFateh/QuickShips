package dev_t.cs161.quickship;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class quickShipController extends Activity implements Runnable {

    private quickShipView _mainView;
    private quickShipController _mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mainController = new quickShipController();
        _mainView = (quickShipView) findViewById(R.id.main_screen);

        quickShipModel player1 = new quickShipModel();
        quickShipModel player2 = new quickShipModel();
        quickShipBoard player1Board = player1.getPlayerGameBoard();
        quickShipBoard player2Board = player2.getPlayerGameBoard();
        player1.copyOpponentGameBoard(player2Board);
        player2.copyOpponentGameBoard(player1Board);
        player1.printMap_debug();
        player2.printMap_debug();
    }

    public void clearCanvas(View v) {
        _mainView.clearCanvas();
    }

    @Override
    public void run() {
        
    }
}