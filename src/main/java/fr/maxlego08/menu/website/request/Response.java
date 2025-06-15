package fr.maxlego08.menu.website.request;

import java.util.Map;

public class Response {

    private final int httpCode;
    private final Map<String, Object> datas;

    /**
     * @param httpCode
     * @param datas
     */
    public Response(int httpCode, Map<String, Object> datas) {
        super();
        this.httpCode = httpCode;
        this.datas = datas;
    }

    /**
     * @return the httpCode
     */
    public int getHttpCode() {
        return this.httpCode;
    }

    /**
     * @return the datas
     */
    public Map<String, Object> getDatas() {
        return this.datas;
    }

    public int getCode() {
        return this.httpCode;
    }

    public Object get(String key) {
        return this.datas.get(key);
    }

    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) this.datas.getOrDefault(key, defaultValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Response [httpCode=" + httpCode + ", datas=" + datas + "]";
    }

}
