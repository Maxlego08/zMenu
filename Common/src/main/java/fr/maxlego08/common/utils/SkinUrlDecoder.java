package fr.maxlego08.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class SkinUrlDecoder {
    private static final String KEY_TEXTURES = "textures";
    private static final String KEY_SKIN = "SKIN";
    private static final String KEY_URL = "url";

    public static URI extractSkinUrl(String base64EncodedData) throws URISyntaxException {
        String decodedUrl = tryDecodeBase64AndExtractUrl(base64EncodedData);
        return new URI(decodedUrl);
    }

    private static String tryDecodeBase64AndExtractUrl(String base64EncodedData) {
        try {
            String jsonString = decodeBase64(base64EncodedData);
            return extractUrlFromJson(jsonString);
        } catch (IllegalArgumentException | JsonParseException e) {
            // Si le décodage échoue, on considère que l'entrée est déjà une URL
            return base64EncodedData;
        }
    }

    private static String decodeBase64(String base64) {
        return new String(Base64.getDecoder().decode(base64));
    }

    private static String extractUrlFromJson(String jsonString) {
        JsonElement element = JsonParser.parseString(jsonString);
        return element.getAsJsonObject()
                .get(KEY_TEXTURES).getAsJsonObject()
                .get(KEY_SKIN).getAsJsonObject()
                .get(KEY_URL).getAsString();
    }
}