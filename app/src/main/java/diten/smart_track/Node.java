package diten.smart_track;

/**
 * Created by matthieu on 08/07/15.
 */
public class Node {
    private boolean walkable;
    private int g, h, f;
    private int col, lin;
    private Node parent;

    public Node(){
        walkable = true;
        g = h = f = 0;
        parent = this;
    }

    public void setParent(Node parent){
        this.parent = parent;
    }

    public void setWalkable(Boolean walkable){
        this.walkable = walkable;
    }

    public void setG(int g){
        this.g = g;
    }

    public void setH(int h){
        this.h = h;
    }

    public void setF(int f){
        this.f = f;
    }

    public void setCol(int col){
        this.col = col;
    }

    public void setLin(int lin){
        this.lin = lin;
    }

    public boolean getWalkable(){
        return walkable;
    }

    public Node getParent(){
        return parent;
    }

    public int getG(){
        return g;
    }

    public int getH(){
        return h;
    }

    public int getF(){
        return f;
    }

    public int getCol(){
        return col;
    }

    public int getLin(){
        return lin;
    }
}
