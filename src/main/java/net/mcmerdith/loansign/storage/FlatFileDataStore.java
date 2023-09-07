package net.mcmerdith.loansign.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.mcmerdith.loansign.LoanSignMain;
import net.mcmerdith.loansign.model.Loan;
import net.mcmerdith.mcmpluginlib.McmPluginLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FlatFileDataStore implements DataStore {
    private static final McmPluginLogger logger = McmPluginLogger.classInstance(FlatFileDataStore.class);

    private final Gson gson = new Gson();

    private File getLoanFile() {
        return new File(LoanSignMain.instance.getDataFolder(), "loans.json");
    }

    @Override
    public boolean save(LoanData data) {
        String dataString = gson.toJson(data.getAllLoans());
        try {
            Files.writeString(getLoanFile().toPath(), dataString,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            logger.exception(e, "Failed to write data");
        }

        return false;
    }

    @Override
    public boolean load(LoanData data) {
        try {
            data.setLoans(gson.fromJson(Files.readString(getLoanFile().toPath(), StandardCharsets.UTF_8), new TypeToken<List<Loan>>() {
            }));
            return true;
        } catch (IOException e) {
            logger.exception(e, "Failed to write data");
        } catch (JsonSyntaxException e) {
            logger.exception(e, "Syntax error in data file");
        }

        return false;
    }
}
