// IO.aidl
package com.msisuzney.common;
import android.hardware.HardwareBuffer;
import android.graphics.Bitmap;
parcelable IOBitmap;

interface IO {

  void changeColor(inout IOBitmap io);

  HardwareBuffer getHardwareBitmap();

  void sendBitmap(in Bitmap io);

}