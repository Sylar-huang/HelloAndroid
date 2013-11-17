package com.example.helloandroid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyRenderer implements GLSurfaceView.Renderer{
	private Triangle mTriangle;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    public volatile float mAngle;
	public void onSurfaceCreated(GL10 unused, EGLConfig config)
	{
		GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
		mTriangle = new Triangle();
		
	}
	public void onDrawFrame(GL10 unused)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//		long time = SystemClock.uptimeMillis() % 4000L;
//		float mAngle = 0.090f * ((int)time);
//		System.out.println("angle changes with: "+mAngle);
		
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 4f, 0f, 0f, -3f, 0f, 1f, 0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
		mTriangle.draw(mMVPMatrix);
	}
	public void onSurfaceChanged(GL10 unused, int width, int height)
	{
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float)width / height;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1,3, 7);
	}
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }	
}
class Triangle {


    private final String vertexShaderCode =
    	"uniform mat4 uMVPMatrix;"+
        "attribute vec4 vPosition;" +
    	"varying vec3 ucolor;" + 
        "void main() {" +
    	"if(abs(vPosition.x) == 0.5)"+
        "{ucolor = vec3(0.0,1.0,0.3);}"+
    	"else"+
    	"{ucolor = vec3(1.0,1.0,1.0);}" +
        "gl_Position = vPosition * uMVPMatrix;" +
        "}";

    private final String fragmentShaderCode =
       "precision mediump float;" +
        "uniform vec4 vColor;" +
        "varying vec3 ucolor;" +
        "void main() {" +
        "  gl_FragColor = vec4(ucolor, 1.0) ;" +
        "}";
//	private String fpath = "E:\\Android development\\HelloAndroid\\src\\com\\example\\helloandroid\\fShader.file";
//	private String vpath = "E:\\Android development\\HelloAndroid\\src\\com\\example\\helloandroid\\vShader.file";
//	private final String vertexShaderCode = reader.ShaderReader(vpath);
//	private final String fragmentShaderCode = reader.ShaderReader(fpath);
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:
         0.0f,  0.6f, 0.0f,   // top
        -0.5f, -0.3f, 0.0f,   // bottom left
         0.5f, -0.3f, 0.0f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
