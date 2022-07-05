package in.co.ppay.wallet_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExpenseTrackerModel {
    // to send %age
    private String TRANSFER = "0%";    // Normal Money Transfer
    private String ENT = "0%";         // Entertainment >> Movies/Mall/Games
    private String FB = "0%";          // Food and Beverages
    private String HF = "0%";          // Health and Fitness
    private String GS = "0%";          // Grocery Store
    private String OTH = "0%";         // Others

    // to send amount spent on each merchant
    private double transferAmount;
    private double entAmount;
    private double fbAmount;
    private double hfAmount;
    private double gsAmount;
    private double othAmount;

    // to send to total amount spent
    private double totalAmountSpent;
}
