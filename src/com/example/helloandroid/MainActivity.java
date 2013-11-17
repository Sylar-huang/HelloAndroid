package com.example.helloandroid;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {
	private GLSurfaceView mGLView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
class MyGLSurfaceView extends GLSurfaceView{
	private MyRenderer mRenderer = new MyRenderer();
	private float mPreviousX;
	private float mPreviousY;
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	public MyGLSurfaceView(Context context){
		super(context);
		try{
			setEGLContextClientVersion(2);
			super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
			setRenderer(mRenderer);
	//		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public boolean onTouchEvent(MotionEvent e){
		float x = e.getX();
		float y = e.getY();
		switch(e.getAction()){
		case MotionEvent.ACTION_MOVE:
			float dx = x - mPreviousX;
			float dy = y - mPreviousY;
			if(y > getHeight()/2)
			{
				dx = dx *-1;
				
			}
			if(x < getWidth()/2){
				dy = dy*-1;
			}
			mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;
			requestRender();
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}
}
