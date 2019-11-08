//
// Created by Noctis on 2019/3/15.
//
#include <android/bitmap.h>
#include <android/log.h>
#include <jni.h>
#include <string>
#include <vector>
#include <libyuv.h>
#include "aoe_support.h"

#define posit(x) std::max(std::min(x, 1.0f), 0.0f)

using namespace std;

using namespace libyuv;

// JNI -------------------------

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jbyteArray JNICALL
AOE_SUPPORT_JNI_METHOD2(convertNV21ToARGB8888)(JNIEnv *env, jclass instance, jbyteArray nv21Src,
                                              jint srcWidth, jint srcHeight) {
    jbyte *nv21SrcData = env->GetByteArrayElements(nv21Src, NULL);
    int responseDimens = (srcWidth * srcHeight) << 2;

    // 用这个方法转换以后，在内存里是ARGB
    uint8_t *abgrData = new uint8_t[responseDimens];
    libyuv::NV21ToABGR((const uint8_t *) nv21SrcData, srcWidth,
                       (const uint8_t *) nv21SrcData + srcWidth * srcHeight,
                       srcWidth, abgrData, srcWidth << 2, srcWidth, srcHeight);

    jbyteArray response = env->NewByteArray(responseDimens);
    env->SetByteArrayRegion(response, 0, responseDimens, (jbyte *) abgrData);

    env->ReleaseByteArrayElements(nv21Src, nv21SrcData, 0);
    delete[] abgrData;

    return response;
}

JNIEXPORT jbyteArray JNICALL
AOE_SUPPORT_JNI_METHOD2(cropABGR)(JNIEnv *env, jobject instance, jbyteArray abgrSrc,
                                 jint srcWidth, jint srcHeight,
                                 jint cropX, jint cropY, jint aCropWidth, jint aCropHeight) {
    if (cropX < 0) {
        cropX = 0;
    }

    if (cropY < 0) {
        cropY = 0;
    }

    int cropWidth = aCropWidth;
    int cropHeight = aCropHeight;
    if (cropX + cropWidth > srcWidth) {
        cropWidth = srcWidth - cropX;
    }

    if (cropY + cropHeight > srcHeight) {
        cropHeight = srcHeight - cropY;
    }

    const int channelNum = 4;
    const int responseDimens = (cropWidth * cropHeight * channelNum);
    uint8_t *clipResult = new uint8_t[responseDimens];
    memset(clipResult, 0, responseDimens);

    jbyte *abgrSrcData = env->GetByteArrayElements(abgrSrc, NULL);

    uint8_t *source = (uint8_t *)abgrSrcData + (cropY * srcWidth + cropX) * channelNum;
    uint8_t *dst =  clipResult;
    for(int i = 0; i < cropHeight; i++){
        memcpy(dst, source, cropWidth * channelNum);
        source += srcWidth * channelNum;
        dst+= cropWidth * channelNum;
    }

    jbyteArray response = env->NewByteArray(responseDimens);
    env->SetByteArrayRegion(response, 0, responseDimens, (jbyte *) clipResult);
    env->ReleaseByteArrayElements(abgrSrc, abgrSrcData, 0);

    delete[] clipResult;
    return response;
}

#ifdef __cplusplus
}
#endif
