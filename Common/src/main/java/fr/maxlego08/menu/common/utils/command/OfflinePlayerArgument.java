package fr.maxlego08.menu.common.utils.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public final class OfflinePlayerArgument implements CustomArgumentType.Converted<UUID, String> {

    private static final DynamicCommandExceptionType ERROR_UNKNOW_PLAYER = new DynamicCommandExceptionType(name -> MessageComponentSerializer.message().serialize(Component.text("Unknown player: " + name)));

    @Override
    public UUID convert(String nativeType) throws CommandSyntaxException {
        UUID playerId = OfflinePlayerCache.getUUID(nativeType);
        if (playerId == null) {
            throw ERROR_UNKNOW_PLAYER.create(nativeType);
        }
        return playerId;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S>CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {
        return OfflinePlayerCache.suggestPlayerNames(builder);
    }
}
