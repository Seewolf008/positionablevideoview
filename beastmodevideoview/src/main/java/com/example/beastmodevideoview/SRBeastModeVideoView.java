package com.example.beastmodevideoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;

/**
 * Created by Sven on 19.08.2018.
 */

public class SRBeastModeVideoView extends TextureView implements TextureView.SurfaceTextureListener, MediaController.MediaPlayerControl {

    private MediaPlayer mediaPlayer;

    private MediaController mediaController;

    private boolean isPrepared;

    private Uri video;

    private String path;

    private String link;


    private boolean isInternetVideo;

    public static final int TOP = 0;
    public static final int RIGHT = 1;
    public static final int BOT = 2;
    public static final int LEFT = 3;

    private int direction;
    private float value;

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
        if(mediaPlayer == null){
            initMedia();
        }
        try {
            if (path != null) {
                if (!path.equals("")) {
                    File file = new File(path);
                    FileInputStream inputStream = new FileInputStream(file);
                    mediaPlayer.setDataSource(inputStream.getFD());
                    inputStream.close();
                }
            } else if (video != null) {
                mediaPlayer.setDataSource(getContext(), video);
            } else if (isInternetVideo) {
                if (!link.equals("")) {
                    mediaPlayer.setDataSource(link);
                }
            }
            mediaPlayer.setSurface(s);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    while (mediaPlayer.getVideoHeight() == 0) {
                    }
                    isPrepared = true;
                    mediaPlayer.seekTo(0);
                    mediaController.setEnabled(true);
                    mediaController.show();
                    switch (direction) {
                        case TOP:
                            calculateScalingVertical(value + mediaPlayer.getVideoWidth());
                            break;
                        case RIGHT:
                            calculateScalingHorizontal(value);
                            break;
                        case BOT:
                            calculateScalingVertical(value);
                            break;
                        case LEFT:
                            calculateScalingHorizontal(value + mediaPlayer.getVideoHeight());
                            break;
                    }
                }
            });

            mediaPlayer.prepareAsync();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IllegalArgumentException | IOException | IllegalStateException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaController.hide();
        mediaPlayer.release();
        mediaPlayer = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void initializeBeastMode(Uri videoUri, float value, int direction) {
        this.video = videoUri;
        this.direction = direction;
        this.value = value;
        initMedia();
    }

    public void initializeBeastMode(String videoPath, float value, int direction) {
        this.path = videoPath;
        this.direction = direction;
        this.value = value;
        initMedia();
    }

    public void initializeBeastMode(String videoPath, float value, boolean internet, int direction) {
        this.link = videoPath;
        this.direction = direction;
        this.value = value;
        this.isInternetVideo = true;
        initMedia();
    }

    private void initMedia() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaController = new MediaController(getContext());
            mediaController.setMediaPlayer(SRBeastModeVideoView.this);
            mediaController.setAnchorView(SRBeastModeVideoView.this);
            setSurfaceTextureListener(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaController.show();
        return false;
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        if(mediaPlayer != null){
           return mediaPlayer.getCurrentPosition();
        } 
        return 0;
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    private float caluclateCropVertical(float value) {
        float distance = (float) mediaPlayer.getVideoHeight() - (float) mediaPlayer.getVideoWidth();

        float reducedBot = value - (float) mediaPlayer.getVideoWidth();

        float percantage = (float) getWidth() / 100f;

        float perc = reducedBot * 100 / distance;


        return perc * percantage;
    }

    private float caluclateCropHorizontal(float value) {
        float distance = (float) mediaPlayer.getVideoWidth() - (float) mediaPlayer.getVideoHeight();

        float reducedBot = value - (float) mediaPlayer.getVideoHeight();

        float percantage = (float) getHeight() / 100f;

        float perc = reducedBot * 100 / distance;


        return perc * percantage;
    }

    public void changeSeekBarColor(int color) {
        SeekBar seekBar;
        LinearLayout root;
        LinearLayout child;
        root = (LinearLayout) mediaController.getChildAt(0);
        child = (LinearLayout) root.getChildAt(1);
        seekBar = (SeekBar) child.getChildAt(1);
        seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void calculateScalingVertical(float i) {
        Matrix matrix = new Matrix();
        float a = ((float) getWidth() / (float) mediaPlayer.getVideoWidth());
        float b = ((float) getHeight() / (float) mediaPlayer.getVideoHeight());
        float max = Math.max(a, b);
        float sx = max / a;
        float sy = max / b;
        matrix.setScale(sx, sy, getWidth() / 2f, caluclateCropVertical(i));
        setTransform(matrix);
    }

    private void calculateScalingHorizontal(float i) {
        Matrix matrix = new Matrix();
        float a = ((float) getWidth() / (float) mediaPlayer.getVideoWidth());
        float b = ((float) getHeight() / (float) mediaPlayer.getVideoHeight());
        float max = Math.max(a, b);
        float sx = max / a;
        float sy = max / b;
        matrix.setScale(sx, sy, caluclateCropHorizontal(i), getWidth() / 2f);
        setTransform(matrix);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public MediaController getMediaController() {
        return mediaController;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public Uri getVideo() {
        return video;
    }

    public String getPath() {
        return path;
    }

    public String getLink() {
        return link;
    }

    public float getValue() {
        return value;
    }

    public boolean isInternetVideo() {
        return isInternetVideo;
    }

}


