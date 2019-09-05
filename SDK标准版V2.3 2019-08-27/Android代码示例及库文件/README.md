1.BmtVendingMachineDemo：邦马特自贩机demo 
2.BmtVendingMachineDemo_V2_3.apk:BmtVendingMachineDemo编译后的apk
3.libs:已实现Android与图像处理板（IPS）通信协议相关Jar包和so库
	libs\armeabi\libbmt_serial_port.so     //串口相关so库
	libs\armeabi-v7a\libbmt_serial_port.so //串口相关so库
	libs\serialbmt.jar              //串口相关jar包
	libs\bangmart_nt_2_3.jar        //基于Bangmart自贩机协议相关实现的jar包
	

集成邦马特机型流程:
直接复制libs相关文件,集成到新项目中

#混淆配置
#start: bangmart
# 保留串口相关类不混淆
-keep class com.bangmart.sdk.serialbmt.** {*;}
-keep class com.bangmart.nt.** {*;}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#end: bangmart
