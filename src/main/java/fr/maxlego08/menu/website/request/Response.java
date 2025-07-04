package fr.maxlego08.menu.website.request;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record Response(int httpCode, Map<String, Object> datas) {

    /**
     * @param httpCode
     * @param datas
     */
    public Response {
    }

    /**
     * @return the httpCode
     */
    @Override
    public int httpCode() {
        return this.httpCode;
    }

    /**
     * @return the datas
     */
    @Override
    public Map<String, Object> datas() {
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
    public @NotNull String toString() {
        return "Response [httpCode=" + httpCode + ", datas=" + datas + "]";
    }

}
