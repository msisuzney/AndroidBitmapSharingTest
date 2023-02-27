package com.msisuzney.ipc.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.*
import android.os.*
import android.system.OsConstants
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.msisuzney.common.IO
import com.msisuzney.common.IOBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var io: IO? = null
    private var bitmap: Bitmap? = null
    private var bitmapWidth = 5000
    private var bitmapHeight = 5000
    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.image)
        Intent().apply {
            setClassName(
                "com.msisuzney.ipc.service",
                "com.msisuzney.ipc.service.MyService"
            )
            this@MainActivity.bindService(this, object : ServiceConnection {
                override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                    io = IO.Stub.asInterface(p1)
                }

                override fun onServiceDisconnected(p0: ComponentName?) {
                }
            }, BIND_AUTO_CREATE)
        }

    }


    private fun initBitmap() {
        val rect = Rect(0, 0, bitmapWidth, bitmapHeight)
        bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)
        bitmap!!.eraseColor(Color.RED)
    }

    fun change(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            io?.apply {
                val bitmap = this@MainActivity.bitmap!!
                Log.d("Cxx", "bitmap size:${bitmap.allocationByteCount * 1.0 / 1024 / 1024}")
                val sharedMemory = SharedMemory.create("bitmap_memory", bitmap.allocationByteCount)
                val buffer = sharedMemory.mapReadWrite()
                bitmap.copyPixelsToBuffer(buffer)
                val ioBitmap = IOBitmap(bitmapWidth, bitmapHeight, sharedMemory)
                Log.d("Cxx", "before ipc:$ioBitmap")
                SharedMemory.unmap(buffer)
                changeColor(ioBitmap)
                Log.d("Cxx", "after ipc:$ioBitmap")
                val changedBuffer = sharedMemory.mapReadWrite()
                bitmap.copyPixelsFromBuffer(changedBuffer)
                SharedMemory.unmap(changedBuffer)
                withContext(Dispatchers.Main) {
                    this@MainActivity.bitmap = bitmap
                    imageView!!.setImageBitmap(this@MainActivity.bitmap)
                }
            }
        }
    }

    fun init(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            initBitmap()
            withContext(Dispatchers.Main) {
                imageView?.setImageBitmap(bitmap)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun get(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            io?.apply {
                val bitmap = Bitmap.wrapHardwareBuffer(
                    this.hardwareBitmap,
                    ColorSpace.get(ColorSpace.Named.SRGB)
                )
                withContext(Dispatchers.Main) {
                    imageView?.setImageBitmap(bitmap)

                }
            }
        }
    }

    fun send(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            io?.apply {
                val bitmap = this@MainActivity.bitmap!!
                this.sendBitmap(bitmap)
                Log.d("Cxx", "before ipc:$bitmap")
                withContext(Dispatchers.Main) {
                    imageView?.setImageBitmap(bitmap)
                    Log.d("Cxx", "after ipc:$bitmap")
                }
            }
        }
    }
}
