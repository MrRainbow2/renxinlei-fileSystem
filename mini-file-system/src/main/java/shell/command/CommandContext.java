package shell.command;

import block.INode;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import shell.Shell;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author renxinlei
 * @Classname CommondContext
 * @Description TODO
 * @Date 2021/4/26 10:58 下午
 */
public class CommandContext {
    List<CommandStrategy> commandStrategies = Lists.newArrayList();

    public CommandContext(Shell shell) {
        try {
            Reflections reflections = new Reflections();
            Set<Class<? extends AbstractCommandStrategy>> monitorClasses = reflections.getSubTypesOf(AbstractCommandStrategy.class);
            for (Class<? extends AbstractCommandStrategy> monitor : monitorClasses) {
                Constructor<?> cons = monitor.getDeclaredConstructor(Shell.class);
                commandStrategies.add((CommandStrategy) cons.newInstance(shell));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CommandStrategy chooseStrategy(String command) {
        if (StringUtils.isBlank(command)) {
            return null;
        }
        for (CommandStrategy commandStrategy : commandStrategies) {
            if (commandStrategy.match(command)) {
                return commandStrategy;
            }
        }
        return null;
    }

    public void invokeCommand(INode parent, String curDir, String input) throws IOException {
        /*
         * 	处理命令
         */
        String[] inputArr = input.split(" ");
        String command = inputArr[0];
        String[] param = null;
        String[] option = null;
        int optionIndex = getOptionIndex(inputArr);
        if (inputArr.length > 1) {
            if (optionIndex == 0) {
                param = Arrays.copyOfRange(inputArr, 1, inputArr.length);
            } else {
                param = Arrays.copyOfRange(inputArr, 1, optionIndex);
                option = Arrays.copyOfRange(inputArr, optionIndex, inputArr.length);
            }
        }

        invokeCommand(parent, curDir, command, param, option);
    }

    public int getOptionIndex(String[] inputArr) {
        for (int index = 0; index < inputArr.length; index++) {
            if (">>".equals(inputArr[index])) {
                return index;
            }
        }
        return 0;
    }

    public void invokeCommand(INode parent, String curDir, String command, String[] param, String[] options) throws IOException {
        CommandStrategy commandStrategy = chooseStrategy(command);
        if (commandStrategy == null) {
            System.out.println("error: Command " + command + " not fund.");
            return;
        }
        if (!commandStrategy.checkParam(param)) {
            System.out.println("error: Illegal param");
            return;
        }
        if (!commandStrategy.checkOption(options)) {
            return;
        }
        commandStrategy.invokeCommand(parent, curDir, param, options);
    }
}
