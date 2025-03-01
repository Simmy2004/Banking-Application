package org.poo.accounts;

import lombok.Data;
import org.poo.solution.Database;
import org.poo.utils.Utils;

@Data
public class Card {
    private String number;
    private String status;
    private String ownerMail;
    public Card(final String ownerMail) {
        this.number = Utils.generateCardNumber();
        status = "active";
        this.ownerMail = ownerMail;
    }

    /**
     * Method used for future extensions, the normal card will not change
     * it's number if a payment is made.
     * @param database Database containing all the users and information about them
     * @param timestamp Time when the operation was made
     */
    public void pay(final Database database, final int timestamp) {
    }


}
