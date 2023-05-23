package com.example.ebankingbackend.service;

import com.example.ebankingbackend.entities.BankAccount;
import com.example.ebankingbackend.entities.CurrentAccount;
import com.example.ebankingbackend.entities.Customer;
import com.example.ebankingbackend.entities.SavingAccount;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;

import java.util.List;

public interface BankAccountService  {
    public Customer  saveCustomer(Customer customer);
    CurrentAccount saveCurrentBankAcount(double initalBalance, double overdraft, Long customerId)throws ClassNotFoundException;
    SavingAccount saveSavingBankAcount(double initalBalance, double  interestRate, Long customerId)throws ClassNotFoundException;
    List<Customer> listCustomer();
    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId,double amount,String description) throws BankAccountNotFoundException;
    void credit(String accountId,double amount,String description)throws BankAccountNotFoundException;
    void transfer(String accountIdSource,String accountIdDestination,double amount) throws BankAccountNotFoundException;

    List <BankAccount> bankAccountList();
}
