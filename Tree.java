
public class Tree {
	private static Tree tree;
    private Node root;
    public Tree() {
        root=null;
    }
    public void setRoot(Node root) {
        this.root = root;
    }
    public static Tree getInstance()
    {
    	if (tree == null)
        {
            tree = new Tree();
        }
        return tree;
    }
    public boolean isEmpty() {
        return root == null;
    }
    public Node getRoot() {
        return root;
    }
    public boolean exists(String key) {
        return find(root, key);
    }

    private boolean find(Node node, String id) {
        boolean res = false;
        if (node.getId().equals(id))
            return true;

        else {
            for (Node child : node.getChildren())
                if (find(child, id))
                    res = true;
        }

        return res;
    }

    public Node findNode(Node node, String id) {
        if (node == null)
            return null;
        if (node.getId().equals(id))
            return node;
        else {
            Node cnode = null;
            for (Node child : node.getChildren())
                if ((cnode = findNode(child, id)) != null)
                    return cnode;
        }
        return null;
    }
	public Node LCA(Node root, String n1,String n2) {
    	if(root == null) return null;
    	Node a= findNode(root, n1);
    	Node b= findNode(root, n2);
    	if(a == root || b == root) return root;
    	Node  ancestor = null;
    	for(Node child: root.getChildren()) {
    		Node found = LCA(child, n1, n2);
    		 if(found != null) {
    			 if(ancestor == null) ancestor = found;
    			 else return root;
    		 }
    	}
    	if(ancestor != null) return ancestor;
    	return null;
    	  
    
}



}
