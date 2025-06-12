
package com.batch.Services.NotificationService;

public class MessageObject {
    private String message;
    private boolean Original;

    public String getMessage() {
        return this.message;
    }

    public boolean isOriginal() {
        return this.Original;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setOriginal(final boolean Original) {
        this.Original = Original;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof MessageObject)) {
            return false;
        } else {
            MessageObject other = (MessageObject)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isOriginal() != other.isOriginal()) {
                return false;
            } else {
                Object this$message = this.getMessage();
                Object other$message = other.getMessage();
                if (this$message == null) {
                    if (other$message != null) {
                        return false;
                    }
                } else if (!this$message.equals(other$message)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MessageObject;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isOriginal() ? 79 : 97);
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = this.getMessage();
        return "MessageObject(message=" + var10000 + ", Original=" + this.isOriginal() + ")";
    }

    public MessageObject(final String message, final boolean Original) {
        this.message = message;
        this.Original = Original;
    }
}
