package org.sid.ebankingbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sid.ebankingbackend.enums.AccountStatus;

import java.util.Date;
import java.util.List;
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  //1
///@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) //2
//@Inheritance(strategy = InheritanceType.JOINED) //3
@DiscriminatorColumn(name = "TYPE",length = 4)  //1
@Data @NoArgsConstructor @AllArgsConstructor
public abstract class BankAccount {
    @Id
    private String id;
    private  double balance;
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @ManyToOne
    private Customer customer;
    @OneToMany(mappedBy = "bankAccount",fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations;
}
