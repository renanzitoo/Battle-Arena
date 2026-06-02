package model;

public class Player {
    int id;
    String name;

    public Player(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return "Player{" +
                "ID: " + id +
                ", Name: '" + name + '\'' +
                '}';
    }
}
