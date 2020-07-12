package com.example.cardgame;

public class Card {
    // 1. 카드 상태 상수 정의
    public static final int CARD_SHOW = 0;
    public static final int CARD_CLOSE = 1;
    public static final int CARD_PLAYEROPEN = 2;
    public static final int CARD_MATCHED = 0;

    public static final int IMG_RED = 1;
    public static final int IMG_GREEN = 2;
    public static final int IMG_BLUE = 3;

    public int m_Color;
    public int m_State;

    public Card(int _Color){
        m_State = CARD_SHOW;
        m_Color = _Color;
    }
}
