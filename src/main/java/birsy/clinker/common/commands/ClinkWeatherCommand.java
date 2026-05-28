package birsy.clinker.common.commands;

import birsy.clinker.common.world.level.weather.OthershoreWeather;
import birsy.clinker.common.world.level.weather.ServerOthershoreWeatherSystem;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public class ClinkWeatherCommand {
    private static final SimpleCommandExceptionType INVALID_DIMENSION = new SimpleCommandExceptionType(
            Component.literal("Dimension does not have Othershore weather!")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("clinkweather")
                .then(Commands.argument("weather", ResourceArgument.resource(context, ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY_KEY))
                        .executes(commandContext -> setWeather(commandContext.getSource(), ResourceArgument.getResource(commandContext, "weather", ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY_KEY)))
                )
        );
    }

    private static int setWeather(CommandSourceStack source, Holder.Reference<OthershoreWeather.Type<?>> weather) throws CommandSyntaxException {
        ServerOthershoreWeatherSystem system = ServerOthershoreWeatherSystem.getServerSystem(source.getLevel());
        if (system == null) throw INVALID_DIMENSION.create();
        system.system.setWeather(weather.value());
        system.distributeChangedPackets();

        source.sendSuccess(
                () -> Component.literal("Set Othershore weather to " + weather.key().location()),
                true
        );

        return 1;
    }
}
