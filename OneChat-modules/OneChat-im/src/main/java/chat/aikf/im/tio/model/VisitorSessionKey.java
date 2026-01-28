package chat.aikf.im.tio.model;



public record VisitorSessionKey(String visitorId, String webStyleId){

    public static VisitorSessionKey fromString(String combined) {
        String[] parts = combined.split("::", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format: " + combined);
        }
        return new VisitorSessionKey(parts[0], parts[1]);
    }


    @Override
    public String toString() {
        return visitorId + "::" + webStyleId;
    }

    /**
     * 获取 visitorId 的后四位（如果长度 >=4），否则返回原值
     */
    public String getVisitorIdLast4() {
        if (visitorId == null || visitorId.length() < 4) {
            return visitorId; // 或返回 "" / null，根据业务需求
        }
        return visitorId.substring(visitorId.length() - 4);
    }
}
