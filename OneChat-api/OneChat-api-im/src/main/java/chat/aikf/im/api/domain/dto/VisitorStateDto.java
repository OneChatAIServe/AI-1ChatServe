package chat.aikf.im.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorStateDto {
    private String kfVisitorId;
   private String kfRuleId;
   private String visitorId;
   private String userAccount;
   private String webStyleId;
}
