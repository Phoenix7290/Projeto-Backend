package org.pdv;
import org.pdv.models.*;

public class Main {
    public static void main(String[] args) {
        User buyer = new User("João Bosco", AcessLevel.CUSTOMER);
        User seller = new User("Marcos", AcessLevel.SELLER);

        Product arroz = new Product("Arroz", "alimentação", 5 );
        int quantity = 4;
        Transaction transacao = new Transaction(arroz, quantity, buyer, seller);

        transacao.printTransaction();
    }
}
