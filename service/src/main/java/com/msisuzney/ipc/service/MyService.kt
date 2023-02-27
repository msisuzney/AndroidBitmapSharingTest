package com.msisuzney.ipc.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.HardwareBuffer
import android.os.Build
import android.os.IBinder
import android.os.SharedMemory
import android.util.Log
import androidx.annotation.RequiresApi
import com.msisuzney.common.IO
import com.msisuzney.common.IOBitmap
import org.greenrobot.eventbus.EventBus


class MyService : Service() {

    private inner class MyBinder : IO.Stub() {

        override fun changeColor(io: IOBitmap?) {
            io?.apply {
                Log.d("Cxx", "received:${io}")
                val buffer = this.bitmapMemory!!.mapReadWrite()
                DirectBufferInterface().changeColor(buffer)
                SharedMemory.unmap(buffer)
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getHardwareBitmap(): HardwareBuffer {
            val options = BitmapFactory.Options()
            options.inScaled = false
            //4000 * 4000 * 4 =
            options.inSampleSize = 2
            options.inPreferredConfig = Bitmap.Config.HARDWARE
            val bitmap =
                BitmapFactory.decodeResource(
                    this@MyService.resources,
                    R.drawable.test,
                    options
                )
            return bitmap.hardwareBuffer
        }

        override fun sendBitmap(io: Bitmap?) {
            io?.apply {
                io.eraseColor(Color.MAGENTA)
                Log.d("Cxx", "received:isMutable:${isMutable},${this.allocationByteCount}")
                EventBus.getDefault().post(Event().apply { bitmap = io })
            }
        }
    }

    override fun onBind(intent: Intent): IBinder = MyBinder()
}