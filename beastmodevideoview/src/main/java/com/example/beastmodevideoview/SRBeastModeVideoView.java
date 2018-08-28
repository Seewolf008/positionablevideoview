package com.example.beastmodevideoview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;

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

    private float bot;

    private boolean isInternetVideo;

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
                    calculateScaling(bot + mediaPlayer.getVideoWidth());
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
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void initializeBeastMode(Uri videoUri, float bot) {
        this.video = videoUri;
        this.bot = bot;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaController = new MediaController(getContext());
            mediaController.setMediaPlayer(SRBeastModeVideoView.this);
            mediaController.setAnchorView(SRBeastModeVideoView.this);
            setSurfaceTextureListener(this);

        }
    }

    public void initializeBeastMode(String videoPath, float bot) {
        this.path = videoPath;
        this.bot = bot;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaController = new MediaController(getContext());
            mediaController.setMediaPlayer(SRBeastModeVideoView.this);
            mediaController.setAnchorView(SRBeastModeVideoView.this);
            setSurfaceTextureListener(this);
        }
    }

    public void initializeBeastMode(String videoPath, float bot, boolean internet) {
        this.link = videoPath;
        this.bot = bot;
        isInternetVideo = true;
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
        return mediaPlayer.getCurrentPosition();
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


    public void setDataSource(Uri uri) {
        video = uri;
    }

    public void setDataSource(String path, float bot) {
        this.path = path;
        this.bot = bot;
    }

    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    private float caluclateCrop(float bot) {
        float distance = (float) mediaPlayer.getVideoHeight() - (float) mediaPlayer.getVideoWidth();

        float reducedBot = bot - (float) mediaPlayer.getVideoWidth();

        float percantage = (float) getWidth() / 100f;

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

    public float getBot() {
        return bot;
    }

    public boolean isInternetVideo() {
        return isInternetVideo;
    }
}


