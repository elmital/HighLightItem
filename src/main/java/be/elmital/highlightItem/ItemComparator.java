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
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

public class ItemComparator {
    public static boolean test(Comparators comparator, ItemStack stack, ItemStack stack2) {
        return comparator.predicate.test(stack, stack2);
    }

    public static class ComparatorArgumentType implements ArgumentType<Comparators> {
        private static final Collection<String> EXAMPLES = generateExamples();

        public static ComparatorArgumentType comparator() {
            return new ComparatorArgumentType();
        }

        public static <S> Comparators getComparator(String name, CommandContext<S> context) {
            return context.getArgument(name, Comparators.class);
        }

        @Override
        public Comparators parse(StringReader reader) throws CommandSyntaxException {
            int areBeginning = reader.getCursor();
            if(!reader.canRead())
                reader.skip();

            while (reader.canRead() && reader.peek() != ' ')
                reader.skip();

            String mode = reader.getString().substring(areBeginning, reader.getCursor());
            try {
                return Comparators.valueOf(mode.toUpperCase());
            } catch (IllegalArgumentException iae) {
                throw new SimpleCommandExceptionType(Text.of(iae.getMessage())).createWithContext(reader);
            }
        }

        @Override
        public Collection<String> getExamples() {
            return EXAMPLES;
        }

        private static Collection<String> generateExamples() {
            return Arrays.stream(Comparators.values()).map(comparator -> comparator.name().toLowerCase()).toList();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(EXAMPLES, builder);
        }
    }

    public enum Comparators {
        ITEM_ONLY((stack, stack2) -> stack.getItem().equals(stack2.getItem())),
        ITEM_AND_AMOUNT(((BiPredicate<ItemStack, ItemStack>) (stack, stack2) -> stack.getCount() == stack2.getCount()).and(ITEM_ONLY.predicate)),
        ITEM_AND_NBT(ITEM_ONLY.predicate.and((stack, stack2) -> (stack.getNbt() == null && stack2.getNbt() == null) || (stack.getNbt() != null && stack2.getNbt() != null && stack.getNbt().equals(stack2.getNbt())))),
        ITEM_AND_NBT_AND_AMOUNT(ITEM_AND_AMOUNT.predicate.and((stack, stack2) -> (stack.getNbt() == null && stack2.getNbt() == null) || (stack.getNbt() != null && stack2.getNbt() != null && stack.getNbt().equals(stack2.getNbt())))),
        NAME_ONLY((stack, stack2) -> stack.getName().equals(stack2.getName())),
        NAME_AND_AMOUNT(((BiPredicate<ItemStack, ItemStack>) (stack, stack2) -> stack.getCount() == stack2.getCount()).and(NAME_ONLY.predicate)),
        NAMESPACE((stack, stack2) -> {
            var key1 = Registries.ITEM.getKey(stack.getItem()).orElse(null);
            var key2 = Registries.ITEM.getKey(stack2.getItem()).orElse(null);
            return key1 != null && key2 != null && key1.getValue().getNamespace().equalsIgnoreCase(key2.getValue().getNamespace());
        });

        final BiPredicate<ItemStack, ItemStack> predicate;

        Comparators(BiPredicate<ItemStack, ItemStack> predicate) {
            this.predicate = predicate;
        }

        public String translationKey() {
            return "highlightitem.comparator." + this.name().toLowerCase();
        }
    }
}
