package shell.command.impl;

import block.INode;
import org.apache.commons.lang3.StringUtils;
import os.FileSystem;
import shell.Shell;
import shell.command.AbstractCommandStrategy;

import java.io.IOException;

/**
 * @author renxinlei
 * @Classname CdCommandStrategy
 * @Description TODO
 * @Date 2021/4/27 12:56 上午
 */
public class CdCommandStrategy extends AbstractCommandStrategy {

    public CdCommandStrategy(Shell shell) {
        super(shell);
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
        INode curInode = miniOS.getINode(path,parent);
        if (curInode == null) {
            return;
        }
        String curDirName = miniOS.getINodeName(curInode.getId());
        shell.setParent(curInode);
        shell.setCurDir(curDirName);
        getShell().setPrompt();
    }

    @Override
    public boolean match(String command) {
        if (StringUtils.isBlank(command)) {
            return false;
        }
        return "cd".equals(command);
    }
}
