package shell;

import block.INode;
import os.FileSystem;
import shell.command.CommandContext;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;


/**
 * @author erjie
 */
public class Shell {

    private final FileSystem miniOS;
    private final CommandContext commandContext;
    private String prompt;        //提示符
    private String user;        //用户名
    private int uid;            //用户id
    private String curDir;        //当前目录
    private INode parent;        //当前目录节点


    public Shell(FileSystem OS) {
        miniOS = OS;
        commandContext = new CommandContext(this);
    }

    public void run() throws IOException, InterruptedException {
        /*
         *
         */
//		Shell信息
        System.out.println("\n\nMiniOS 0.1.0 ");
        System.out.println("");

//		登陆
        Scanner sc = new Scanner(System.in);
        login(sc);
//			定位到家目录
        parent = miniOS.getINode("/home/" + user);
        curDir = miniOS.getINodeName(parent.getId());
        System.out.print("\nCurrent login: " + new Date().toString());
        System.out.println("\nWelcome to MiniOS 0.1.0");
        System.out.println("\n");
        setPrompt();

//		交互
        String command;
        while (true) {
            System.out.print(prompt);
            command = sc.nextLine().trim();
            if ("shutdown".equals(command)) {
                break;
            }
            doCommand(command);
        }
        sc.close();
    }

    public void login(Scanner sc) throws IOException {
        /*
         * 	登陆认证
         */
        while (true) {
            System.out.print("maple-story-machine login:");
            user = sc.nextLine();
            System.out.print("Password:");
            String passwd = sc.nextLine();

//			身份验证
            uid = userAuthVerify(user, passwd);
            if (uid != -1) {
                break;
            } else {
                System.out.println("Login incorrect");
            }
        }

    }

    public int userAuthVerify(String user, String passwd) throws IOException {
        /*
         * 	认证用户名和密码
         */
//		获取user配置文件
        String userProfile = miniOS.readFile(miniOS.getINode("/etc/user"));
        String[] userList = userProfile.trim().split(" ");
        String[] userfile;
        int id;
        for (int i = 0; i < userList.length; i++) {
            userfile = userList[i].split(":");
            if (userfile[1].equals(user)) {
                if (userfile[2].equals(passwd)) {
                    id = Integer.parseInt(userfile[0]);
                    return id;
                }
            }
        }
        return -1;
    }

    public void setPrompt() {
        /*
         * 	更新提示符
         */
        String proChar;
        if (user.equals("root")) {
            proChar = "#";
        } else {
            proChar = "$";
        }
        prompt = (user + "@" + "maple-story-machine:" + curDir + proChar + " ");
    }

    public void doCommand(String com) throws IOException {
        commandContext.invokeCommand(parent, curDir, com);
    }

    public FileSystem getMiniOS() {
        return miniOS;
    }

    public int getUid() {
        return uid;
    }

    public void setCurDir(String curDir) {
        this.curDir = curDir;
    }

    public void setParent(INode parent) {
        this.parent = parent;
    }
}
