package com.baekcedar.android.customview_rectai;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static int GROUND_LIMIT = 10;
    FrameLayout ground;
    CustomView cv;

    private int groundUnit = 0;
    private int unit = 0;
    private static int player_x = 0;
    private static int player_y = 0;
    Enemy enemy;
    private static int goToX=0;
    private static int goToY=0;
    Button btnUp,btnDown,btnLeft,btnRight,btnStart;
    // Y, X

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dp = getResources().getDisplayMetrics();
        groundUnit = dp.widthPixels;
        unit = groundUnit / GROUND_LIMIT;

        btnUp = (Button) findViewById(R.id.btnUp);
        btnDown = (Button) findViewById(R.id.btnDown);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        ground = (FrameLayout) findViewById(R.id.ground);
        cv = new CustomView(this);
        ground.addView(cv);

        enemy = new Enemy(cv);

        enemy.start();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnUp    : player_y = player_y + checkCollision("y",- 1); break;    // 위로가면 unit 만큼 y좌표 감소
            case R.id.btnDown  : player_y = player_y + checkCollision("y",+ 1); break;
            case R.id.btnLeft  : player_x = player_x + checkCollision("x",- 1); break;  // 왼쪽으로 unit만큼 x좌표 감소
            case R.id.btnRight : player_x = player_x + checkCollision("x",+ 1); break;
            case R.id.btnStart : enemy.start(); break;
        }
        cv.invalidate();
    }
    // 충돌검사
    private int checkCollision(String direction, int nextValue){
        // 외곽선 체크
        if(direction.equals("y")){
            // y축에서 다음 이동하는 곳의 좌표가
            // 0보다 작거나, GROUND_LIMIT 즉 canvas 보다 크면 0을 리턴해서 이동하지 않게 한다
            if( (player_y + nextValue) < 0 || (player_y + nextValue) >= GROUND_LIMIT )
                return 0;
        }else{
            if( (player_x + nextValue) < 0 || (player_x + nextValue) >= GROUND_LIMIT )
                return 0;
        }


        return nextValue;
    }

    class Enemy extends Thread {
        int x=0;
        int y=0;
        int size = unit;

        CustomView view ;

        Enemy(CustomView view){
            this.view = view;
            view.add(this);
        }

        @Override
        public void run() {
            int distenceX=0;
            int distenceY=0;
            while( true ){
                distenceX = player_x*unit+unit;
                distenceY = player_y*unit+unit;
                if(x > distenceX ){
                    x-= 1;
                }
                if(x < distenceX ){
                    x += 1;
                }
                if(x > distenceY ){
                    y -= 1;
                }
                if(x < distenceY){
                    y += 1;
                }
                view.postInvalidate();
                try{
                    Thread.sleep(10);
                 } catch (Exception e){}

            }
        }




    }
    class CustomView extends View {
        Paint paint = new Paint();
        ArrayList<Enemy>  enemies = new ArrayList<>();
        public void add(Enemy enemy){
            enemies.add(enemy);
        }
        public CustomView(Context context) {
            super(context);

        }
        @Override
        protected void onDraw(Canvas canvas) {
            // 운동장 배경 그리기
            paint.setColor(Color.CYAN);
            canvas.drawRect(
                    0, 0
                    ,groundUnit, groundUnit, paint);

            // map 세팅된 장애물 그리기


            // 플레이어 그리기
            paint.setColor(Color.MAGENTA);
            canvas.drawRect(
                    player_x*unit
                    , player_y*unit
                    , player_x*unit + unit
                    , player_y*unit + unit
                    , paint);


            for(Enemy enemy  : enemies){
                paint.setColor(Color.BLACK);
                canvas.drawCircle(
                        enemy.x, enemy.y, enemy.size-10, paint);
            }




        }
    }
}

