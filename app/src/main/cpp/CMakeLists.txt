cmake_minimum_required(VERSION 3.4.1)

project(execNative)

find_library( log-lib log)

add_definitions(-D__WAIT_DEBUGGER_ATTACH__)

add_executable( mycommand mycommand.c )
# https://cloud.tencent.com/developer/ask/93388
set_target_properties( mycommand PROPERTIES PREFIX "lib" SUFFIX ".so" )

add_library( mycommand2 SHARED mycommand.c )
