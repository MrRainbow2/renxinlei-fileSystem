package shell.command;

import os.FileSystem;
import shell.Shell;

/**
 * @author renxinlei
 * @Classname AbstractCommandStrategy
 * @Description TODO
 * @Date 2021/4/26 11:39 下午
 */
public abstract class AbstractCommandStrategy implements CommandStrategy {

    private final Shell shell;

    public AbstractCommandStrategy(Shell shell) {
        this.shell = shell;
    }

    @Override
    public boolean checkOption(String[] options) {
        return true;
    }

    public Shell getShell() {
        return shell;
    }

    public FileSystem getShellOs() {
        return shell.getMiniOS();
    }
}
