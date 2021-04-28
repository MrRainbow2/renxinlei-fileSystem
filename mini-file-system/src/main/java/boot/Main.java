package boot;

import os.FileSystem;
import shell.Shell;

import java.io.IOException;


/**
 * @author erjie
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        /*
         * 	加载文件系统
         */
        FileSystem fileSystem = new FileSystem();

        /*
         * 	加载DISK文件
         */
        int errCode = fileSystem.loadDisk();
        switch (errCode) {
            case 0:
                System.out.println("load disk finish ");
                break;
            case 1:
                System.out.println("Not found DISK file!");
                System.out.println("Create a new DISK file...");
                System.out.println("Formating success!");
                fileSystem.formatDisk();
            default:
                break;
        }

        /*
         * 	加载Shell
         */
        Shell shell = new Shell(fileSystem);

//		运行shell
        shell.run();
    }

}
