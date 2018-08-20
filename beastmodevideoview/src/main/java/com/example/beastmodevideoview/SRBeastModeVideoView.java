package com.example.beastmodevideoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.SeekBar;

import java.io.IOException;

/**
 * Created by Sven on 19.08.2018.
 */

public class SRBeastModeVideoView extends TextureView implements TextureView.SurfaceTextureListener{

    private MediaPlayer mediaPlayer;

    private Uri video;

    private String path;
    
    public SRBeastModeVideoView(Context context) {
        super(context);
    }

    public SRBeastModeVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SRBeastModeVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SRBeastModeVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface s = new Surface(surface);

        try {
            mediaPlayer = new MediaPlayer();
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            mediaPlayer.setDataSource(inputStream.getFD());
            inputStream.close();
            mediaPlayer.setDataSource(fd);
            mediaPlayer.setSurface(s);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    private float caluclateCrop(float bot) {
        float distance = (float) mediaPlayer.getVideoHeight() - (float) mediaPlayer.getVideoWidth();

        float reducedBot = bot - (float) mediaPlayer.getVideoWidth();

        float percantage = (float) getWidth() / 100f;

        float perc = reducedBot * 100 / distance;


        return perc * percantage;
    }

    public void calculateScaling(float i) {
        Matrix matrix = new Matrix();
        float a = ((float) getWidth() / (float) mediaPlayer.getVideoWidth());
        float b = ((float) getHeight() / (float) mediaPlayer.getVideoHeight());
        float max = Math.max(a, b);
        float sx = max / a;
        float sy = max / b;
        matrix.setScale(sx, sy, getWidth() / 2f, caluclateCrop(i));
        setTransform(matrix);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void initializeBeastMode() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            setSurfaceTextureListener(this);
        }
    }

    public void setDataSource(Uri uri){
        video = uri;
    }
    
    public void setDataSource(String path){
        this.path = path;
    }

    public void start(){
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }
}

