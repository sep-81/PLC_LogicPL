package ByteCodes;

public class Istore extends ByteCode{

    private int index;

    public void Istore(int i){index = i;}
    public String toString(){
        if (index <= 3 && index >= 0)
            return "istore_" + index;
        else
            return "istore " + index;
    }

}
