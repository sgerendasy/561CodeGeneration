import java.util.ArrayList;
import java.util.List;

public class Node {
    private String id;
    private Node parent;
    private List<Node> children;
    

    public Node(String id) {
        this.id = id;
        this.children = new ArrayList<Node>();
    }
    public void addChild(Node child) {
        child.setParent(this);
        children.add(child);
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }
    public String getId() {
        return this.id;
    }

    public Node getParent() {
        return this.parent;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj)
            return false;

        if (obj instanceof Node) {
            if (((Node) obj).getId().equals(this.id))
                return true;
        }

        return false;
    }

    @Override
    public java.lang.String toString() {
        return this.id.toString();
    }

}