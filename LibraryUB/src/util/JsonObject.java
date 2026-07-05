package util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JsonObject - parser & builder JSON sederhana tanpa library eksternal.
 * Dibuat khusus agar project ini tidak butuh dependency tambahan (Maven/Gradle)
 * sehingga bisa langsung di-compile di NetBeans/VSCode tanpa setup ribet.
 *
 * OOP Concept: Encapsulation, Collection (Map internal)
 */
public class JsonObject {

    private Map<String, Object> data;

    public JsonObject() {
        this.data = new LinkedHashMap<>();
    }

    public JsonObject put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getString(String key) {
        Object v = data.get(key);
        return v == null ? null : String.valueOf(v);
    }

    public int getInt(String key) {
        Object v = data.get(key);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(String.valueOf(v));
    }

    public double getDouble(String key) {
        Object v = data.get(key);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        return Double.parseDouble(String.valueOf(v));
    }

    public boolean has(String key) {
        return data.containsKey(key) && data.get(key) != null;
    }

    public JsonObject getJsonObject(String key) {
        Object v = data.get(key);
        return (v instanceof JsonObject) ? (JsonObject) v : null;
    }

    public Map<String, Object> getMap() {
        return data;
    }

    /**
     * Serialize JsonObject jadi String JSON
     */
    public String toJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            sb.append(valueToJson(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    private static String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof JsonObject) {
            return ((JsonObject) value).toJsonString();
        } else if (value instanceof String) {
            return "\"" + escape((String) value) + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        } else {
            return "\"" + escape(String.valueOf(value)) + "\"";
        }
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Parse String JSON jadi JsonObject
     */
    public static JsonObject parse(String json) {
        JsonParser parser = new JsonParser(json);
        Object result = parser.parseValue();
        if (result instanceof JsonObject) {
            return (JsonObject) result;
        }
        JsonObject wrapper = new JsonObject();
        return wrapper;
    }

    /**
     * Inner class: parser rekursif sederhana
     * OOP Concept: Encapsulation (private parsing logic)
     */
    private static class JsonParser {
        private String s;
        private int pos;

        JsonParser(String s) {
            this.s = s;
            this.pos = 0;
        }

        Object parseValue() {
            skipWhitespace();
            if (pos >= s.length()) return null;
            char c = s.charAt(pos);
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't' || c == 'f') return parseBoolean();
            if (c == 'n') { pos += 4; return null; }
            return parseNumber();
        }

        JsonObject parseObject() {
            JsonObject obj = new JsonObject();
            pos++; // skip {
            skipWhitespace();
            if (pos < s.length() && s.charAt(pos) == '}') { pos++; return obj; }
            while (pos < s.length()) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                pos++; // skip :
                Object value = parseValue();
                obj.put(key, value);
                skipWhitespace();
                if (pos < s.length() && s.charAt(pos) == ',') {
                    pos++;
                    continue;
                }
                if (pos < s.length() && s.charAt(pos) == '}') {
                    pos++;
                    break;
                }
                break;
            }
            return obj;
        }

        java.util.List<Object> parseArray() {
            java.util.List<Object> list = new java.util.ArrayList<>();
            pos++; // skip [
            skipWhitespace();
            if (pos < s.length() && s.charAt(pos) == ']') { pos++; return list; }
            while (pos < s.length()) {
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                if (pos < s.length() && s.charAt(pos) == ',') {
                    pos++;
                    continue;
                }
                if (pos < s.length() && s.charAt(pos) == ']') {
                    pos++;
                    break;
                }
                break;
            }
            return list;
        }

        String parseString() {
            StringBuilder sb = new StringBuilder();
            pos++; // skip opening quote
            while (pos < s.length() && s.charAt(pos) != '"') {
                char c = s.charAt(pos);
                if (c == '\\' && pos + 1 < s.length()) {
                    pos++;
                    char esc = s.charAt(pos);
                    switch (esc) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'u':
                            String hex = s.substring(pos + 1, pos + 5);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                            break;
                        default: sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            pos++; // skip closing quote
            return sb.toString();
        }

        Boolean parseBoolean() {
            if (s.startsWith("true", pos)) {
                pos += 4;
                return true;
            } else {
                pos += 5;
                return false;
            }
        }

        Object parseNumber() {
            int start = pos;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos)) ||
                    s.charAt(pos) == '-' || s.charAt(pos) == '+' ||
                    s.charAt(pos) == '.' || s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
                pos++;
            }
            String numStr = s.substring(start, pos);
            if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
                return Double.parseDouble(numStr);
            }
            try {
                return Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                return Long.parseLong(numStr);
            }
        }

        void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }
    }
}
