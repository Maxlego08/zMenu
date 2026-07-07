package fr.maxlego08.menu.api.website;

import java.io.IOException;

public class DisallowedHostException extends IOException {
    public DisallowedHostException(String message) {
        super(message);
    }
}