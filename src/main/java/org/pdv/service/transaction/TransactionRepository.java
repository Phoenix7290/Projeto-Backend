package org.pdv.service.transaction;

import org.pdv.domain.transaction.Transaction;

import java.util.List;

public interface TransactionRepository {
    public String save(Transaction transaction);

    public void delete(String id);

    public List<Transaction> findAll();

    public Transaction findById(String id);
}
