package tech.demonlee.ideaplugindemo.model;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;

/**
 * @author Demon.Lee
 * @date 2023-07-04 12:04
 */
public class GetDtoContext {

    private static final Gson gson = new Gson();

    private String clazzName;
    private String clazzParam;

    /**
     * key：param val：get方法
     */
    private Map<String, String> paramMtdMap;

    public GetDtoContext(String clazzName, String clazzParam, Map<String, String> paramMtdMap) {
        this.clazzName = clazzName;
        this.clazzParam = clazzParam;
        this.paramMtdMap = paramMtdMap;
    }

    public String getClazzName() {
        return clazzName;
    }

    public String getClazzParam() {
        return clazzParam;
    }

    public Map<String, String> getParamMtdMap() {
        return paramMtdMap;
    }

    public String generateGetMethodCode(String param) {
        String val = paramMtdMap.get(param);
        if (Objects.isNull(val)) {
            return "";
        }
        return clazzParam + "." + val + "()";
    }

    @Override
    public String toString() {
        return "GetDtoContext: " + gson.toJson(this);
    }
}
