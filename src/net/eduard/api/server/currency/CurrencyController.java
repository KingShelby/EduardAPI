package net.eduard.api.server.currency;

import net.eduard.api.EduardAPI;
import net.eduard.api.server.currency.list.CurrencyVaultEconomy;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CurrencyController {

    private CurrencyController() {
    }

    private static final CurrencyController INSTANCE = new CurrencyController();

    public static CurrencyController getInstance() {
        return INSTANCE;
    }

    public CurrencyHandler getNextCurrency(CurrencyHandler currencyHandler) {
        Iterator<CurrencyHandler> it = currencies.values().iterator();

        while (it.hasNext()) {
            CurrencyHandler handler = it.next();
            if (currencyHandler.equals(handler)) {
                if (it.hasNext()) {
                    return it.next();
                }
                return currencies.values().iterator().next();
            }
        }
        return null;
    }

    public CurrencyHandler getCurrencyByIcon(ItemStack icon) {
        Iterator<CurrencyHandler> it = currencies.values().iterator();
        while (it.hasNext()) {
            CurrencyHandler currencyHandler = it.next();
            if (icon.equals(currencyHandler.getIcon())) {
                return currencyHandler;
            }
        }

        return null;
    }

    private Map<String, CurrencyHandler> currencies = new LinkedHashMap<>();


    public void register(SimpleCurrencyHandler simpleCurrencyHandler) {
        EduardAPI.getInstance().getConfigs().add("currency." + simpleCurrencyHandler.getName(), simpleCurrencyHandler);
        EduardAPI.getInstance().getConfigs().saveConfig();
        simpleCurrencyHandler = EduardAPI.getInstance().getConfigs().get("currency." + simpleCurrencyHandler.getName(), SimpleCurrencyHandler.class);
        System.out.println("Moeda registrada: " + simpleCurrencyHandler.getName());
        register(simpleCurrencyHandler.getName(), simpleCurrencyHandler);
    }

    public void register(String currencyName, CurrencyHandler currencyHandler) {
        currencies.put(currencyName.toLowerCase(), currencyHandler);
    }

    public boolean isRegistred(String currencName) {
        return currencies.containsKey(currencName.toLowerCase());
    }

    public CurrencyHandler getCurrencyHandler(String currencyName) {
        return currencies.get(currencyName.toLowerCase());
    }

}
