package es.localhost.proyectofinal;

public class ChatMessage {
    private final String message;
    private final String email;

    public ChatMessage(String message, String email) {
        this.message = message;
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }
}
