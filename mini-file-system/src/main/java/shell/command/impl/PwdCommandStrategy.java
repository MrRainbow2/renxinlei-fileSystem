package shell.command.impl;

import block.INode;
import org.apache.commons.lang3.StringUtils;
import os.FileSystem;
import shell.Shell;
import shell.command.AbstractCommandStrategy;

import java.io.IOException;

/**
 * @author renxinlei
 * @Classname PwdCommandStrategy
 * @Description TODO
 * @Date 2021/4/28 12:42 上午
 */
public class PwdCommandStrategy extends AbstractCommandStrategy {
    public PwdCommandStrategy(Shell shell) {
        super(shell);
    }

    @Override
    public boolean checkParam(String[] param) {
        if (param != null && param.length > 0) {
            return false;
        }
        return true;
    }

    @Override
    public void invokeCommand(INode parent, String curDir, String[] param, String[] options) throws IOException {
        FileSystem miniOS = getShellOs();
        String path = miniOS.getAbsolutePath(parent, "");
        if (options == null || options.length == 0) {
            System.out.println(path);
        } else {
            redirect(parent, curDir, options, path);
        }
    }

    public void redirect(INode parent, String curDir, String[] options, String fileContent) throws IOException {
        Shell shell = getShell();
        FileSystem miniOS = getShellOs();
        miniOS.createFile(parent, shell.getUid(), options[1], fileContent);
    }

    @Override
    public boolean match(String command) {
        if (StringUtils.isBlank(command)) {
            return false;
        }
        return "pwd".equals(command);
    }
}
