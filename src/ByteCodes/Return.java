package ByteCodes;

public class Return extends ByteCode{
    private int type = 0;       // 0: No expression   1: reference expression
    public Return(int _type){type = _type;}
    public String toString(){
        if(type == 0)
            return "return";
        else
            return "areturn";
    }
}
