1.Compile libbmt_serial_port.so and copy to /usr/lib
sh build.sh
Q:fatal error: jni.h: No such file or director
A:
	(1).check whether java jdk installed:java --version
	no- install java jdk at first.

	Reference:
	yum install dnf
	sudo dnf install java-latest-openjdk java-latest-openjdk-devel
	https://computingforgeeks.com/how-to-install-java-12-on-centos-fedora/
	
	(2).check jdk path in build.sh 
	replace "/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/include" with your java jdk path 

