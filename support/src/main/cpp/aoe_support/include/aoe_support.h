//
// Created by noctis on 18/7/26.
//

#ifndef AOE_VISION_H
#define AOE_VISION_H

#include <string>
#include <jni.h>

#define TAG "AoeSupport"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)

#define AOE_SUPPORT_JNI_METHOD2(METHOD_NAME) \
  Java_com_didi_aoe_extensions_support_image_AoeSupport_##METHOD_NAME

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jbyteArray JNICALL
AOE_SUPPORT_JNI_METHOD2(convertNV21ToARGB8888)(JNIEnv *env, jclass instance, jbyteArray nv21Src,
                                               jint srcWidth, jint srcHeight);

JNIEXPORT jbyteArray JNICALL
AOE_SUPPORT_JNI_METHOD2(cropABGR)(JNIEnv *env, jobject instance, jbyteArray abgrSrc,
                                  jint srcWidth, jint srcHeight,
                                  jint cropX, jint cropY, jint aCropWidth, jint aCropHeight);

JNIEXPORT jbyteArray JNICALL
AOE_SUPPORT_JNI_METHOD2(rotateARGB)(JNIEnv *env, jclass instance, jbyteArray nv21Src,
                                    jint srcWidth, jint srcHeight, jint degree);

JNIEXPORT jbyteArray JNICALL
AOE_SUPPORT_JNI_METHOD2(scaleBGRA)(JNIEnv *env, jclass instance, jbyteArray bgraSrc, jint srcWidth,
                                    jint srcHeight, jint dstWidth, jint dstHeight, jint filterMode);

#ifdef __cplusplus
}
#endif


#endif //AOE_VISION_H
