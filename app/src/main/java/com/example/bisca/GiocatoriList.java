package com.example.bisca;

import java.util.ArrayList;

public class GiocatoriList {
    ArrayList<Giocatore> list;

    public GiocatoriList(){
        list = new ArrayList();
    }

    void add(Giocatore g){
        list.add(g);
    }

    Giocatore get(int index){
        if (index >= 0 && index < getSize())
            return list.get(index);
        throw new IndexOutOfBoundsException();
    }

    Giocatore getFromName(String n){
        for (int i = 0; i < this.getSize(); i++){
            if (list.get(i).getName().equals(n))
                return list.get(i);
        }
        return null;
    }

    boolean remove(Giocatore g){
        return list.remove(g);
    }

    boolean removeFromName(String n){
        return list.remove(this.getFromName(n));
    }

    Giocatore remove(int index){
        return list.remove(index);
    }

    boolean isPresent(String n){
        for (int i = 0; i < this.getSize(); i++){
            if (this.get(i).getName().equals(n))
                return true;
        }
        return false;
    }

    int getSize(){
        return list.size();
    }

    ArrayList getList(){
        return list;
    }

}



/**public boolean Coincide(String name, String pass){
 for (int i = 0; i < this.getSize(); i++){
 if (this.get(i).getUsername().equals(name))
 if (this.get(i).getPassword().equals(pass))
 return true;
 }
 return false;
 }*/