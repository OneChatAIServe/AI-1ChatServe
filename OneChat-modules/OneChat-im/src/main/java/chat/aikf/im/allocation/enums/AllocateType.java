package chat.aikf.im.allocation.enums;

public enum AllocateType {

    ALLOCATE_TYPE_PD(1), //排队
    ALLOCATE_TYPE_DH(2); //对话

    private final Integer code;

    AllocateType(Integer code) {
        this.code = code;
    }

    public Integer getCode(){
        return code;
    }

}
