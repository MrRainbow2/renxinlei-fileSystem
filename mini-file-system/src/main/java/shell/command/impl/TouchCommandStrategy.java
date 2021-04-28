package shell.command.impl;

import block.INode;
import org.apache.commons.lang3.StringUtils;
import os.FileSystem;
import shell.Shell;
import shell.command.AbstractCommandStrategy;

import java.io.IOException;

/**
 * @author renxinlei
 * @Classname TouchCommandStrategy
 * @Description TODO
 * @Date 2021/4/27 9:54 下午
 */
public class TouchCommandStrategy extends AbstractCommandStrategy {

    public TouchCommandStrategy(Shell shell) {
        super(shell);
    }

    @Override
    public boolean checkParam(String[] param) {
        return param != null && param.length <= 1;
    }

    @Override
    public void invokeCommand(INode parent, String curDir, String[] param, String[] options) throws IOException {
        FileSystem miniOS = getShellOs();
        Shell shell = getShell();
        String fileName = param[0];
        miniOS.createFile(parent, shell.getUid(), fileName, "");
    }

    @Override
    public boolean match(String command) {
        if (StringUtils.isBlank(command)) {
            return false;
        }
        return "touch".equals(command);
    }
}
