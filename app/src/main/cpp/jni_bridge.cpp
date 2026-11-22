#include <jni.h>
#include <mutex>
#include "Engine.hpp"

static SystemEngine g_engine;
static std::mutex g_engine_mutex;

extern "C" JNIEXPORT jstring JNICALL
Java_com_cdh_cppandroid_MainActivity_applyAdd(JNIEnv* env, jclass /*clazz*/, jlong delta) {
    std::lock_guard<std::mutex> lock(g_engine_mutex);
    std::string result = g_engine.apply(std::make_unique<AddOperation>(delta));
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_cdh_cppandroid_MainActivity_applyMultiply(JNIEnv* env, jclass /*clazz*/, jlong factor) {
    std::lock_guard<std::mutex> lock(g_engine_mutex);
    std::string result = g_engine.apply(std::make_unique<MultiplyOperation>(factor));
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_cdh_cppandroid_MainActivity_resetEngine(JNIEnv* env, jclass /*clazz*/) {
    std::lock_guard<std::mutex> lock(g_engine_mutex);
    std::string result = g_engine.reset();
    return env->NewStringUTF(result.c_str());
}
