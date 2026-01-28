package chat.aikf.im.allocation.model;

// chat.aikf.im.allocation.model.AssignmentResult
public class AssignmentResult {
    private String agentId;
    private String agentName;
    private String message;
    private boolean success;
    private boolean queued;

    // 私有构造
    private AssignmentResult(String agentId, String agentName, String message, boolean success, boolean queued) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.message = message;
        this.success = success;
        this.queued = queued;
    }

    public static AssignmentResult success(String agentId, String agentName, String msg) {
        return new AssignmentResult(agentId, agentName, msg, true, false);
    }

    public static AssignmentResult failure(String msg) {
        return new AssignmentResult(null, null, msg, false, false);
    }

    public static AssignmentResult queued(String msg) {
        return new AssignmentResult(null, null, msg, true, true);
    }

    // getter...
}
