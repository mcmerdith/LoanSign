package net.mcmerdith.loansign.storage;

public interface DataStorage {

    /**
     * Load the Data
     *
     * @return If loading was successful
     * @implSpec Data must be fully loaded when this method returns
     */
    boolean load(LoanData data);

    /**
     * Save the Data (if necessary)
     *
     * @return If the saving was successful
     * @implSpec Data must be fully saved when this method returns
     */
    boolean save(LoanData data);
}
