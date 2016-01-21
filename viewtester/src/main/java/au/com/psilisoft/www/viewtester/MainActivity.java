package au.com.psilisoft.www.viewtester;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 *
 */
public class MainActivity extends Activity {

    ImageView mImage1;
    ImageView mImage2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_details);



        /*setContentView(R.layout.images);
        mImage1 = (ImageView) findViewById(R.id.image_1);
        mImage2 = (ImageView) findViewById(R.id.image_2);

        Bitmap original_bmp = Bitmap.createBitmap(300, 500, Bitmap.Config.ARGB_8888);
        mImage1.setImageBitmap(original_bmp);

        Canvas canvas = new Canvas(original_bmp);
        Paint paint = new Paint();
        for (int i = 0; i < 500; i += 50) {
            for (int j = 0; j < 300; j += 50) {
                paint.setColor(Color.rgb((int) (i / 500. * 255), (int) (j / 300. * 255), (int) ((i+j) / 800. * 255)));
                canvas.drawRect(j, i, j + 50, i + 50, paint);
            }
        }

        Matrix matrix = new Matrix();
        matrix.preTranslate(-original_bmp.getWidth() / 2f, -original_bmp.getHeight() / 2f);
        matrix.setRotate(90);
        matrix.postTranslate(original_bmp.getWidth() / 2f, original_bmp.getHeight() / 2f);

        int value = Math.min(original_bmp.getHeight(), original_bmp.getWidth());

        Bitmap finalBitmap = Bitmap.createBitmap(original_bmp, 0, 0, value, value, matrix, false);

        mImage2.setImageBitmap(finalBitmap);*/












        /*LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linearLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 100);

        CenterView centerView = new CenterView(this);
        centerView.setLayoutParams(params);
        centerView.setBackgroundColor(Color.rgb(0xAA, 0xBB, 0xCC));
        NormalView normalView = new NormalView(this);
        normalView.setLayoutParams(params);
        normalView.setBackgroundColor(Color.rgb(0xCC, 0xAA, 0xBB));
        linearLayout.addView(centerView);
        linearLayout.addView(normalView);*/
    }

    class CenterView extends View {

        public CenterView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setTextAlign(Paint.Align.CENTER);
            Rect bounds = new Rect();
            paint.getTextBounds("1234", 0, 4, bounds);
            canvas.drawText(
                    "1234",
                    getWidth() / 2f,
                    getHeight() / 2f + bounds.height() / 2f,
                    paint);
        }
    }

    class NormalView extends View {

        public NormalView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setTextSize(100);
            Rect bounds = new Rect();
            paint.getTextBounds("1234", 0, 4, bounds);
            canvas.drawText(
                    "1234",
                    getWidth() / 2f - bounds.width() / 2f,
                    getHeight() / 2f + bounds.height() / 2f,
                    paint);

        }
    }
}
