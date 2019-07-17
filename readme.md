# 9102年java里的xxe

9102年了，尝试了一下使用 XXE 攻击 java 程序，高版本会失败，总结了一下 jvm 里的变化。

> 配合 http://www.leadroyal.cn/?p=914 使用更佳

### 有回显的情况，测试 Java XXE 对各种协议的支持情况

- LocalEntityDemo.java
- local.txt
- line.txt

运行可以看到file协议被完全执行一遍，并且有回显。

### 使用 http oob 读单行文件

- ExternalEntityDemo1
- server/LocalHttpServer
- d1_step1.xml
- d1_step2.dtd

运行可以看到单行文件内容通过 http 协议传给了http 服务器。

### 使用 ftp oob 读多行文件（高版本会失败）

- 【大于等于7u141】【大于等于8u162】会执行失败
- ExternalEntityDemo2
- server/LocalFtpServer
- d2_step1.xml
- d2_step2.dtd

运行可以看到多行文件内容通过 ftp 协议传给了ftp 服务器。

### 其他

- bad_char1.txt 文件中第一类特殊字符
- bad_char2.txt 文件中第二类特殊字符
- test.pri 随手产生的一个私钥，稍微复杂一点，使用 ftp-oob 读多行也可以正常读取到

