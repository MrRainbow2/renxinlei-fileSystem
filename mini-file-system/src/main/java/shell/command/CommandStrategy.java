package shell.command;

import block.INode;

import java.io.IOException;

/**
 * @author renxinlei
 * @Classname CommandStrategy
 * @Description TODO
 * @Date 2021/4/26 11:00 下午
 */
public interface CommandStrategy {
    /**
     * 检查指令参数
     *
     * @param param
     * @return
     */
    boolean checkParam(String[] param);

    /**
     * 检查指令设置
     *
     * @param options
     * @return
     */
    boolean checkOption(String[] options);

    /**
     * 执行指令
     *
     * @param parent  当前目录Inode
     * @param curDir  当前文件夹名称
     * @param param
     * @param options
     */
    void invokeCommand(INode parent, String curDir, String[] param, String[] options) throws IOException;

    /**
     * 匹配指令执行
     *
     * @param command
     * @return
     */
    boolean match(String command);
}
