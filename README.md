# 嵌入 命令行工具 到 Android 软件 APK 包内

Android 开发中, 在 apk 软件包里嵌入原生命令行工具. 然后在主界面上点击按钮执行.

要点: 
- 告诉 CMake 直接将可执行文件编译到 assets 文件夹里, AS 打包时直接将其作为资源包含进软件里.
- 执行这个命令时, 将这个资源解包到 SD 卡上, 并赋予可执行属性, 就可以运行了.
