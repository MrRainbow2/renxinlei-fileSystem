package os;


import block.INode;
import block.INodeBlock;
import block.SuperBlock;
import disk.Disk;

import java.io.IOException;

import static constant.FileSystemConst.EMPTY_BLOCK;
import static constant.FileSystemConst.INODE_SIZE;

public class FileSystem {
    /**
     * 第0行为superblock
     * 第1到50行为200个inode,即50个inodeblock
     * 第51行到最后为数据块block
     */

    public static int getInodeSize() {
        return INODE_SIZE;
    }

    private final Disk diskObj;
    private final SuperBlock superBlock;
    private final INodeBlock[] iNodeBlocks;
    private INode root;

    public FileSystem() {
        /*
         * 	构造函数
         */
        superBlock = SuperBlock.getSuperBlock();

        diskObj = new Disk();

        iNodeBlocks = new INodeBlock[50];


    }

    /**
     * 加载磁盘文件
     */
    public int loadDisk() throws IOException {

        int errCode = diskObj.loadDisk(superBlock);

//		若加载成功，则读取所有iNodeBlock
        if (errCode == 0) {
            System.out.println("开始加载superBlock。。。。。。");
            superBlock.setSuperBlock(diskObj.Read(0));
            System.out.println("superBlock加载完成。。。。。。");
            System.out.println("开始加载inodeBlock。。。。。。");
            long startTime = System.currentTimeMillis();
            for (int i = 1; i < 51; i++) {
                iNodeBlocks[i - 1] = new INodeBlock();
                iNodeBlocks[i - 1].init(diskObj.Read(i), i);
            }
            System.out.println("inodeBlock加载完成。。。。。。");
            System.out.println("耗时:" + (System.currentTimeMillis() - startTime) + "ms");
            root = iNodeBlocks[0].getiNodes()[0];
            root.setName("/");
        }

        return errCode;
    }

    public void formatDisk() throws IOException {
        /*
         * 	格式化磁盘
         */
        diskObj.formatDisk();
//		格式化磁盘后生成文件系统，即初始化
        initializeOS();
    }

    public void initializeOS() throws IOException {
        /*
         * 	初始化文件系统
         */
        System.out.println("Initializing the file system ...");

//		写入superBlock
//		设置superBlock的属性
        int[] potteries = new int[]{diskObj.getBlocksNum(), 200, 0, 0, 51, 51};
        superBlock.setSuperBlock(potteries);
        writeSuperBlock();

//		初始化inodeblocks
        initINodeBlocks();

//		写入根目录
        INode rootDir = iNodeBlocks[0].getiNodes()[0];
        rootDir.setPos(new int[]{1, 0});
        rootDir.setId();
        rootDir.setFlag(1);
        rootDir.setOwner(0);
        rootDir.setName("/");
        write(rootDir);
        writeFile(rootDir, "1 .");
        root = rootDir;

//		添加配置文件夹
        mkdir(rootDir, 0, "etc");
//			添加用户配置文件
        createFile(getINode(getFileId(rootDir, "etc")), 0, "user", "0:root:123456 1:rxl:930223");

//		添加用户家目录
        mkdir(rootDir, 0, "home");
//			添加root和smoke家目录
        mkdir(getINode(getFileId(rootDir, "home")), 0, "root");
        mkdir(getINode(getFileId(rootDir, "home")), 1, "rxl");


        System.out.println("success: Initialize the MiniOS!");
    }

    //	初始化inodeblocks
    public void initINodeBlocks() {
        for (int i = 0; i < iNodeBlocks.length; i++) {
            iNodeBlocks[i] = new INodeBlock(EMPTY_BLOCK, i);
        }
    }

    //	创建目录
    public void mkdir(INode parent, int owner, String dirName) throws IOException {
        /*
         * 	创建目录
         */
        INode dir = getFreeINode();
        dir.setId();
        dir.setFlag(1);
        dir.setOwner(owner);
        dir.setParent(parent);
        dir.setName(dirName);
        String file = dir.getId() + " . " + parent.getId() + " .. ";
        addChild(parent, dir, dirName, file);
    }

    //	创建文件
    public void createFile(INode parent, int owner, String fileName, String file) throws IOException {
        INode fileInode = getFreeINode();
        fileInode.setId();
        fileInode.setFlag(2);
        fileInode.setOwner(owner);
        fileInode.setParent(parent);
        addChild(parent, fileInode, fileName, file);
    }

    //	向目录中添加子文件
    public void addChild(INode parent, INode child, String childName, String file) throws IOException {
        /*
         * 	增加父目录中的节点
         * 	更新父目录
         * 	添加子文件
         */
        String parFile = readFile(parent);
        parFile = (parFile + " " + child.getId() + " " + childName);
        updateFile(parent, parFile);
        writeFile(child, file);
    }

    //	写入superblock
    public void writeSuperBlock() throws IOException {
        diskObj.write(superBlock);
    }

    //	写入inode
    public void write(INode inode) throws IOException {
        int[] pos = inode.getPos();
        iNodeBlocks[pos[0] - 1].setiNode(inode, pos[1]);
        diskObj.write(pos[0], iNodeBlocks[pos[0] - 1]);
    }


    //	向磁盘写文件
    public void writeFile(INode inode, String file) throws IOException {
        /*
         * 	向磁盘中写入文件
         */
        int freeNo;
        while (file.length() > 0) {
            freeNo = superBlock.getFreeFile();
            if (file.length() <= diskObj.getBlockSize()) {
                diskObj.write(freeNo, file);
                inode.setPtrNo(freeNo);
                break;
            }
            diskObj.write(freeNo, file.substring(0, diskObj.getBlockSize()));
            file = file.substring(diskObj.getBlockSize());
        }
//			写入inode
        write(inode);
//			向空闲block写入数据后更新block
        setNextFreeList();
    }

    //	写文件到指定block
    public void writeFile(INode inode, String file, int[] ptr) throws IOException {
        /*
         * 	写文件
         */
        int freeNo;
        int[] newPtr = new int[ptr.length];
        boolean setNextFreeList = false;
        for (int i = 0; i < ptr.length && file.length() > 0; i++) {
            setNextFreeList = false;
            freeNo = ptr[i];
            if (freeNo == 0) {
                freeNo = getNextFreeBlock();
                setNextFreeList = true;
            }
            newPtr[i] = freeNo;
            diskObj.write(freeNo, file.substring(0, Math.min(diskObj.getBlockSize(), file.length())));
            if (setNextFreeList) {
                setNextFreeList();
            }
            file = file.substring(Math.min(diskObj.getBlockSize(), file.length()));
        }
        inode.setPtr(newPtr);
    }

    //	读取文件
    public String readFile(INode inode) throws IOException {
        /**
         * 	读取inode的文件
         */
        StringBuilder file = new StringBuilder();
        int[] filePtr = inode.getPtr();
        for (int i = 0; i < filePtr.length; i++) {
            if (filePtr[i] == 0) {
                break;
            }
            String line = diskObj.Read(filePtr[i]);
            file.append(line);
        }
        return file.toString();
    }

    /**
     * 优化逻辑，根据内存中的缓存结拼接
     *
     * @param parent
     * @param curName
     * @return
     */
    public String getAbsolutePath(INode parent, String curName) {
        if (parent.getId() == 1) {
            return "/" + curName;
        }

        return getAbsolutePath(parent.getParent(), parent.getName()) + "/" + curName;
    }

    public INode getINode(String path, INode parent) throws IOException {
        if (!path.startsWith("/")) {
            path = getAbsolutePath(parent, path);
        }
        return getINode(path);
    }

    //	根据路径获得inode
    public INode getINode(String path) throws IOException {
        /*
         *
         */
        path = path.trim();
        if (!path.startsWith("/")) {
            System.out.println("error: Illegal path ");
            return null;
        }
        if ("/".equals(path)) {
            return root;
        }
        String[] pathList = path.trim().split("/");
        INode inode = root;
        String[] childs = getDirChilds(inode);
        for (int i = 1; i < pathList.length; i++) {
            boolean flag = false;
            for (int j = 0; j < childs.length; j += 2) {
                if (childs[j + 1].equals(pathList[i])) {
                    INode parentInode = inode;
                    inode = getINode(Integer.parseInt(childs[j]));
                    if (inode.getName() == null) {
                        inode.setName(childs[j + 1]);
                    }
                    if (inode.getParent() == null) {
                        inode.setParent(parentInode);
                    }
                    childs = getDirChilds(inode);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println("error: not fund the file or directory");
                return null;
            }
        }
        return inode;

    }

    //	获取目录文件列表
    public String[] getDirChilds(INode parent) throws IOException {
        /*
         *
         */
        if (parent.getFlag() != 1) {
//			System.out.println("error: inode:"+parent.getId()+" isn't a directory");
            return null;
        }
        String[] childs;
        String childStr = readFile(parent);
        childs = childStr.trim().split(" ");
        return childs;

    }

    //	更新文件
    public void updateFile(INode inode, String file) throws IOException {
        /*
         * 	更新已存在的文件
         */
//		String fileStr = readFile(inode);
        int[] filePtr = inode.getPtr();
        deleteData(filePtr);
        writeFile(inode, file, filePtr);
        write(inode);
    }


    //	清楚指定区域的数据块
    public void deleteData(int[] ptr) throws IOException {
        /*
         * 	清除数据块block
         */
        for (int i = 0; i < ptr.length; i++) {
            if (ptr[i] == 0) {
                break;
            }
            diskObj.write(ptr[i], EMPTY_BLOCK);
        }
    }

    //	获取空闲inode
    public INode getFreeINode() {
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < INodeBlock.getInodesPerBlock(); j++) {
                if (iNodeBlocks[i].getiNodes()[j].getFlag() == 0) {
                    INode inode = iNodeBlocks[i].getiNodes()[j];
                    inode.setPos(new int[]{i + 1, j});
                    return inode;
                }
            }
        }
        return null;
    }

    //	
    public String getChildName(INode inode, int cId) throws IOException {
        /*
         * 	如果inode是目录，则根据cId获取文件名
         */
        int[] dirPtr = inode.getPtr();
        String file = "";
        for (int i = 0; i < dirPtr.length; i++) {
            if (dirPtr[i] != 0)
                file += diskObj.Read(dirPtr[i]);
        }
        String[] ids = file.split(" ");
        for (int i = 0; i < ids.length; i += 2) {
            if (ids[i] == String.valueOf(cId)) {
                return ids[i + 1];
            }
        }
        return null;
    }

    //	获取子文件的id
    public int getFileId(INode parent, String name) throws IOException {
        /*
         *
         */
        String file = readFile(parent);

//		行
        String[] ids = file.split(" ");
        for (int i = 0; i < ids.length; i += 2) {
            if (ids[i + 1].equals(name)) {
                return Integer.parseInt(ids[i]);
            }
        }
        return -1;
    }

    //	获得inode的文件名
    public String getINodeName(int id) throws IOException {
        /*
         *
         */
        if (id == 1) {
            return "/";
        }
        INode inode = getINode(id);
        INode parent = inode.getParent();
        String[] childs = getDirChilds(parent);
        for (int i = 0; i < childs.length; i += 2) {
            if (Integer.parseInt(childs[i]) == id) {
                return childs[i + 1];
            }
        }
        return null;
    }

    //	查找指定id的INode
    public INode getINode(int id) {
        for (int i = 0; i < iNodeBlocks.length; i++) {
            if (iNodeBlocks[i].searchNode(id)[0] == 1) {
                return iNodeBlocks[i].getiNodes()[iNodeBlocks[i].searchNode(id)[1]];
            }
        }
        return null;
    }

    //	更新superblock的freeList
    public void setNextFreeList() throws IOException {
        superBlock.setFreeFile(getNextFreeBlock());
        superBlock.setFileBlocksUsed(superBlock.getBlocksNum() + 1);
        diskObj.write(superBlock);
    }

    /**
     * TODO 可以使用dataBlock bitMap代替
     *
     * @return
     * @throws IOException
     */
    public int getNextFreeBlock() throws IOException {
        int freeBlock = superBlock.getFreeFile();
        for (int i = freeBlock; i != freeBlock - 1; i++) {
            if (diskObj.Read(i).equals(EMPTY_BLOCK)) {
                return i;
            }
            if (i == diskObj.getBlocksNum()) {
                i = superBlock.getFileBegan();
            }
        }
        return freeBlock;
    }
}