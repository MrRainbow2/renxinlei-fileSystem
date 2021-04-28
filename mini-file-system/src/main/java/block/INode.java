package block;


import os.FileSystem;

public class INode {

    /**
     * inode号
     */
    private int id;
    /**
     * 文件字节数
     */
    private long byteSize;
    /**
     * 文件类型，0为空闲inode，1为文件夹，2为文件
     */
    private int flag;
    /**
     * 文件所属者id,root用户为0
     */
    private int owner;
    /**
     * 指向存储数据块
     */
    private int[] ptr;
    /**
     * 此inode的位置信息{行，列}
     */
    private int[] pos;
    /**
     * 父节点
     */
    private INode parent;

    /**
     * 文件名称
     */
    private String name;

    public INode() {
        id = 0;
        flag = 0;
        owner = 0;
        ptr = new int[FileSystem.getInodeSize() - 3];
        pos = new int[2];
    }

    public INode(int f, int o, int[] p) {
        flag = f;
        owner = o;
        ptr = new int[FileSystem.getInodeSize() - 3];
        pos = p;
    }


    public void setPtrNo(int ptrNo) {
        /*
         * 	设置文件存储块指针
         */
        int i = 0;
        while (ptr[i] != 0 && i < ptr.length) {
            i++;
        }
        ptr[i] = ptrNo;
    }

    public void freeINode() {
        flag = 0;
        owner = 0;
        for (int i = 0; i < ptr.length; i++) {
            ptr[i] = 0;
        }
    }

    @Override
    public String toString() {
        StringBuilder iNodeStr = new StringBuilder();
        iNodeStr = new StringBuilder((id + " " + flag + " " + owner + " "));
        for (int value : ptr) {
            iNodeStr.append(value).append(" ");
        }
        return iNodeStr.toString();
    }

    public int getFlag() {
        return flag;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int[] getPtr() {
        return ptr;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        id = (pos[0] - 1) * 4 + pos[1] + 1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int[] getPos() {
        return pos;
    }

    public void setPos(int[] pos) {
        this.pos = pos;
    }

    public void setPtr(int[] ptr) {
        this.ptr = ptr;
    }

    public INode getParent() {
        return parent;
    }

    public void setParent(INode parent) {
        this.parent = parent;
    }

    public long getByteSize() {
        return byteSize;
    }

    public void setByteSize(long byteSize) {
        this.byteSize = byteSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
