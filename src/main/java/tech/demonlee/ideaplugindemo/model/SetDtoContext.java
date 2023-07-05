package tech.demonlee.ideaplugindemo.model;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * @author Demon.Lee
 * @date 2023-07-04 12:04
 */
public class SetDtoContext {
    private static final Gson gson = new Gson();

    /**
     * 类属性名
     */
    private String clazzParamName;
    /**
     * param 集合，保证顺序性
     */
    private List<String> paramList;
    /**
     * key：param val：set方法
     */
    private Map<String, String> paramMtdMap;

    public SetDtoContext(String clazzParamName, List<String> paramList, Map<String, String> paramMtdMap) {
        this.clazzParamName = clazzParamName;
        this.paramList = paramList;
        this.paramMtdMap = paramMtdMap;
    }

    public String getClazzParamName() {
        return clazzParamName;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public Map<String, String> getParamMtdMap() {
        return paramMtdMap;
    }

    public String generateSetMethodStr(String param) {
        return this.clazzParamName + "." + this.paramMtdMap.get(param);
    }

    @Override
    public String toString() {
        return "SetDtoContext: " + gson.toJson(this);
    }
}
