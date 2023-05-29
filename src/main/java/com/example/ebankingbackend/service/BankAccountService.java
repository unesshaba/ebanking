package com.example.ebankingbackend.service;

import com.example.ebankingbackend.dtos.*;
import com.example.ebankingbackend.entities.BankAccount;
import com.example.ebankingbackend.entities.CurrentAccount;
import com.example.ebankingbackend.entities.Customer;
import com.example.ebankingbackend.entities.SavingAccount;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService  {
    public CustomerDTO  saveCustomer(CustomerDTO customerDTO);

    //    Logger log= LoggerFactory.getLogger(this.getClass().getName());
    //    si ona enlover l'objet log il suffit d'ajouter l'annotation @slf4j
//    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CurrentBankAccountDTO saveCurrentBankAcount(double initalBalance, double overdraft, Long customerId)throws ClassNotFoundException;
    SavingBankAccountDTO saveSavingBankAcount(double initalBalance, double  interestRate, Long customerId)throws ClassNotFoundException;
    List<CustomerDTO> listCustomer();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId,double amount,String description) throws BankAccountNotFoundException;
    void credit(String accountId,double amount,String description)throws BankAccountNotFoundException;
    void transfer(String accountIdSource,String accountIdDestination,double amount) throws BankAccountNotFoundException;

    List <BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId)throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size)throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomer(String keyword);
}
