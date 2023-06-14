package ByteCodes;

public class Iload extends ByteCode{
    private int index;
    public Iload(int i){index = i;}
    public String toString(){
        if (index <= 3 && index >= 0)
            return "iload_" + index;
        else
            return "iload " + index;
    }
}

