package ByteCodes;

public class Iconst extends ByteCode{

    private int value;
    public void Iconst(int val){value = val;}

    public String toString(){
            return "iconst_" + value;
    }
}
