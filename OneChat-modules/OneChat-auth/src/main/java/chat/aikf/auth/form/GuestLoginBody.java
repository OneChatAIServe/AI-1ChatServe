package chat.aikf.auth.form;

import jakarta.validation.constraints.NotBlank;

public class GuestLoginBody {
   @NotBlank(message = "样式ID不能为空")
    private String webStyleId;
    @NotBlank(message = "访客ID不能为空")
    private String visitorId;

    public String getWebStyleId() {
        return webStyleId;
    }

    public void setWebStyleId(String webStyleId) {
        this.webStyleId = webStyleId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }
}