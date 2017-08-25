package com.cjt2325.cameralibrary;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.cjt2325.cameralibrary.listener.CaptureListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.cjt2325.cameralibrary.listener.ReturnListener;
import com.cjt2325.cameralibrary.listener.TypeListener;

import java.io.File;

public class JCameraView extends RelativeLayout implements CameraInterface.CamOpenOverCallback, SurfaceHolder.Callback {
    private static final String TAG = "CJT";

    private static final int TYPE_PICTURE = 0x001;
    private static final int TYPE_VIDEO = 0x002;

    public static final int MEDIA_QUALITY_HIGH = 20 * 100000;
    public static final int MEDIA_QUALITY_MIDDLE = 16 * 100000;
    public static final int MEDIA_QUALITY_LOW = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY = 1 * 80000;
    public static final int MEDIA_QUALITY_SORRY_YOU_ARE_GOOD_MAN = 1 * 10000;

    //只能拍照
    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;
    //只能录像
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;
    //两者都可以
    public static final int BUTTON_STATE_BOTH = 0x103;

    private JCameraListener jCameraListener;

    private Context mContext;
    private VideoView mVideoView;
    private ImageView mPhoto;
    private ImageView mSwitchCamera;
    private CaptureLayout mCaptureLayout;
    private FoucsView mFoucsView;
    private MediaPlayer mMediaPlayer;

    private int layout_width;
    private int fouce_size;
    private float screenProp;

    private Bitmap captureBitmap;
    private String videoUrl;
    private long videoDuration = 0;
    private int type = -1;

    private int CAMERA_STATE = -1;
    private static final int STATE_IDLE = 0x010;
    private static final int STATE_RUNNING = 0x020;
    private static final int STATE_WAIT = 0x030;

    private boolean stopping = false;
    private boolean isBorrow = false;
    private boolean takePictureing = false;
    private boolean forbiddenSwitch = false;

    /**
     * switch buttom param
     */
    private int iconSize = 0;
    private int iconMargin = 0;
    private int iconSrc = 0;
    private int duration = 0;
    private boolean showCapture = false;
    private boolean showFront = false;

    /**
     * constructor
     */
    public JCameraView(Context context) {
        this(context, null);
    }

    /**
     * constructor
     */
    public JCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * constructor
     */
    public JCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //get AttributeSet
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCameraView, defStyleAttr, 0);
        iconSize = a.getDimensionPixelSize(R.styleable.JCameraView_iconSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics()));
        iconMargin = a.getDimensionPixelSize(R.styleable.JCameraView_iconMargin, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        iconSrc = a.getResourceId(R.styleable.JCameraView_iconSrc, R.drawable.ic_sync_black_24dp);
        duration = a.getInteger(R.styleable.JCameraView_duration_max, 11 * 1000);
        showCapture = a.getBoolean(R.styleable.JCameraView_showCapture, true);
        showFront = a.getBoolean(R.styleable.JCameraView_showFront, false);
        if (showFront) {
            CameraInterface.getInstance().setSelectedCamera(CameraInterface.getInstance().CAMERA_FRONT_POSITION);
        }

        a.recycle();
        initData();
        initView();
    }

    private void initData() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        layout_width = outMetrics.widthPixels;
        fouce_size = layout_width / 4;
        CAMERA_STATE = STATE_IDLE;
    }

    private void initView() {
        setWillNotDraw(false);
        this.setBackgroundColor(0x000000);
        //VideoView
        mVideoView = new VideoView(mContext);
        LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mVideoView.setLayoutParams(videoViewParam);

        //mPhoto
        mPhoto = new ImageView(mContext);
        LayoutParams photoParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        photoParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        mPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        mPhoto.setLayoutParams(photoParam);
        mPhoto.setBackgroundColor(0x000000);
        mPhoto.setVisibility(INVISIBLE);

        //switchCamera
        mSwitchCamera = new ImageView(mContext);
        LayoutParams imageViewParam = new LayoutParams(iconSize, iconSize);
        imageViewParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        imageViewParam.setMargins(0, iconMargin, iconMargin, 0);
        mSwitchCamera.setLayoutParams(imageViewParam);
        mSwitchCamera.setImageResource(iconSrc);
        mSwitchCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBorrow || switching || forbiddenSwitch) {
                    return;
                }
                switching = true;
                new Thread() {
                    /**
                     * switch camera
                     */
                    @Override
                    public void run() {
                        CameraInterface.getInstance().switchCamera(JCameraView.this);
                    }
                }.start();
            }
        });

        //CaptureLayout
        mCaptureLayout = new CaptureLayout(mContext);
        LayoutParams layout_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout_param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout_param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout_param.setMargins(0, 0, 0, 0);
        mCaptureLayout.setLayoutParams(layout_param);
        mCaptureLayout.setDuration(duration);

        //mFoucsView
        mFoucsView = new FoucsView(mContext, fouce_size);
        LayoutParams foucs_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mFoucsView.setLayoutParams(foucs_param);
        mFoucsView.setVisibility(INVISIBLE);

        //add view to ParentLayout
        this.addView(mVideoView);
        this.addView(mPhoto);
        this.addView(mSwitchCamera);
        if (showCapture) {
            this.addView(mCaptureLayout);
        }
        this.addView(mFoucsView);
        //START >>>>>>> captureLayout lisenter callback
        mCaptureLayout.setCaptureListener(new CaptureListener() {
            @Override
            public void takePictures() {
                if (CAMERA_STATE != STATE_IDLE || takePictureing) {
                    return;
                }
                CAMERA_STATE = STATE_RUNNING;

                //正在拍照时，隐藏拍照按钮
                mCaptureLayout.setBtnCaptureVisibility(View.INVISIBLE);
                takePictureing = true;
                mFoucsView.setVisibility(INVISIBLE);
                CameraInterface.getInstance().takePicture(new CameraInterface.TakePictureCallback() {
                    @Override
                    public void captureResult(Bitmap bitmap) {
                        captureBitmap = bitmap;
                        CameraInterface.getInstance().doStopCamera();
                        type = TYPE_PICTURE;
                        isBorrow = true;
                        CAMERA_STATE = STATE_WAIT;
                        Log.i(TAG, "captureResult takePicture: " + bitmap.getWidth() + " " + bitmap.getHeight());
                        mPhoto.setImageBitmap(bitmap);
                        mPhoto.setVisibility(VISIBLE);

                        mCaptureLayout.startAlphaAnimation();
                        mCaptureLayout.startTypeBtnAnimator();
                        takePictureing = false;
                        mSwitchCamera.setVisibility(INVISIBLE);
                        CameraInterface.getInstance().doOpenCamera(JCameraView.this);
                    }
                });
            }

            @Override
            public void recordShort(long time) {
                if (CAMERA_STATE != STATE_RUNNING && stopping) {
                    return;
                }
                stopping = true;
                mCaptureLayout.setTextWithAnimation("录制时间过短");
                //在MediaRecorder stop前，隐藏录像/拍照按钮。
                // 防止快速连续点击出现无法录像/拍照问题
                mCaptureLayout.setBtnCaptureVisibility(View.INVISIBLE);
                Log.i(TAG, "recordShort: " + time);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "recordShort stopRecord running ");
                        CameraInterface.getInstance().stopRecord(true, new
                                CameraInterface.StopRecordCallback() {
                                    @Override
                                    public void recordResult(String url) {
                                        Log.i(TAG, "Record Stopping ...");
                                        //在MediaRecorder stop后，显示录像/拍照按钮
                                        postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mCaptureLayout.setBtnCaptureVisibility(View.VISIBLE);
                                            }
                                        }, 100);
                                        mCaptureLayout.isRecord(false);
                                        CAMERA_STATE = STATE_IDLE;
                                        stopping = false;
                                        isBorrow = false;
                                    }
                                });
                    }
                }, 50);
            }

            @Override
            public void recordStart() {
                if (CAMERA_STATE != STATE_IDLE && stopping) {
                    return;
                }

                mCaptureLayout.isRecord(true);
                isBorrow = true;
                CAMERA_STATE = STATE_RUNNING;
                mFoucsView.setVisibility(INVISIBLE);
                Log.i("CJT", "startRecorder");
                try {
                    Log.i("CJT", "Vibrator");
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 200}; // 停止 开启 停止 开启
                    vibrator.vibrate(pattern, -1); //重复两次上面的pattern 如果只想震动一次，index设为-1
                } catch (Exception ex) {

                }

                CameraInterface.getInstance().startRecord(mVideoView.getHolder().getSurface(), new CameraInterface
                        .ErrorCallback() {
                    @Override
                    public void onError() {
                        Log.i("CJT", "startRecorder error");
                        mCaptureLayout.isRecord(false);
                        CAMERA_STATE = STATE_WAIT;
                        stopping = false;
                        isBorrow = false;
                    }
                });

            }

            @Override
            public void recordEnd(final long time) {
                Log.i(TAG, "recordEnd time: " + time);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraInterface.getInstance().stopRecord(false, new CameraInterface.StopRecordCallback() {
                            @Override
                            public void recordResult(final String url) {
                                CAMERA_STATE = STATE_WAIT;
                                videoUrl = url;
                                videoDuration = time;
                                type = TYPE_VIDEO;
                                new Thread(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void run() {
                                        try {
                                            if (mMediaPlayer == null) {
                                                mMediaPlayer = new MediaPlayer();
                                            } else {
                                                mMediaPlayer.reset();
                                            }
                                            Log.i("CJT", "URL = " + url);
                                            mMediaPlayer.setDataSource(url);
                                            mMediaPlayer.setSurface(mVideoView.getHolder().getSurface());
                                            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer
                                                    .OnVideoSizeChangedListener() {
                                                @Override
                                                public void
                                                onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                                    updateVideoViewSize(mMediaPlayer.getVideoWidth(), mMediaPlayer
                                                            .getVideoHeight());
                                                }
                                            });
                                            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    mMediaPlayer.start();
                                                }
                                            });
                                            mMediaPlayer.setLooping(true);
                                            mMediaPlayer.prepare();
//                                            videoDuration = mMediaPlayer.getDuration();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        });
                    }
                }, 100);
            }

            @Override
            public void recordZoom(float zoom) {
                CameraInterface.getInstance().setZoom(zoom, CameraInterface.TYPE_RECORDER);
            }
        });

        mCaptureLayout.setTypeListener(new TypeListener() {
            @Override
            public void cancel() {
                if (CAMERA_STATE == STATE_WAIT) {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    handlerPictureOrVideo(type, false);
                }
            }

            @Override
            public void confirm() {
                Log.i(TAG, "confirm: ");
                if (CAMERA_STATE == STATE_WAIT) {
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    handlerPictureOrVideo(type, true);
                }
            }
        });

        mCaptureLayout.setReturnListener(new ReturnListener() {
            @Override
            public void onReturn() {
                if (jCameraListener != null && !takePictureing) {
                    jCameraListener.quit();
                }
            }
        });
        //END >>>>>>> captureLayout lisenter callback
        mVideoView.getHolder().addCallback(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        float heightSize = MeasureSpec.getSize(heightMeasureSpec);
        screenProp = heightSize / widthSize;
    }

    @Override
    public void cameraHasOpened() {
        CameraInterface.getInstance().doStartPreview(mVideoView.getHolder(), screenProp);
    }

    private boolean switching = false;

    @Override
    public void cameraSwitchSuccess() {
        switching = false;
    }

    /**
     * start preview
     */
    public void onResume() {
        Log.i(TAG, "onResume: ");
        CameraInterface.getInstance().registerSensorManager(mContext);
        CameraInterface.getInstance().setSwitchView(mSwitchCamera);
    }

    /**
     * stop preview
     */
    public void onPause() {
        CameraInterface.getInstance().unregisterSensorManager(mContext);
        CameraInterface.getInstance().doStopCamera();
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    private boolean firstTouch = true;
    private float firstTouchLength = 0;
    private int zoomScale = 0;

    /**
     * handler touch focus
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    //显示对焦指示器
                    setFocusViewWidthAnimation(event.getX(), event.getY());
                }
                if (event.getPointerCount() == 2) {
                    Log.i("CJT", "ACTION_DOWN = " + 2);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    firstTouch = true;
                }
                if (event.getPointerCount() == 2) {
                    //第一个点
                    float point_1_X = event.getX(0);
                    float point_1_Y = event.getY(0);
                    //第二个点
                    float point_2_X = event.getX(1);
                    float point_2_Y = event.getY(1);

                    float result = (float) Math.sqrt(Math.pow(point_1_X - point_2_X, 2) + Math.pow(point_1_Y -
                            point_2_Y, 2));

                    if (firstTouch) {
                        firstTouchLength = result;
                        firstTouch = false;
                    }
                    if ((int) (result - firstTouchLength) / 50 != 0) {
                        firstTouch = true;
                        CameraInterface.getInstance().setZoom(result - firstTouchLength, CameraInterface.TYPE_CAPTURE);
                    }
                    Log.i("CJT", "result = " + (result - firstTouchLength));
                }
                break;
            case MotionEvent.ACTION_UP:
                firstTouch = true;
                break;
        }
        return true;
    }

    /**
     * focusview animation
     */
    private void setFocusViewWidthAnimation(float x, float y) {
        if (isBorrow) {
            return;
        }
        if (y > mCaptureLayout.getTop()) {
            return;
        }
        mFoucsView.setVisibility(VISIBLE);
        if (x < mFoucsView.getWidth() / 2) {
            x = mFoucsView.getWidth() / 2;
        }
        if (x > layout_width - mFoucsView.getWidth() / 2) {
            x = layout_width - mFoucsView.getWidth() / 2;
        }
        if (y < mFoucsView.getWidth() / 2 + iconMargin) {
            y = mFoucsView.getWidth() / 2 + iconMargin;
        }
        if (y > mCaptureLayout.getTop() - mFoucsView.getWidth() / 2) {
            y = mCaptureLayout.getTop() - mFoucsView.getWidth() / 2;
        }
        CameraInterface.getInstance().handleFocus(mContext, x, y, new CameraInterface.FocusCallback() {
            @Override
            public void focusSuccess() {
                mFoucsView.setVisibility(INVISIBLE);
            }
        });

        mFoucsView.setX(x - mFoucsView.getWidth() / 2);
        mFoucsView.setY(y - mFoucsView.getHeight() / 2);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFoucsView, "scaleX", 1, 0.6f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFoucsView, "scaleY", 1, 0.6f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFoucsView, "alpha", 1f, 0.3f, 1f, 0.3f, 1f, 0.3f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY).before(alpha);
        animSet.setDuration(200);
        animSet.start();
    }

    public void setJCameraListener(JCameraListener jCameraListener) {
        this.jCameraListener = jCameraListener;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void handlerPictureOrVideo(int type, boolean confirm) {
        Log.i(TAG, "handlerPictureOrVideo: type " + type);
        if (jCameraListener == null || type == -1) {
            return;
        }
        switch (type) {
            case TYPE_PICTURE:
                mPhoto.setVisibility(INVISIBLE);
                if (confirm && captureBitmap != null) {
                    jCameraListener.captureSuccess(captureBitmap);
                } else {
                    if (captureBitmap != null) {
                        captureBitmap.recycle();
                    }
                    captureBitmap = null;
                }
                break;
            case TYPE_VIDEO:
                if (confirm) {
                    //回调录像成功后的URL
                    jCameraListener.recordSuccess(videoUrl, videoDuration);
                } else {
                    //删除视频
                    File file = new File(videoUrl);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                mCaptureLayout.isRecord(false);
                CameraInterface.getInstance().doOpenCamera(JCameraView.this);
                break;
        }
        isBorrow = false;
        mSwitchCamera.setVisibility(VISIBLE);
        CAMERA_STATE = STATE_IDLE;
    }

    public void setSaveVideoPath(String path) {
        CameraInterface.getInstance().setSaveVideoPath(path);
    }

    /**
     * TextureView resize
     */
    public void updateVideoViewSize(float videoWidth, float videoHeight) {
        if (videoWidth > videoHeight) {
            LayoutParams videoViewParam;
            int height = (int) ((videoHeight / videoWidth) * getWidth());
            videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                    height);
            videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout
                    .TRUE);
            mVideoView.setLayoutParams(videoViewParam);
        }
    }

    /**
     * forbidden audio
     */
    public void enableshutterSound(boolean enable) {
    }

    public void forbiddenSwitchCamera(boolean forbiddenSwitch) {
        this.forbiddenSwitch = forbiddenSwitch;
    }

    //启动Camera错误回调
    public void setErrorListener(ErrorListener errorListener) {
        CameraInterface.getInstance().setErrorListener(errorListener);
    }

    //设置CaptureButton功能（拍照和录像）
    public void setFeatures(int state) {
        this.mCaptureLayout.setButtonFeatures(state);
    }

    //设置录制质量
    public void setMediaQuality(int quality) {
        CameraInterface.getInstance().setMediaQuality(quality);
    }


    boolean toOpenCamera = false;
    boolean surfaceCreate = false;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("CJT", "surfaceCreated");
        surfaceCreate = true;
        if (toOpenCamera) {
            openCamera();
            toOpenCamera = false;
        }
    }

    public void openCamera() {
        Log.i(TAG, "openCamera: ");
        toOpenCamera = true;

        if (!surfaceCreate) {
            Log.i(TAG, "openCamera surfaceCreate not ");
            return;
        }
        Log.i(TAG, "openCamera: ");
        surfaceCreate = false;

        new Thread() {
            @Override
            public void run() {
                CameraInterface.getInstance().doOpenCamera(JCameraView.this);
            }
        }.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("CJT", "surfaceDestroyed");
        CameraInterface.getInstance().doDestroyCamera();
    }
}
