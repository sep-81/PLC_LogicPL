package ByteCodes;

public class Invokestatic extends ByteCode{
    private String functionPath;
    public Invokestatic(String _functionPath){functionPath = _functionPath;}
    public String toString(){return ("invokestatic " + functionPath);}
}
