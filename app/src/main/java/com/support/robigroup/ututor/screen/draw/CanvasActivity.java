package com.support.robigroup.ututor.screen.draw;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;

import com.support.robigroup.ututor.R;

import java.util.ArrayList;


public class CanvasActivity extends AppCompatActivity {
    String filename = "";
    RelativeLayout relativeLayout;
    Paint paint;
    SketchSheetView view;
    Path path;
    Bitmap bitmap;
    Canvas canvas;
    ImageButton button_clear;
    ImageButton button_nazad;
    ImageButton button_textsize;
    ImageButton button_done;
    ImageButton button_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);

        button_clear = (ImageButton) findViewById(R.id.clearButton);
        button_nazad = (ImageButton) findViewById(R.id.button_nazad);
        button_textsize = (ImageButton) findViewById(R.id.button_textsize);
        button_done = (ImageButton) findViewById(R.id.button_done);
        button_color = (ImageButton) findViewById(R.id.button_color);


        view = new SketchSheetView(this);
        view.setDrawingCacheEnabled(true);



        path = new Path();
        inishPaint(Color.BLACK,5);

        relativeLayout.addView(view, new LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));




        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.resetArrayList();

            }
        });
        button_nazad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        button_textsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paint.setStrokeWidth(3);
            }
        });
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mylog", "onClick: done : "+bitmap);
//                SolutionsAddFragment.result = Bitmap.createBitmap(view.getDrawingCache());
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        button_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inishPaint(Color.RED,paint.getStrokeWidth());
            }
        });
    }

    private void inishPaint(int color,float size){
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    class SketchSheetView extends View {

        public SketchSheetView(Context context) {

            super(context);

            this.setDrawingCacheEnabled(true);

            bitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_4444);

            canvas = new Canvas(bitmap);

//            this.setBackgroundColor(getResources().getColor(lightBlue2));
        }

        private ArrayList<DrawingClass> drawingClassArrayList = new ArrayList<>();

        public void resetArrayList(){
            drawingClassArrayList.clear();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            DrawingClass pathWithPaint = new DrawingClass();

            canvas.drawPath(path, paint);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                path = new Path();

                path.moveTo(event.getX(), event.getY());

                path.lineTo(event.getX(), event.getY());
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                path.lineTo(event.getX(), event.getY());

                pathWithPaint.setPath(path);

                pathWithPaint.setPaint(paint);

                drawingClassArrayList.add(pathWithPaint);
            }

            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (drawingClassArrayList.size() > 0) {
                for(int i = 0; i< drawingClassArrayList.size(); i++){
                    canvas.drawPath(
                            drawingClassArrayList.get(i).getPath(),
                            drawingClassArrayList.get(i).getPaint());
                }
            }

        }
    }


    public class DrawingClass {

        Path DrawingClassPath;
        Paint DrawingClassPaint;

        public Path getPath() {
            return DrawingClassPath;
        }

        public void setPath(Path path) {
            this.DrawingClassPath = path;
        }


        public Paint getPaint() {
            return DrawingClassPaint;
        }

        public void setPaint(Paint paint) {
            this.DrawingClassPaint = paint;
        }
    }




}