/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kingl.zxs.klapplication.ZXing.decoding;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.kingl.zxs.klapplication.Activity.CaptureActivity;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 * �����߳�
 */
final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";

  private final CaptureActivity activity;

  // 可以解析的编码类型
  private final Hashtable<DecodeHintType, Object> hints;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;
  //是一种java.util.concurrent包下一个同步工具类，它允许一个或多个线程等待直到在其他线程中一组操作执行完成。

  DecodeThread(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats,
               String characterSet, ResultPointCallback resultPointCallback) {

    this.activity = activity;
    handlerInitLatch = new CountDownLatch(1);

    hints = new Hashtable<DecodeHintType, Object>(3);

    // // The prefs can't change while the thread is running, so pick them
    // up once here.
    // if (decodeFormats == null || decodeFormats.isEmpty()) {
    // SharedPreferences prefs =
    // PreferenceManager.getDefaultSharedPreferences(activity);
    // decodeFormats = new Vector<BarcodeFormat>();
    // if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D, true)) {
    // decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
    // }
    // if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, true)) {
    // decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
    // }
    // if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX,
    // true)) {
    // decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
    // }
    // }
    if (decodeFormats == null || decodeFormats.isEmpty()) {
      decodeFormats = new Vector<BarcodeFormat>();
      decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
      decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
      decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);

    }

    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
    }

    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK,
            resultPointCallback);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    //Looper类，是用来封装消息循环和消息队列的一个类，用于在android线程中进行消息处理
    handler = new DecodeHandler(activity, hints);
    handlerInitLatch.countDown();//可以在多个线程中调用  计算调用次数是所有线程调用次数的总和
    Looper.loop();
    //让Looper开始工作，从消息队列里取消息，处理消息
  }

}
