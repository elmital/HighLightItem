/*
 *  This file is part of the HighLightItem distribution (https://github.com/elmital/HighLightItem).
 *
 *  HighLightItem minecraft mod
 *  Copyright (C) 2022  elmital
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package be.elmital.highlightItem.utils;

import be.elmital.highlightItem.OptionEnum;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ConfigUtils {
    public static <E extends Enum<E> & OptionEnum> void changeEnumOption(E option, E[] values, Consumer<E> updater) {
        updater.accept(values[(option.ordinal() + 1) % values.length]);
    }

    public static abstract class EnumArgumentType<E extends Enum<E> & OptionEnum> implements ArgumentType<E> {
        private final Class<E> enumClass;
        private final Collection<String> examples;

        protected EnumArgumentType(Class<E> clazz, E[] values) {
            this.enumClass = clazz;
            this.examples = Arrays.stream(values).map(value -> value.name().toLowerCase()).toList();
        }

        public static <S,E extends Enum<E> & OptionEnum> E getArguments(String name, Class<E> clazz, CommandContext<S> context) {
            return context.getArgument(name, clazz);
        }

        @Override
        public E parse(StringReader reader) throws CommandSyntaxException {
            int areBeginning = reader.getCursor();
            if(!reader.canRead())
                reader.skip();

            while (reader.canRead() && reader.peek() != ' ')
                reader.skip();

            final String sub = reader.getString().substring(areBeginning, reader.getCursor());
            Optional<@NonNull E> value = Enums.getIfPresent(this.enumClass, sub.toUpperCase());
            if (!value.isPresent() || value.equals(Optional.absent())) {
                throw new SimpleCommandExceptionType(Component.literal(sub + " isn't a valid value")).createWithContext(reader);
            }
            return value.get();
        }

        @Override
        public Collection<String> getExamples() {
            return this.examples;
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return SharedSuggestionProvider.suggest(getExamples(), builder);
        }
    }
}
