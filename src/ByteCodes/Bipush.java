package ByteCodes;

public class Bipush extends ByteCode{

    private int value;
    public void Bipush(int val){value = val;}

    public String toString(){
        return "bipush_" + value;
    }
}