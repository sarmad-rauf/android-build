#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_es_marocapp_usecase_splash_SplashActivity_getSecureKeyValues(
        JNIEnv *env,
        jobject /* this */) {
    std::string key = "$7*93)(@$#$><D:}[.,)(><2@§das";
    return env->NewStringUTF(key.c_str());
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_es_marocapp_usecase_splash_SplashActivity_getServerPublicKeys(
        JNIEnv *env,
        jobject /* this */) {

    // Server Public Keys
    std::string key1 = "EbmVRuVFiv5FDIvVCnjqSiH+DLLIpku9/I024/54Oms=";
    std::string key2 = "EbmVRuVFiv5FDIvVCnjqSiH+DLLIpku9/I024/54Oms=";

    // A Separator is used for separation for the keys
    std::string separator = ":::::::"; //7 chars

    // Combine keys to send in one string to Kotlin code
    std::string combine_keys = key1 + separator + key2 ;

    return env->NewStringUTF(combine_keys.c_str());

}

extern "C" JNIEXPORT jint

JNICALL
Java_com_jazz_jazzworld_usecase_splash_SplashActivity_AddValues(JNIEnv *env, jobject /* this */,
                                                                jint firstValue, jint secondValue) {
    return (firstValue + secondValue);
}


extern "C" JNIEXPORT jstring

JNICALL
Java_com_es_marocapp_usecase_splash_SplashActivity_getAesGcmHexKey(
        JNIEnv *env,
        jobject /* this */) {
    std::string key = "2192B39425BBD08B6E8E61C5D1F1BC9F428FC569FBC6F78C0BC48FCCDB0F42AE";
    return env->NewStringUTF(key.c_str());
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_es_marocapp_usecase_splash_SplashActivity_getAesGcmHexIV(
        JNIEnv *env,
        jobject /* this */) {
    std::string key = "E1E592E87225847C11D948684F3B070D";
    return env->NewStringUTF(key.c_str());
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_es_marocapp_usecase_splash_SplashActivity_getAesCBCHexKey(
        JNIEnv *env,
        jobject /* this */) {
    std::string key = "04BC61DC6F1A98B3C86AC22D67ED3F1A";

    //old key
    //std::string key = "B67C1EA886E95E689A1BB3DBAD065C16";
    return env->NewStringUTF(key.c_str());
}

extern "C" JNIEXPORT jstring

JNICALL
Java_com_es_marocapp_usecase_splash_SplashActivity_getAesCBCHexIV(
        JNIEnv *env,
        jobject /* this */) {
    std::string key = "74B3A7A3FF0D3C3E6EBB485AE04E43BD";

    //old IV
    //std::string key = "48E53E0639A76C5A5E0C5BC9E3A91538";
    return env->NewStringUTF(key.c_str());
}