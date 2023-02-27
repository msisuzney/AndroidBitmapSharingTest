#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "Cxx"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_msisuzney_ipc_service_DirectBufferInterface_changeColor(JNIEnv *env, jobject obj, jobject buffer) {
    auto *buf = (jbyte *) env->GetDirectBufferAddress(buffer);
    jlong capacity = env->GetDirectBufferCapacity(buffer);
    for (int i = 0; i < capacity; i += 4) {
        buf[i] = 0x00;
        buf[i + 1] = 0x00;
        buf[i + 2] = -1;
        buf[i + 3] = -1;
    }
}