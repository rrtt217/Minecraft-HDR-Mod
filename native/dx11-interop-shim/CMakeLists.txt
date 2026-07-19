cmake_minimum_required(VERSION 3.16)

if(NOT WIN32)
    message(FATAL_ERROR
        "dx11-interop-shim can only be built on Windows (uses D3D11/DXGI).")
endif()

project(dx11-interop-shim LANGUAGES C)

find_package(JNI REQUIRED)

add_library(dx11-interop-shim SHARED
    src/dx11_shim.c
)

target_include_directories(dx11-interop-shim PRIVATE
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

target_link_libraries(dx11-interop-shim PRIVATE
    d3d11
    dxgi
    d3dcompiler
)

if(MSVC)
    # C4201: nameless struct/union — used heavily by Windows SDK headers
    # C4459: parameter names hiding struct member names (in JNI function params)
    target_compile_options(dx11-interop-shim PRIVATE /W4 /WX /wd4201 /wd4459)
else()
    target_compile_options(dx11-interop-shim PRIVATE -Wall -Wextra -Werror)
endif()

set_target_properties(dx11-interop-shim PROPERTIES
    OUTPUT_NAME "dx11-interop-shim"
    PREFIX ""
)
