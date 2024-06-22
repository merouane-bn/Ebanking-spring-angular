package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.entities.CurrentAccount;
import org.sid.ebankingbackend.entities.SavingAccount;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Pour appliquer Lazy
@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    public void consulter(){
        //chercher un compte
        BankAccount bankAccount=
                bankAccountRepository.findById("0277b3fe-b06b-48d9-913f-0d4a3d404700").orElse(null);
        if(bankAccount!=null) {
            System.out.println("************************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getClass().getSimpleName());
            if (bankAccount instanceof CurrentAccount) {
                System.out.println("Over Draft=>" + ((CurrentAccount) bankAccount).getOverDraft());

            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("Rate=>" + ((SavingAccount) bankAccount).getInterestRate());

            }
            //afficher historique de ce compte
            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("************************");
                System.out.println(op.getType() + "\t" + op.getAmount() + "\t" + op.getOperationDate());


            });
        }

    }
}
