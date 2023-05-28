package net.mcmerdith.loansign.operations;

import org.bukkit.inventory.ItemStack;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.operations.SignShopArguments;
import org.wargamer2010.signshop.operations.SignShopOperation;
import org.wargamer2010.signshop.util.itemUtil;

import java.util.List;

public class loanPlayerMoney implements SignShopOperation {

    @Override
    public Boolean setupOperation(SignShopArguments ssArgs) {
        // No linked chests
        if (ssArgs.getContainables().isEmpty()) {
            if (ssArgs.isOperationParameter("allowNoChests"))
                return true;
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("chest_missing", ssArgs.getMessageParts()));
            return false;
        }

        // Get items from all chests
        ItemStack[] isTotalItems = itemUtil.getAllItemStacksForContainables(ssArgs.getContainables().get());

        // Chests are empty
        if (isTotalItems.length == 0) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("chest_empty", ssArgs.getMessageParts()));
            return false;
        }

        // Update the items we're working with
        ssArgs.getItems().set(isTotalItems);
        ssArgs.setMessagePart("!items", itemUtil.itemStackToString(ssArgs.getItems().get()));
        return true;
    }

    @Override
    public Boolean checkRequirements(SignShopArguments ssArgs, Boolean activeCheck) {
        // If player exists (NOT if they are online)
        if (!ssArgs.isPlayerOnline())
            return true;

        // If shop has no define items
        if (ssArgs.getItems().get() == null) {
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("no_items_defined_for_shop", ssArgs.getMessageParts()));
            return false;
        }

        // Get the items that could be used for the transaction
        List<TransactionItem> availableItems = getItemsSuitableForTransaction(ssArgs);

        if (availableItems == null) {
            ssArgs.sendFailedRequirementsMessage("player_doesnt_have_items");
            return false;
        }

        // Get the transaction item wrappers
        List<TransactionItem> transactionItems = filterItems(availableItems, ssArgs.isOperationParameter("any"));
        // Get the unwrapped itemstacks
        ItemStack[] unwrappedTransactionItems = unwrapTranscationItems(transactionItems);

        // Update the items in the transaction
        ssArgs.getItems().set(unwrappedTransactionItems);
        // Update the message with the items selected for the transaction
        ssArgs.setMessagePart("!items", itemUtil.itemStackToString(unwrappedTransactionItems));

        // Calculate a weighted average for the available multipliers
        double totalMultiplier = 0.0D;
        for (TransactionItem transactionItem : transactionItems)
            totalMultiplier += transactionItem.amountMultiplier * transactionItem.item.getDamageMultiplier();
        totalMultiplier /= transactionItems.size();

        // Set the price
        ssArgs.getPrice().set(ssArgs.getPrice().get() * totalMultiplier);

        return true;
    }

    @Override
    public Boolean runOperation(SignShopArguments ssArgs) {
        if (!checkRequirements(ssArgs, true))
            return false;
        boolean transactedAll = ssArgs.getPlayer().get().takePlayerItems(ssArgs.getItems().get()).isEmpty();
        if (!transactedAll)
            ssArgs.getPlayer().get().sendMessage(SignShopConfig.getError("could_not_complete_operation", null));
        return transactedAll;
    }
}
