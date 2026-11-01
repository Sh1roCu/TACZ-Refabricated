package com.tacz.guns.init;

import com.tacz.guns.command.RootCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class CommandRegistry {
    public static void onServerStaring() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, context, environment) ->
                RootCommand.register(dispatcher)));
    }
}
