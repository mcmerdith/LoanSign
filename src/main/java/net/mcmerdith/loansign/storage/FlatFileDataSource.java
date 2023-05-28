package net.mcmerdith.loansign.storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.mcmerdith.loansign.LoanSignMain;
import net.mcmerdith.loansign.model.Loan;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Period;
import java.util.List;
import java.util.UUID;

public class FlatFileDataSource extends DataSource {
    private final Gson gson = new Gson();

    private File getLoanFile() {
        return new File(LoanSignMain.instance.getDataFolder(), "loans.json");
    }

    @Override
    public boolean save() {
        String data = gson.toJson(loans);
        try {
            Files.writeString(getLoanFile().toPath(), data,
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
    public boolean load() {
        try {
            loans = gson.fromJson(Files.readString(getLoanFile().toPath(), StandardCharsets.UTF_8), new TypeToken<>() {
            });
            return true;
        } catch (IOException e) {
            logger.exception(e, "Failed to write data");
        } catch (JsonSyntaxException e) {
            logger.exception(e, "Syntax error in data file");
        }

        return false;
    }
}
