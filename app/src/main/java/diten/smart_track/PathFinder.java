package diten.smart_track;

import android.util.Log;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by matthieu on 08/07/15.
 */

public class PathFinder {
    private static ArrayList OpenList;
    private static ArrayList CloseList;

    public static int NODE_DISTANCE_VALUE = 10;



    public static ArrayList findPath(ArrayList array, Node begin, Node end){
            OpenList = new ArrayList();
            CloseList = new ArrayList();

            ArrayList FinalList = new ArrayList();

            addToOPenList(begin);

            Node currentNode = null;

            while(OpenList.size() > 0){
                currentNode = getCurrentNode();
                if (currentNode == end){
                    break;
                }
                addToCloseList(currentNode);
                ArrayList ListNeighbours = getNeighbours(currentNode, array);

                Log.i("MainActivity", "Le noeud renvoyé est: " + ListNeighbours.get(0) );


                int max = ListNeighbours.size();

                Log.i("MainActivity", "La taille de la liste est de: " + max );


                for(int i = 0; i < max; i++){
                    //Log.i("MainActivity", "OK, pour l'instant3.5 " );
                    Node node = (Node)(ListNeighbours.get(i));                // ===> PROBLEM
                    if(isOnCloseList(node) || node.getWalkable() == false)
                        continue;
                    int newG = node.getParent().getG() + NODE_DISTANCE_VALUE;
                    int newH = (Math.abs(end.getLin() - node.getLin()) + Math.abs(end.getCol() - node.getCol())) * NODE_DISTANCE_VALUE;
                    int newF = newG + newH;
                    if (newG < node.getG()){
                        node.setG(newG);
                        node.setParent(currentNode);
                        node.setF(newF);
                        node.setH(newH);
                    }
                    else{
                        addToOPenList(node);
                        node.setG(newG);
                        node.setParent(currentNode);
                        node.setF(newF);
                        node.setH(newH);
                    }
                }
            }
        if (OpenList.size() == 0) {
                    return FinalList;
            }
            Node LastNode = end;
            while( LastNode != begin){
                //(LastNode.getParent());       //Faire un Log.i
                FinalList.add(LastNode);
                LastNode = LastNode.getParent();
            }

        return FinalList;
    }


    private static void addToOPenList(Node node){
        removeFromCloseList(node);
        OpenList.add(node);
    }

    private static void addToCloseList(Node node){
        removeFromOpenList(node);
        CloseList.add(node);
    }

    private static void removeFromCloseList(Node node){
        ArrayList ListTmp = new ArrayList();
        int max = CloseList.size();
        for(int i = 0; i < max; i++){
            if(CloseList.get(i) != node){
                ListTmp.add(CloseList.get(i));
            }
            CloseList = ListTmp;
        }
    }

    private static void removeFromOpenList(Node node){
        ArrayList ListTmp = new ArrayList();
        int max = OpenList.size();
        for(int i = 0; i < max; i++){
            if (OpenList.get(i) != node){
                ListTmp.add(OpenList.get(i));
            }
        }
        OpenList = ListTmp;
    }

    private static Node getCurrentNode(){
        ArrayList ListTmp = new ArrayList();
        int max = OpenList.size();
        int minF = 1000000;
        Node currNode = null;
        for(int i = 0; i < max; i++){
            Node node = (Node) OpenList.get(i);

            if (node.getF() < minF){
                minF = node.getF();
                currNode = node;
            }
        }
        return currNode;
    }

    private static ArrayList getNeighbours(Node node, ArrayList array){
        ArrayList neighbours = new ArrayList();
        int maxCol = ((ArrayList)(array.get(0))).size();
        int maxLin = array.size();

        int indiceUp = node.getLin() + 1;
        int indiceBottom = node.getLin() - 1;
        int indiceLeft = node.getCol() - 1;
        int indiceRight = node.getCol() + 1;

        if (indiceUp > -1)
            neighbours.add(array[indiceUp][node.getCol()]);
        if (indiceBottom > maxLin)
            neighbours.add(array[indiceBottom][node.getCol()]);
        if (indiceLeft > -1)
            neighbours.add(array[node.getLin()][indiceLeft]);
        if (indiceRight < maxCol)
            neighbours.add(array[node.getLin()][indiceRight]);

        return neighbours;
    }

    private static Boolean isOnCloseList(Node node){
        int max = CloseList.size();
        for (int i = 0; i < max; i++){
            if(CloseList.get(i) == node)
                return true;
        }
        return false;
    }

    private static Boolean isOnOpenList(Node node){
        int max = OpenList.size();
        for(int i = 0; i < max; i++){
            if(OpenList.get(i) == node)
                return true;
        }
        return false;
    }

}
