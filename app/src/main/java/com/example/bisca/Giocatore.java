package com.example.bisca;

public class Giocatore {
    private String nome;
    private int vite;
    private String frase;
    private boolean esclamazione = false;
    int cartemesc;

    public Giocatore(String n, int v, String f){
        nome = n;
        vite = v;
        frase = f;
        cartemesc = 0;
    }

    public Giocatore(String n, int v){
        nome = n;
        vite = v;
        frase = "A PEZZI";
    }

    String getName(){
        return nome;
    }

    void setName(String n){
        if (n.equals(""))
            throw new IllegalArgumentException();
        nome = n;
    }

    int getVite(){
        return vite;
    }

    void setVite(int v){
        vite = v;
    }

    String getFrase(){
        return frase;
    }

    void setFrase(String f){
        frase = f;
    }

    boolean getEsclazione(){
        return esclamazione;
    }

    void setEsclamazione(boolean e){
        esclamazione = e;
    }

    void decrementaVite(){
        if (vite != 0){
            vite--;
        }
    }

    int getCartemesc(){
        return cartemesc;
    }

    void setCartemesc(int c){
        cartemesc = c;
    }

}
