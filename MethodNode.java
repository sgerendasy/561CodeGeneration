public class MethodNode {
    String ident;
    String returnType;
    String classIdent;

    public MethodNode(){}

    public MethodNode(String ident, String returnType)
    {
        this.ident = ident;
        this.returnType = returnType;
    }

    public MethodNode(String ident, String returnType, String classIdent)
    {
        this.ident = ident;
        this.returnType = returnType;
        this.classIdent = classIdent;
    }
}
