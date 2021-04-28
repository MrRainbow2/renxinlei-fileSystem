package shell.command.impl;

import block.INode;
import org.apache.commons.lang3.StringUtils;
import os.FileSystem;
import shell.Shell;
import shell.command.AbstractCommandStrategy;

import java.io.IOException;

/**
 * @author renxinlei
 * @Classname CatCommondStrategy
 * @Description TODO
 * @Date 2021/4/27 7:49 下午
 */
public class CatCommandStrategy extends AbstractCommandStrategy {

    public CatCommandStrategy(Shell shell) {
        super(shell);
    }


    @Override
    public boolean checkOption(String[] options) {
        if (options == null) {
            return true;
        }
        if (options.length != 2) {
            System.out.println("syntax error near unexpected token newline'");
            return false;
        }
        if (options[1].startsWith("/")) {
            System.out.println("重定向只支持当前目录");
        }
        return true;
    }

    @Override
    public boolean checkParam(String[] param) {
        return param != null && param.length == 1;
    }

    @Override
    public void invokeCommand(INode parent, String curDir, String[] param, String[] options) throws IOException {
        FileSystem miniOS = getShellOs();
        Shell shell = getShell();
        String path = param[0];
        INode file = miniOS.getINode(path, parent);
        if (file.getFlag() != 2) {
            System.out.println("cat: " + path + "Is a directory");
            return;
        }
        if (file.getOwner() != shell.getUid()) {
            System.out.println("error: permission denied");
            return;
        }
        String fileContent = miniOS.readFile(file);
        if (options == null || options.length == 0) {
            catFile(fileContent);
        } else {
            redirect(parent, curDir, options, fileContent);
        }
    }

    public void redirect(INode parent, String curDir, String[] options, String fileContent) throws IOException {
        Shell shell = getShell();
        FileSystem miniOS = getShellOs();
        miniOS.createFile(parent, shell.getUid(), options[1], fileContent);
    }


    public void catFile(String fileContent) {
        System.out.println(fileContent);
    }

    @Override
    public boolean match(String command) {
        if (StringUtils.isBlank(command)) {
            return false;
        }
        return "cat".equals(command);
    }
}
