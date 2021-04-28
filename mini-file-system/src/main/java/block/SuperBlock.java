package block;


/**
 * @author erjie
 * 单例
 */
public class SuperBlock {
    /**
     * block总数
     */
    private static int blocksNum;
    /**
     * inode总数
     */
    private static int iNodeNum;
    /**
     * inode已使用数
     */
    private static int iNodeUsed;
    /**
     * 数据块block已使用数
     */
    private static int fileBlocksUsed;
    /**
     * 数据块block开始位置
     */
    private static int fileBegan;
    /**
     * 空闲数据块block位置
     */
    private static int freeFile;

    private static final SuperBlock SUPER_BLOCK = new SuperBlock();

    private SuperBlock() {
    }

    public void setSuperBlock(String line) {
        String[] pros = line.split(" ");
        int[] intPros = new int[pros.length];
        for (int i = 0; i < pros.length; i++) {
            intPros[i] = Integer.parseInt(pros[i]);
        }

        setSuperBlock(intPros);
    }


    public void setSuperBlock(int[] properties) {
        blocksNum = properties[0];
        iNodeNum = properties[1];
        iNodeUsed = properties[2];
        fileBlocksUsed = properties[3];
        fileBegan = properties[4];
        freeFile = properties[5];
    }

    public static SuperBlock getSuperBlock() {
        return SUPER_BLOCK;
    }

    public static String getLine() {
        /**
         * 类似toString，方便写入disk
         */
        StringBuilder superBlockLine = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            switch (i) {
                case 0:
                    superBlockLine.append(blocksNum).append(" ");
                    break;
                case 1:
                    superBlockLine.append(iNodeNum).append(" ");
                    break;
                case 2:
                    superBlockLine.append(iNodeUsed).append(" ");
                    break;
                case 3:
                    superBlockLine.append(fileBlocksUsed).append(" ");
                    break;
                case 4:
                    superBlockLine.append(fileBegan).append(" ");
                    break;
                case 5:
                    superBlockLine.append(freeFile).append(" ");
                    break;

                default:
                    superBlockLine.append("0 ");
                    break;
            }
        }

        return superBlockLine.toString();
    }

    public int getBlocksNum() {
        return blocksNum;
    }

    public void setBlocksNum(int blocksNum) {
        SuperBlock.blocksNum = blocksNum;
    }

    public int getiNodeNum() {
        return iNodeNum;
    }

    public void setiNodeNum(int iNodeNum) {
        SuperBlock.iNodeNum = iNodeNum;
    }

    public int getiNodeUsed() {
        return iNodeUsed;
    }

    public void setiNodeUsed(int iNodeUsed) {
        SuperBlock.iNodeUsed = iNodeUsed;
    }

    public int getFileBlocksUsed() {
        return fileBlocksUsed;
    }

    public void setFileBlocksUsed(int fileBlocksUsed) {
        SuperBlock.fileBlocksUsed = fileBlocksUsed;
    }

    public int getFileBegan() {
        return fileBegan;
    }

    public void setFileBegan(int fileBegan) {
        SuperBlock.fileBegan = fileBegan;
    }

    public int getFreeFile() {
        return freeFile;
    }

    public void setFreeFile(int freeFile) {
        SuperBlock.freeFile = freeFile;
    }


}