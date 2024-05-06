package ByteCodes;

public class Bipush extends ByteCode{

    private int value;
    public Bipush(int val){value = val;}

    public String toString(){
        return "bipush " + value;
    }
}