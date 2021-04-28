# filesystem

一个模拟文件系统的程序

## 程序描述

创建一个DISK文件，用于模拟磁盘，并在此磁盘上实现一个简单的文件系统，并提供一个shell用于用户交互。

disk.Disk：提供读写文件的接口，设定磁盘的规格，默认为1024行，即有1024个block，每个block可以写入64个字符

os.FileSystem：提供文件系统的接口，实现此文件系统的功能

shell.Shell	：提供用户与文件系统交互的功能

shell.command.CommandStrategy : 提供了基本指令支持。 时间指令自动加载。



- 在格式化的DISK文件上初始化文件系统
	<img src="pic/initial_smokeOS.png" width=500 height=300 alt="disk layout" />
	
	- 第1行存储superblock，记录磁盘block总数，inode总数，inode已使用数，数据块block已使用数，数据块block开始位置，空闲数据块block位置
	- 第2行到第51行存储inode，每个block可以存储4个inode，即每个inode有16个字符
		
		- inode 第1到3个字符分别存储inode的id，flag（0为空inode，1为目录，2为文件），onwer id，剩余用来存储文件的block块号

	- 剩余都是存储数据的block
	
	
- 初始化文件系统后运行Shell，用户进行操作

	- 登录，认证用户身份

	- 提供命令行

## 待改进点

- 文件写入时获取空闲 dataBlock, iNode的方式，当前采用遍历方式，拿到对应偏移的块后，会判断是否占用。改进方式：在superBlock中维护 bitMap,用于存储对用块的使用情况
- 当前对于文件的写入，读取采用循环读文件的方式。在文件系统操作时响应时间较长。 改进方式： 采用异步吸入方式，引入缓存区。
- 当前在写入文件时没有根据inode
- 文件编辑器