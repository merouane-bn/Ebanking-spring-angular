package org.sid.ebankingbackend.exceptions;

//exceptions no surveille extends runtimeexceptionset surveiller cest extends exception dans ce cas on dooit
// faire soit try catcg ou throw
public class CustomerNotFoundException extends Exception {
    public CustomerNotFoundException(String message) {
        super(message);

    }
}
