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

package be.elmital.highlightItem;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;


public class HighLightColorArgumentType implements ArgumentType<HighlightItem.HighLightColor> {
    private static final Collection<String> EXAMPLES = generateExamples();

    public static HighLightColorArgumentType color() {
        return new HighLightColorArgumentType();
    }

    public static <S> HighlightItem.HighLightColor getColor(String name, CommandContext<S> context) {
        return context.getArgument(name, HighlightItem.HighLightColor.class);
    }

    @Override
    public HighlightItem.HighLightColor parse(StringReader reader) throws CommandSyntaxException {
        int areBeginning = reader.getCursor();
        if(!reader.canRead())
            reader.skip();

        while (reader.canRead() && reader.peek() != ' ')
            reader.skip();

        String colorString =  reader.getString().substring(areBeginning, reader.getCursor());
        try {
            return HighlightItem.HighLightColor.valueOf(colorString.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new SimpleCommandExceptionType(new LiteralText(iae.getMessage())).createWithContext(reader);
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static Collection<String> generateExamples() {
        return Arrays.stream(HighlightItem.HighLightColor.values()).map(HighlightItem.HighLightColor::name).toList();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EXAMPLES, builder);
    }
}
