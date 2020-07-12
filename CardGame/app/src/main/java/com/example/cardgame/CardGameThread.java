package com.example.cardgame;

public class CardGameThread extends Thread {
    CardGameView m_View;

    public CardGameThread(CardGameView _View){
        m_View = _View;
    }

    @Override
    public void run() {
        super.run();
        while(true){
            m_View.checkMatch( );
        }
    }
}
