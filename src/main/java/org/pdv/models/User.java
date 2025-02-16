package org.pdv.models;

public class User {
    private String name;
    private AcessLevel acess;

    public User(String name, AcessLevel acess) {
        this.name = name;
        this.acess = acess;
    }

   public AcessLevel getAcess() {
        return this.acess;
   }

   public String getName() {
        return this.name;
   }

}
