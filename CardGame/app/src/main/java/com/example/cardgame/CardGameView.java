package com.example.cardgame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CardGameView extends View {

    Bitmap m_BackGroundImage;
    Bitmap m_CardBackSide;

    Bitmap m_Card_Red;
    Bitmap m_Card_Green;
    Bitmap m_Card_Blue;

    Card m_Shuffle[][];

    Card m_FirstSelectCard;
    Card m_SecondSelectCard;

    CardGameThread m_Thread;

    private int m_State;

    public static final int STATE_READY = 0;
    public static final int STATE_GAME = 1;
    public static final int STATE_END = 2;

    public CardGameView(Context context){
        super(context);
        m_State = STATE_READY;

        DisplayMetrics _Metrics = getResources().getDisplayMetrics();
        int _ScreenHeight = _Metrics.heightPixels;
        int _ScreenWidth = _Metrics.widthPixels;


        //배경 이미지
        m_BackGroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background, null);
        m_BackGroundImage = resizeBitmap(m_BackGroundImage, _ScreenWidth, _ScreenHeight);
        //뒷면 이미지
        m_CardBackSide = BitmapFactory.decodeResource(getResources(), R.drawable.backside, null);
        //색별 카드 이미지
        m_Card_Red = BitmapFactory.decodeResource(getResources(), R.drawable.front_red, null);
        m_Card_Green = BitmapFactory.decodeResource(getResources(), R.drawable.front_green, null);
        m_Card_Blue = BitmapFactory.decodeResource(getResources(), R.drawable.front_blue, null);

        //화면에 표시할 카드 할당
        m_Shuffle = new Card[3][2];

        m_Thread = new CardGameThread(this);

        m_Thread.start();
        setCardShuffle();
    }

    private Bitmap resizeBitmap(Bitmap _Bitmap, int _NewWidth, int _NewHeight){
        Bitmap _Resized = Bitmap.createScaledBitmap(_Bitmap, _NewWidth, _NewHeight, true);
        return _Resized;
    }

    public void onDraw(Canvas canvas){
        int _PositionX;
        int _PositionY;

        int _ImageWidth;
        int _ImageHeight;

        _ImageWidth = m_CardBackSide.getWidth();
        _ImageHeight = m_CardBackSide.getHeight();

        //배경 그리기
        canvas.drawBitmap(m_BackGroundImage, 0, 0, null);
        //카드 그려주기
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 2; j++){
                //고정값으로 계산하게 했는데, 추후 상대값으로 하게 변경요망
                _PositionX = 90 + i * (_ImageWidth + 100);
                _PositionY = 1020 + j * (50 + _ImageHeight);

                if(m_Shuffle[i][j].m_State == Card.CARD_CLOSE){
                    canvas.drawBitmap(m_CardBackSide, _PositionX, _PositionY, null);
                }else if(m_Shuffle[i][j].m_State == Card.CARD_SHOW || m_Shuffle[i][j].m_State == Card.CARD_MATCHED  || m_Shuffle[i][j].m_State == Card.CARD_PLAYEROPEN ) {
                    if (m_Shuffle[i][j].m_Color == Card.IMG_RED) {
                        canvas.drawBitmap(m_Card_Red, _PositionX, _PositionY, null);
                    } else if (m_Shuffle[i][j].m_Color == Card.IMG_GREEN) {
                        canvas.drawBitmap(m_Card_Green, _PositionX, _PositionY, null);
                    } else if (m_Shuffle[i][j].m_Color == Card.IMG_BLUE) {
                        canvas.drawBitmap(m_Card_Blue, _PositionX, _PositionY, null);
                    }
                }
            }
        }
    }

    public void setCardShuffle(){
        int _CardSize;
        int _FirstRandomX;
        int _FirstRandomY;

        int _SecondRandomX;
        int _SecondRandomY;

        Card _TempCard;

        m_Shuffle[0][0] = new Card(Card.IMG_RED);
        m_Shuffle[0][1] = new Card(Card.IMG_BLUE);
        m_Shuffle[1][0] = new Card(Card.IMG_GREEN);
        m_Shuffle[1][1] = new Card(Card.IMG_GREEN);
        m_Shuffle[2][0] = new Card(Card.IMG_BLUE);
        m_Shuffle[2][1] = new Card(Card.IMG_RED);

        _CardSize = m_Shuffle.length * m_Shuffle[0].length;

        // 카드를 섞음
        for(int i = 0; i < _CardSize * 3; i++) {
            _FirstRandomY = (int) (Math.random() * m_Shuffle.length);
            _FirstRandomX = (int) (Math.random() * m_Shuffle[0].length);

            _SecondRandomY = (int) (Math.random() * m_Shuffle.length);
            _SecondRandomX = (int) (Math.random() * m_Shuffle[0].length);

            _TempCard = m_Shuffle[_FirstRandomY][_FirstRandomX];
            m_Shuffle[_FirstRandomY][_FirstRandomX] = m_Shuffle[_SecondRandomY][_SecondRandomX];
            m_Shuffle[_SecondRandomY][_SecondRandomX] = _TempCard;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(m_State == STATE_READY){
            startGame();
            m_State = STATE_GAME;
        }else if(m_State == STATE_GAME){
            int _ImageWidth;
            int _ImageHeight;
            int _X = (int)event.getX();
            int _Y = (int)event.getY();

            _ImageWidth = m_CardBackSide.getWidth();
            _ImageHeight = m_CardBackSide.getHeight();

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                for (int i=0; i<3; i++) {
                    for (int j=0; j<2; j++) {
                        int _xPosition = 90 + i * (_ImageWidth + 100);
                        int _yPosition = 1020 + j * (50 + _ImageHeight);
                        //터치확인 rect 생성
                        Rect _rectCard = new Rect(_xPosition, _yPosition, _xPosition + _ImageWidth, _yPosition + _ImageHeight);
                        // 선택된 카드 뒤집기
                        if (_rectCard .contains(_X,_Y)) {
                            if(m_FirstSelectCard == null){
                                m_FirstSelectCard = m_Shuffle[i][j];
                                m_FirstSelectCard.m_State = Card.CARD_PLAYEROPEN;
                            }else{
                                m_SecondSelectCard = m_Shuffle[i][j];
                                m_SecondSelectCard.m_State = Card.CARD_PLAYEROPEN;
                            }
                        }
                    }
                }
            }
        }else if(m_State == STATE_END){
            m_State = STATE_READY;
        }

        postInvalidate();
        return super.onTouchEvent(event);
    }

    public void startGame(){
        m_Shuffle [0][0]. m_State = Card. CARD_CLOSE;
        m_Shuffle [0][1]. m_State = Card. CARD_CLOSE;
        m_Shuffle [1][0]. m_State = Card. CARD_CLOSE;
        m_Shuffle [1][1]. m_State = Card. CARD_CLOSE;
        m_Shuffle [2][0]. m_State = Card. CARD_CLOSE;
        m_Shuffle [2][1]. m_State = Card. CARD_CLOSE;

        m_FirstSelectCard = null;
        m_SecondSelectCard = null;
    }

    public void checkMatch(){
        // 두 카드의 색상을 비교합니다.
        if ( m_FirstSelectCard == null || m_SecondSelectCard == null){
            return;
        }

        if ( m_FirstSelectCard.m_Color == m_SecondSelectCard.m_Color) {
            // 두 카드의 색상이 같으면 두 카드를 맞춘 상태로 바꿉니다.
            m_FirstSelectCard.m_State = Card. CARD_MATCHED;
            m_SecondSelectCard.m_State = Card. CARD_MATCHED;
        }else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { }
            // 두 카드의 색상이 다른 경우 두 카드를 이전처럼 뒷면으로 돌려줍니다.
            m_FirstSelectCard.m_State = Card. CARD_CLOSE;
            m_SecondSelectCard.m_State = Card. CARD_CLOSE;
        }
        // 다시 선택할 수 있도록 null로 설정
        m_FirstSelectCard = null;
        m_SecondSelectCard = null;

        postInvalidate( );
    }
}