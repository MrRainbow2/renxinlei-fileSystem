package shell.command.impl;

import block.INode;
import org.apache.commons.lang3.StringUtils;
import os.FileSystem;
import shell.Shell;
import shell.command.AbstractCommandStrategy;

import java.io.IOException;

/**
 * @author renxinlei
 * @Classname MkdirCommandStrategy
 * @Description TODO
 * @Date 2021/4/26 11:31 下午
 */
public class MkdirCommandStrategy extends AbstractCommandStrategy {

    public MkdirCommandStrategy(Shell shell) {
        super(shell);
    }

    @Override
    public boolean checkParam(String[] param) {
        return param != null && param.length >= 1;
    }

    @Override
    public void invokeCommand(INode parent, String curDir, String[] param, String[] options) throws IOException {
        FileSystem miniOs = getShellOs();
        Shell shell = getShell();
        for (String s : param) {
            miniOs.mkdir(parent, shell.getUid(), s);
        }
    }

    @Override
    public boolean match(String command) {
        if (StringUtils.isBlank(command)) {
            return false;
        }
        return "mkdir".equals(command);
    }
}
