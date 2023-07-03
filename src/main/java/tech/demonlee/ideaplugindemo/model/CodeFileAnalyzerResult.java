package tech.demonlee.ideaplugindemo.model;

/**
 * @author Demon.Lee
 * @date 2023-07-03 10:26
 */
public class CodeFileAnalyzerResult {

    private int privateFieldCount;
    private int publicMethodCount;
    private int overrideMethodCount;

    public CodeFileAnalyzerResult(int privateFieldCount, int publicMethodCount, int overrideMethodCount) {
        this.privateFieldCount = privateFieldCount;
        this.publicMethodCount = publicMethodCount;
        this.overrideMethodCount = overrideMethodCount;
    }

    public int getPrivateFieldCount() {
        return privateFieldCount;
    }

    public int getPublicMethodCount() {
        return publicMethodCount;
    }

    public int getOverrideMethodCount() {
        return overrideMethodCount;
    }

    public String display() {
        return "私有属性：" + privateFieldCount +
                ",\n 公有方法：" + publicMethodCount +
                ",\n 重写方法：" + overrideMethodCount;
    }
}
