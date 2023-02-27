package com.msisuzney.ipc.service

import java.nio.ByteBuffer

class DirectBufferInterface {

    external fun changeColor(buffer: ByteBuffer): Unit

    companion object {
        // Used to load the 'bytebuffer' library on application startup.
        init {
            System.loadLibrary("bytebuffer")
        }
    }
}