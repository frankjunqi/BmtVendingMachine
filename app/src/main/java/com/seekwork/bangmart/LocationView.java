package com.seekwork.bangmart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bangmart.nt.machine.Area;
import com.bangmart.nt.machine.Floor;
import com.bangmart.nt.machine.Location;

import java.util.HashMap;

/**
 * Created by gongtao on 2017/11/13.
 */

public class LocationView extends android.support.v7.widget.AppCompatImageView {
    private int iAreaIndex = -1, iFloorNum = 0, iLocationNum = 0;

    private Area area;

    private Bitmap mBitmap;
    private Canvas canvas;
    private Paint paint;

    public final int WIDTH = 1080;   //界面的宽度
    public final int HEIGHT = 900;   //界面的高度
    private int widthInFact = 1000;  //实际货柜的有效宽度
    private int heightInFact = 1500; //实际货柜的有效高度

    private int marginLeft = 10, marginRight = 10, marginBottom = 10, marginTop = 50;
    private int strokeWidth = 5;

    private boolean isSellOut = false;

    private final HashMap<Pos, Rect> layout = new HashMap<>();

    private OnSelectedListener onSelectedListener = null;

    public LocationView(Context context) {
        super(context);
    }

    public LocationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Area area) {
        this.area = area;
        iAreaIndex = area.areaNo;
        iFloorNum = area.getFloorCount();

        this.widthInFact = area.getWidth();
        this.heightInFact = area.getHeight();

        mBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        setImageBitmap(mBitmap);
        setBackgroundColor(Color.WHITE);

        // 创建一张画布
        canvas = new Canvas(mBitmap);

        paint = new Paint();
        paint.setStrokeWidth(strokeWidth);

        // 以下为默认值，不可改变
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        setOnTouchListener(new OnTouchListener() {
            private Rect selectedLocation;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (iFloorNum == 0) {
                    return false;
                }

                switch (motionEvent.getAction() & motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();

                        if (y < 100 && x > WIDTH - 300) {
                            isSellOut = !isSellOut;
                            Paint tmpPaint = new Paint();

                            tmpPaint.setStrokeWidth(2);
                            tmpPaint.setDither(true);
                            tmpPaint.setAntiAlias(true);
                            tmpPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            tmpPaint.setStrokeJoin(Paint.Join.ROUND);
                            tmpPaint.setStrokeCap(Paint.Cap.ROUND);

                            tmpPaint.setColor((isSellOut) ? Color.RED : Color.WHITE);

                            canvas.drawRect(WIDTH - 100, 18, WIDTH - 80, 38, tmpPaint);
                            invalidate();
                            break;
                        }

                        for (Pos key : layout.keySet()) {
                            Rect rectLocation = layout.get(key);

                            if (rectLocation.contains(x, y)) {

                                if (selectedLocation != null) {
                                    paint.setColor(Color.WHITE);
                                    paint.setStyle(Paint.Style.FILL_AND_STROKE);
                                    canvas.drawCircle(selectedLocation.left + 5 + selectedLocation.width() / 2, selectedLocation.top + 20 + selectedLocation.height() / 2, 30, paint);
                                }
                                paint.setColor(Color.LTGRAY);
                                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                                canvas.drawCircle(rectLocation.left + 5 + rectLocation.width() / 2, rectLocation.top + 20 + rectLocation.height() / 2, 30, paint);

                                invalidate();

                                selectedLocation = rectLocation;

                                if (onSelectedListener != null) {
                                    onSelectedListener.onSelected(iAreaIndex, key.floor, key.location, isSellOut);
                                }
                            }
                        }

                        break;
                    default:
                }
                return false;
            }
        });
    }

    public void refresh() {
        if (iFloorNum == 0) {
            return;
        }

        canvas.drawColor(Color.WHITE);

        Paint textPaint = new Paint();
        // 画笔颜色为蓝色
        textPaint.setColor(Color.BLUE);
        // 宽度5个像素
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(30);

        // 以下为默认值，不可改变
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setStrokeCap(Paint.Cap.ROUND);

        iLocationNum = area.getLocationCount();

        StringBuilder sb = new StringBuilder();
        sb.append("第").append(iAreaIndex).append("号柜").append("         共有").append(iFloorNum).append("层货架 ").append(iLocationNum).append("个货位");
        canvas.drawText(sb.toString(), marginLeft, marginTop - 10, textPaint);

        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(marginLeft, marginTop, WIDTH - marginRight, HEIGHT - marginBottom, paint);

        Paint tmpPaint = new Paint();
        tmpPaint.setColor(Color.RED);
        tmpPaint.setStrokeWidth(2);
        tmpPaint.setDither(true);
        tmpPaint.setAntiAlias(true);
        tmpPaint.setStyle(Paint.Style.STROKE);
        tmpPaint.setStrokeJoin(Paint.Join.ROUND);
        tmpPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawRect(WIDTH - 105, 13, WIDTH - 75, 43, tmpPaint);

        tmpPaint.setColor((isSellOut) ? Color.RED : Color.WHITE);
        tmpPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(WIDTH - 100, 18, WIDTH - 80, 38, tmpPaint);

        textPaint.setColor(Color.RED);
        canvas.drawText("出货", WIDTH - 70, marginTop - 10, textPaint);

        drawLayoutByFact();
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    private int getBoundLeft() {
        return marginLeft + strokeWidth + 10;
    }

    private int getBoundTop() {
        return marginTop + 5;
    }

    private int getBoundRight() {
        return marginRight + strokeWidth + 10;
    }

    private int getBoundBottom() {
        return marginBottom + strokeWidth + 10;
    }

    private int getBoundWidth() {
        return WIDTH - getBoundLeft() - getBoundRight();
    }

    private int getBoundHeight() {
        return HEIGHT - getBoundTop() - getBoundBottom();
    }

    private void drawLayoutByFact() {

        float rateX = (float) (WIDTH - marginLeft - marginRight) / (float) widthInFact;
        float rateY = (float) (HEIGHT - marginTop - marginBottom) / (float) heightInFact;

        int upFloor = getBoundTop();

        for (int i = 0; i < iFloorNum; i++) {
            Floor floor = area.getFloor(i);

            int y = 0, x1 = WIDTH, x2 = WIDTH - getBoundRight();
            int rightBound = WIDTH - getBoundRight();

            for (int j = floor.getCount() - 1; j >= 0; j--) {
                Location coordinate = floor.getLocation(j);

                int x = (int) ((coordinate.x - area.getPos()) * rateX + getBoundLeft() + 20);
                y = HEIGHT - (int) (coordinate.y * rateY) - getBoundBottom();

                paint.setColor(Color.BLUE);
                canvas.drawCircle(x, y, 10, paint);

                x -= 20;
                y += 20;

                int floorHeight = y - upFloor;
                int locationWidth = rightBound - x;

                paint.setColor(Color.GREEN);
                canvas.drawLine(x, y - 50, x, y, paint);
                x1 = (x1 > x) ? x : x1;

                layout.put(new Pos(i, j), new Rect(x, y - floorHeight, x + locationWidth, y));
                rightBound = x;
            }

            canvas.drawLine(x1, y, x2, y, paint);
            canvas.drawLine(x2, y - 50, x2, y, paint);
            upFloor = y;
        }
    }

    interface OnSelectedListener {
        void onSelected(int area, int floor, int location, boolean isSellOut);
    }

    class Pos {
        int floor, location;

        Pos(int f, int l) {
            floor = f;
            location = l;
        }

        @Override
        public boolean equals(Object obj) {
            return (this.floor == ((Pos) obj).floor &&
                    this.location == ((Pos) obj).location);
        }
    }
}
