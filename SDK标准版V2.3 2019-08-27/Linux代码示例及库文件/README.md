serialport:		For compile libbmt_serial_port.so
bos_nt_sdk.jar:	Bangmart sdk which depend on libbmt_serial_port.so
Tester.java:	Demo in bos_nt_sdk.jar

1.Run jar for test
/**replace "/dev/ttyAMA0" with your serial port*/
java -jar bos_nt_sdk.jar /dev/ttyAMA0 

(1).Q:Missing read/write permission, trying to chmod the file
  A:Check if the serial port is correct