package shell.command.impl;

import block.INode;
import org.apache.commons.lang3.StringUtils;
import os.FileSystem;
import shell.Shell;
import shell.command.AbstractCommandStrategy;

import java.io.IOException;

/**
 * @author renxinlei
 * @Classname LsCommandStrategy
 * @Description TODO
 * @Date 2021/4/27 12:47 上午
 */
public class LsCommandStrategy extends AbstractCommandStrategy {
    public LsCommandStrategy(Shell shell) {
        super(shell);
    }

    @Override
    public boolean checkParam(String[] param) {
        if (param == null) {
            return true;
        }
        return param.length <= 1;
    }

    @Override
    public void invokeCommand(INode parent, String curDir, String[] param, String[] options) throws IOException {
        FileSystem miniOS = getShellOs();
        INode dirNode = null;
        String path = "";
        if (param == null) {
            dirNode = parent;
        } else {
            path = param[0];
            dirNode = miniOS.getINode(path, parent);
        }
        if (dirNode == null) {
            System.out.println("ls: " + path + ": No such file or directory");
            return;
        }
        String[] dirC = miniOS.getDirChilds(dirNode);
        StringBuilder fileContent = new StringBuilder();
        for (int i = 0; i < dirC.length; i += 2) {
            fileContent.append(" ").append(dirC[i + 1]).append(" ");
        }

        if (options != null) {

        } else {
            System.out.println(fileContent);
        }

    }

    @Override
    public boolean match(String command) {

        if (StringUtils.isBlank(command)) {
            return false;
        }
        return "ls".equals(command);
    }
}
