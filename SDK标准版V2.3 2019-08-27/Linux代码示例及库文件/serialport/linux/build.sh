#javah classpath . com.bangmart.nt.channel.serial.SerialPort 生成JNI的头文件

#gcc -fPIC -D_REENTRANT -I/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/include -I/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/include/linux -c SerialPort.c
#gcc -fPIC -D_REENTRANT -I/usr/lib/jvm/java-12-openjdk/include -I/usr/lib/jvm/java-12-openjdk/include/linux -c SerialPort.c
gcc -fPIC -D_REENTRANT -I/usr/lib/jvm/java-12-openjdk/include -I/usr/lib/jvm/java-12-openjdk/include/linux -c SerialPort.c
gcc SerialPort.o -o libbmt_serial_port.so -shared
sudo cp libbmt_serial_port.so /usr/lib
