package me.saif.betterenderchests.command;

import me.saif.betterenderchests.VariableEnderChests;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandManager {

    private final VariableEnderChests plugin;
    private final Map<PluginCommand, Command> pluginCommands = new HashMap<>();

    public CommandManager(VariableEnderChests plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(PluginCommand command) {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) bukkitCommandMap.get(Bukkit.getServer());
            Command bukkitCommand = new CommandImpl(command);
            commandMap.register(plugin.getName(), bukkitCommand);

            this.pluginCommands.put(command, bukkitCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterAll() {
        for (PluginCommand pluginCommand : new HashSet<>(this.pluginCommands.keySet())) {
            this.unregisterCommand(pluginCommand);
        }
    }

    public void unregisterCommand(PluginCommand pluginCommand) {
        Command command = this.pluginCommands.get(pluginCommand);

        if (command == null)
            return;

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) bukkitCommandMap.get(Bukkit.getServer());

            command.unregister(commandMap);

            Field knownCommands;
            if (VariableEnderChests.MC_VERSION >= 13)
                knownCommands = commandMap.getClass().getSuperclass().getDeclaredField("knownCommands");
            else
                knownCommands = commandMap.getClass().getDeclaredField("knownCommands");

            knownCommands.setAccessible(true);
            Map<String, Command> stringCommandMap = (Map<String, Command>) knownCommands.get(commandMap);

            Set<String> toRemove = new HashSet<>();

            stringCommandMap.forEach((s, commandFromMap) -> {
                if (command == commandFromMap)
                    toRemove.add(s);
            });

            for (String s : toRemove) {
                stringCommandMap.remove(s);
            }

            if (VariableEnderChests.MC_VERSION >= 13)
                Bukkit.getServer().getClass().getMethod("syncCommands").invoke(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
