cmake_minimum_required(VERSION 3.22.1)
project("native_cpp")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(native_cpp SHARED
    jni_bridge.cpp
    ../../../../cpp_engine/Engine.cpp
)

target_include_directories(native_cpp PRIVATE ../../../../cpp_engine)
target_link_libraries(native_cpp log)
