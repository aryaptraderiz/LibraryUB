package service;

import exception.FirebaseException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import util.JsonObject;

/**
 * FirestoreClient - REST API wrapper untuk komunikasi dengan Firebase Firestore.
 * Menggunakan HttpURLConnection bawaan Java, tanpa SDK/library eksternal.
 *
 * OOP Concept: Encapsulation, Exception Handling, Singleton Pattern
 *
 * Dokumen Firestore disimpan dalam format khusus, contoh:
 * {
 *   "fields": {
 *     "nama": { "stringValue": "Arya" },
 *     "stok": { "integerValue": "3" }
 *   }
 * }
 * Class ini menangani konversi dari/ke format tersebut agar service lain
 * cukup bekerja dengan JsonObject biasa (key: value polos).
 */
public class FirestoreClient {

    private static FirestoreClient instance;

    // ID PROJECT FIREBASE
    private static final String PROJECT_ID = "library-ub";
    private static final String BASE_URL =
            "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents";

    private FirestoreClient() {}

    public static FirestoreClient getInstance() {
        if (instance == null) {
            instance = new FirestoreClient();
        }
        return instance;
    }

    /**
     * Ambil semua dokumen dalam satu collection.
     * Return: Map<documentId, JsonObject fields>
     */
    public Map<String, JsonObject> getCollection(String collection) throws FirebaseException {
        Map<String, JsonObject> hasil = new LinkedHashMap<>();
        try {
            String urlStr = BASE_URL + "/" + collection;
            JsonObject response = sendRequest(urlStr, "GET", null);

            Object docsObj = response.get("documents");
            if (docsObj instanceof java.util.List) {
                for (Object docRaw : (java.util.List<?>) docsObj) {
                    if (docRaw instanceof JsonObject) {
                        JsonObject doc = (JsonObject) docRaw;
                        String name = doc.getString("name"); // full path
                        String docId = name.substring(name.lastIndexOf('/') + 1);
                        JsonObject fields = parseFields(doc.getJsonObject("fields"));
                        hasil.put(docId, fields);
                    }
                }
            }
            return hasil;
        } catch (FirebaseException fe) {
            throw fe;
        } catch (Exception e) {
            throw new FirebaseException("Gagal mengambil data dari koleksi '" + collection + "': " + e.getMessage(), e);
        }
    }

    /**
     * Ambil satu dokumen berdasarkan ID.
     * Return null jika tidak ditemukan.
     */
    public JsonObject getDocument(String collection, String docId) throws FirebaseException {
        try {
            String urlStr = BASE_URL + "/" + collection + "/" + docId;
            JsonObject response = sendRequest(urlStr, "GET", null);
            if (response.has("error")) {
                return null;
            }
            return parseFields(response.getJsonObject("fields"));
        } catch (FirebaseException fe) {
            // 404 dianggap "tidak ditemukan", bukan error fatal
            if (fe.getMessage() != null && fe.getMessage().contains("404")) {
                return null;
            }
            throw fe;
        } catch (Exception e) {
            throw new FirebaseException("Gagal mengambil dokumen '" + docId + "': " + e.getMessage(), e);
        }
    }

    /**
     * Buat / timpa dokumen dengan ID tertentu (PATCH = upsert di Firestore REST API).
     */
    public void setDocument(String collection, String docId, JsonObject data) throws FirebaseException {
        try {
            String urlStr = BASE_URL + "/" + collection + "/" + docId;
            JsonObject body = toFirestoreFields(data);
            sendRequest(urlStr, "PATCH", body.toJsonString());
        } catch (Exception e) {
            throw new FirebaseException("Gagal menyimpan dokumen '" + docId + "': " + e.getMessage(), e);
        }
    }

    /**
     * Hapus dokumen berdasarkan ID.
     */
    public void deleteDocument(String collection, String docId) throws FirebaseException {
        try {
            String urlStr = BASE_URL + "/" + collection + "/" + docId;
            sendRequest(urlStr, "DELETE", null);
        } catch (Exception e) {
            throw new FirebaseException("Gagal menghapus dokumen '" + docId + "': " + e.getMessage(), e);
        }
    }

    // ============== HTTP CORE ==============

    private JsonObject sendRequest(String urlStr, String method, String jsonBody) throws FirebaseException {
        HttpURLConnection conn = null;
        try {
            URL url = java.net.URI.create(urlStr).toURL();
            conn = (HttpURLConnection) url.openConnection();

            // HttpURLConnection bawaan Java tidak mengizinkan method PATCH secara langsung
            // (whitelist method-nya terbatas pada GET/POST/PUT/DELETE/dst).
            // Solusi resmi yang didukung Google APIs: kirim sebagai POST biasa,
            // lalu beri tahu server method aslinya lewat header X-HTTP-Method-Override.
            if ("PATCH".equals(method)) {
                conn.setRequestMethod("POST");
                conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            } else {
                conn.setRequestMethod(method);
            }

            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("Content-Type", "application/json");

            if (jsonBody != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                }
            }

            int statusCode = conn.getResponseCode();
            InputStream is = (statusCode >= 200 && statusCode < 300)
                    ? conn.getInputStream() : conn.getErrorStream();

            String responseBody = readStream(is);

            if (statusCode < 200 || statusCode >= 300) {
                throw new FirebaseException("HTTP " + statusCode + " - " + responseBody);
            }

            if (responseBody == null || responseBody.isEmpty()) {
                return new JsonObject();
            }
            return JsonObject.parse(responseBody);

        } catch (FirebaseException fe) {
            throw fe;
        } catch (java.net.SocketTimeoutException | java.net.ConnectException e) {
            throw new FirebaseException("Tidak dapat terhubung ke server Firebase. Periksa koneksi internet Anda.", e);
        } catch (Exception e) {
            throw new FirebaseException("Terjadi kesalahan komunikasi dengan Firebase: " + e.getMessage(), e);
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    // ============== KONVERSI FORMAT FIRESTORE ==============

    /**
     * Konversi JsonObject biasa (key: value) menjadi format Firestore { "fields": { ... } }
     */
    private JsonObject toFirestoreFields(JsonObject data) {
        JsonObject fieldsWrapper = new JsonObject();
        JsonObject fields = new JsonObject();

        for (Map.Entry<String, Object> entry : data.getMap().entrySet()) {
            fields.put(entry.getKey(), wrapValue(entry.getValue()));
        }
        fieldsWrapper.put("fields", fields);
        return fieldsWrapper;
    }

    private JsonObject wrapValue(Object value) {
        JsonObject wrapped = new JsonObject();
        if (value == null) {
            wrapped.put("nullValue", null);
        } else if (value instanceof Integer || value instanceof Long) {
            wrapped.put("integerValue", String.valueOf(value));
        } else if (value instanceof Double || value instanceof Float) {
            wrapped.put("doubleValue", value);
        } else if (value instanceof Boolean) {
            wrapped.put("booleanValue", value);
        } else {
            wrapped.put("stringValue", String.valueOf(value));
        }
        return wrapped;
    }

    /**
     * Konversi format Firestore { "fields": { "x": {"stringValue": "y"} } }
     * menjadi JsonObject biasa { "x": "y" }
     */
    private JsonObject parseFields(JsonObject firestoreFields) {
        JsonObject result = new JsonObject();
        if (firestoreFields == null) return result;

        for (Map.Entry<String, Object> entry : firestoreFields.getMap().entrySet()) {
            String key = entry.getKey();
            Object rawTypeWrapper = entry.getValue();
            if (rawTypeWrapper instanceof JsonObject) {
                JsonObject typeWrapper = (JsonObject) rawTypeWrapper;
                result.put(key, unwrapValue(typeWrapper));
            }
        }
        return result;
    }

    private Object unwrapValue(JsonObject typeWrapper) {
        if (typeWrapper.has("stringValue")) return typeWrapper.getString("stringValue");
        if (typeWrapper.has("integerValue")) return Integer.parseInt(typeWrapper.getString("integerValue"));
        if (typeWrapper.has("doubleValue")) return typeWrapper.getDouble("doubleValue");
        if (typeWrapper.has("booleanValue")) return typeWrapper.get("booleanValue");
        if (typeWrapper.has("nullValue")) return null;
        return null;
    }
}