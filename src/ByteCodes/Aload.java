package ByteCodes;

public class Aload extends ByteCode{
    private int index;
    public Aload(int _index){index = _index;}

    public String toString() {
            if (index <= 3 && index >= 0)
                return "aload_" + index;
            else
                return "aload " + index;
    }
}
