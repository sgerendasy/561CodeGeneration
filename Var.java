

public class Var
{
    public String ident;
    public String type;

    public Var(String i, String t)
    {
        this.ident = i;
        this.type = t;
    }

    public void UpdateType(String newType) throws Exception
    {
        //check to make sure newType is a sub type of the old type

    	//if (TypeChecker.checkSubtype(newType, this.type))
        Node newNode = Tree.getInstance().LCA(Tree.getInstance().getRoot(), newType, this.type);
        if ((newNode) != null)
        {
            this.type = newNode.getId();
        }
        else
        {
            throw new Exception("Cannot change " + this.type + " to " + newType);
        }
    }
}

