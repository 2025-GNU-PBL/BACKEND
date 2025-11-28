// src/main/java/gnu/project/backend/chat/ChatConstants.java
package gnu.project.backend.chat.constant;

public final class ChatConstants {

    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    public static final int MAX_MESSAGE_LENGTH = 255;
    public static final int DAILY_SEND_LIMIT_PER_SIDE = 10000;

    private ChatConstants() {
    }
}
