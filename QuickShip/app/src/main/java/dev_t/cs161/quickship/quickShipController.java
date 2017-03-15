package dev_t.cs161.quickship;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

public class quickShipController extends Activity {

    private quickShipView _mainView;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mainView = new quickShipView(this);
        setContentView(_mainView);
        newGame();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        _mainView.onResumeMySurfaceView();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        _mainView.onPauseMySurfaceView();
    }

    public void newGame() {
        quickShipModel player1 = new quickShipModel();
        quickShipModel player2 = new quickShipModel();
        quickShipBoard player1Board = player1.getPlayerGameBoard();
        quickShipBoard player2Board = player2.getPlayerGameBoard();
        player1.copyOpponentGameBoard(player2Board);
        player2.copyOpponentGameBoard(player1Board);
        player1.printMap_debug();
        player2.printMap_debug();
        running = true;
    }
}