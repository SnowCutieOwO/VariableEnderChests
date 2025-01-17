package me.saif.betterenderchests.converters;

import me.saif.betterenderchests.VariableEnderChests;
import me.saif.betterenderchests.converters.enderplus.EnderPlusOldConverter;
import me.saif.betterenderchests.utils.CaselessString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConverterManager {

    private VariableEnderChests plugin;
    private Map<CaselessString, Converter> converterMap = new HashMap<>();
    private boolean converting = false;

    public ConverterManager(VariableEnderChests plugin) {
        this.plugin = plugin;

        this.addConverter(new EnderPlusOldConverter(this.plugin));
        this.addConverter(new SQLiteMySQLConverter(this.plugin));
        this.addConverter(new CustomEnderChestFlatFileConverter(this.plugin));
    }

    public void addConverter(Converter converter) {
        this.converterMap.put(new CaselessString(converter.getName()), converter);
    }

    public boolean isConverting() {
        return converting;
    }

    public void setConverting(boolean converting) {
        this.converting = converting;
    }

    public Converter getConverter(String name) {
        return this.converterMap.get(new CaselessString(name));
    }

    public Set<Converter> getConverters() {
        return new HashSet<>(this.converterMap.values());
    }

}
