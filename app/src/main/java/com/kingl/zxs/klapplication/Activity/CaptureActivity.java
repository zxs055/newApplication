package com.kingl.zxs.klapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.kingl.zxs.klapplication.R;
import com.kingl.zxs.klapplication.ZXing.Camera.CameraManager;
import com.kingl.zxs.klapplication.ZXing.Camera.ViewfinderView;
import com.kingl.zxs.klapplication.ZXing.decoding.CaptureActivityHandler;
import com.kingl.zxs.klapplication.ZXing.decoding.DecodeFormatManager;
import com.kingl.zxs.klapplication.ZXing.decoding.InactivityTimer;
import com.kingl.zxs.klapplication.utils.ToastUtil;

import java.io.IOException;
import java.util.Vector;

public class CaptureActivity extends Activity implements Callback {


    //region 公共
//    private Button commitBtn;
//    private Button cancelBtn;
    private TextView titiltxt;
    private ImageButton backImgBtn;
    private ImageView scanImgBtn;
    //endregion

    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private boolean playBeep;
    private boolean vibrate;
    private MediaPlayer mediaPlayer;

    private Camera camera;
    private Camera.Parameters parameter;
    private boolean isOpen=false;
    private int isSelect=0;

    private CaptureActivityHandler handler;

    private static final float BEEP_VOLUME = 0.10f;
    private ImageView lightImage;
    private LinearLayout barcodeLayout;
    private ImageView code1Image;
    private ImageView code2Image;

    public Handler getHandler() {
        return handler;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕保持常亮
        setContentView(R.layout.activity_capture);
        initView();
        init();
    }


    private View.OnClickListener btnClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                case R.id.light_image:

                    camera = CameraManager.getCamera();
                    parameter = camera.getParameters();
                    // TODO 开灯
                    if (isOpen) {
                        ToastUtil.shortToast("关灯了");
                        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameter);
                        isOpen = false;
                    } else {  // 关灯
                        ToastUtil.shortToast("开灯了");
                        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameter);
                        isOpen = true;
                    }
                    break;
                case R.id.code1_image:
                    if(isSelect==1){
                        code1Image.setBackgroundColor(Color.parseColor("#00ffffff"));
                        isSelect=0;
                    }else {
                        code1Image.setBackgroundColor(Color.parseColor("#33ffffff"));
                        code2Image.setBackgroundColor(Color.parseColor("#00ffffff"));
                        isSelect=1;
                    }
                    break;
                case R.id.code2_image:
                    if(isSelect==2){
                        code2Image.setBackgroundColor(Color.parseColor("#00ffffff"));
                        isSelect=0;
                    }else {
                        isSelect=2;
                        code2Image.setBackgroundColor(Color.parseColor("#33ffffff"));
                        code1Image.setBackgroundColor(Color.parseColor("#00ffffff"));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void initView() {
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        lightImage = (ImageView) findViewById(R.id.light_image);
        lightImage.setOnClickListener(btnClick);
        barcodeLayout=(LinearLayout)findViewById(R.id.layout_barcode);
        barcodeLayout.bringToFront();
        findViewById(R.id.main_title).bringToFront();
        code1Image=(ImageView)findViewById(R.id.code1_image);
        code2Image=(ImageView)findViewById(R.id.code2_image);
        code1Image.setOnClickListener(btnClick);
        code2Image.setOnClickListener(btnClick);
        //region公共
//        this.commitBtn = ((Button) findViewById(R.id.commitBtn));
//        this.commitBtn.setOnClickListener(this);
//        this.cancelBtn = ((Button) findViewById(R.id.cancelBtn));
//        this.cancelBtn.setOnClickListener(this);
        this.titiltxt=(TextView)findViewById(R.id.titletxt);
        this.titiltxt.setText("扫码");
        this.backImgBtn=(ImageButton)findViewById(R.id.back);
        this.backImgBtn.setOnClickListener(btnClick);
        this.scanImgBtn=(ImageView)findViewById(R.id.scanImgBtn);
//        this.scanImgBtn.setOnClickListener(this);
        scanImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent it = new Intent(AllotProductTaskListActivity.this, CaptureActivity.class);
//                startActivityForResult(it, SCANNIN_GREQUEST_CODE);
            }
        });
        //endregion
    }

    private void init() {
        this.scanImgBtn.setVisibility(View.INVISIBLE);
        CameraManager.getInsance(getApplication());
        hasSurface=false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView=(SurfaceView)findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder=surfaceView.getHolder();
        if(hasSurface){
            initCamera(surfaceHolder);
        }else {
            surfaceHolder.addCallback(this);
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB){
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }

        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);//AudioManager类提供了访问音量和振铃器mode控制
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            //getRingerMode()返回当前的铃声模式。
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void initCamera(SurfaceHolder surfaceHolder) {
        try{
            CameraManager.get().openDriver(surfaceHolder);
        }catch (IOException ioe){
            return;
        }catch (RuntimeException e){
            return;
        }

        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    public void handleDecode(final Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String mBarcodeFormat=obj.getBarcodeFormat().toString();//返回的就是编码格式
        int size = DecodeFormatManager.ONE_D_FORMATS.size();            //遍历一维码字符集
        int count=0;
        for (int i = 0; i < size; i++) {
            Log.e(" 一维码编码格式 ------>",DecodeFormatManager.ONE_D_FORMATS.get(i)+"");
            //此处根据  拍码后返回的编码格式  与  DecodeFormatManager类中的一维码 编码格式  进行对比
            //相同则将标示字段赋值为1（即一维码）      否者将标示字段赋值为2（即二维码）
            if(DecodeFormatManager.ONE_D_FORMATS.get(i).name().equals(mBarcodeFormat)){
                ToastUtil.shortToast("一维码："+obj.getText());
                count++;
                if(isSelect!=2){
                    Intent intent = new Intent();
                    intent.putExtra("scan_result", obj.getText());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
        if(count==0&&isSelect!=1){
            ToastUtil.shortToast("二维码："+obj.getText());
            Intent intent = new Intent();
            intent.putExtra("scan_result", obj.getText());
            setResult(RESULT_OK, intent);
            finish();
        }else {
            handler.restartPreviewAndDecode();
        }
        /*
         * AlertDialog.Builder dialog = new AlertDialog.Builder(this); if
         * (barcode == null) { dialog.setIcon(null); } else {
         *
         * Drawable drawable = new BitmapDrawable(barcode);
         * dialog.setIcon(drawable); } dialog.setTitle("ɨ����");
         * dialog.setMessage(obj.getText()); dialog.setNegativeButton("ȷ��", new
         * DialogInterface.OnClickListener() {
         *
         * @Override public void onClick(DialogInterface dialog, int which) {
         * //��Ĭ���������ɨ��õ��ĵ�ַ Intent intent = new Intent();
         * intent.setAction("android.intent.action.VIEW"); Uri content_url =
         * Uri.parse(obj.getText()); intent.setData(content_url);
         * startActivity(intent); finish(); } });
         * dialog.setPositiveButton("ȡ��", new DialogInterface.OnClickListener()
         * {
         *
         * @Override public void onClick(DialogInterface dialog, int which) {
         * finish(); } }); dialog.create().show();
         */
    }

    private static final long VIBRATE_DURATION = 200L;
    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

}
