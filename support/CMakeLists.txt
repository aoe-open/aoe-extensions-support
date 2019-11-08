cmake_minimum_required(VERSION 3.4.1)

#include头文件目录
include_directories(src/main/cpp/yuv/include/
        src/main/cpp/aoe_support/include/)

file(GLOB YUV_SRC src/main/cpp/yuv/source/*.cc)
set(YUV_COMPILE_CODE ${YUV_SRC})

#source directory源文件目录
file(GLOB AOE_SUPPORT_SRC src/main/cpp/aoe_support/*.cpp)
set(AOE_SUPPORT_COMPILE_CODE ${AOE_SUPPORT_SRC})

#添加libyuv库
add_library(libyuv STATIC ${YUV_COMPILE_CODE})

#编译为动态库
add_library(aoe_support SHARED ${AOE_SUPPORT_COMPILE_CODE})

#添加工程所依赖的库
find_library(log-lib log)
target_link_libraries(
        aoe_support
        libyuv
        ${log-lib})