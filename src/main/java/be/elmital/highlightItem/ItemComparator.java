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

import be.elmital.highlightItem.utils.ConfigUtils;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.BiPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class ItemComparator {
    public static boolean test(Comparators comparator, ItemStack stack, ItemStack stack2) {
        return comparator.predicate.test(stack, stack2);
    }

    public static class ComparatorArgumentType extends ConfigUtils.EnumArgumentType<Comparators> {
        ComparatorArgumentType() {
            super(Comparators.class, Comparators.values());
        }

        public static ComparatorArgumentType comparator() {
            return new ComparatorArgumentType();
        }

        public static <S> Comparators getComparator(String name, CommandContext<S> context) {
            return context.getArgument(name, Comparators.class);
        }
    }

    public enum Comparators implements OptionEnum {
        ITEM_ONLY((stack, stack2) -> stack.getItem().equals(stack2.getItem())),
        ITEM_AND_AMOUNT(((BiPredicate<ItemStack, ItemStack>) (stack, stack2) -> stack.getCount() == stack2.getCount()).and(ITEM_ONLY.predicate)),
        ITEM_AND_NBT(ITEM_ONLY.predicate.and((stack, stack2) -> (stack.getComponents() == null && stack2.getComponents() == null) || (stack.getComponents() != null && stack2.getComponents() != null && stack.getComponents().equals(stack2.getComponents())))),
        ITEM_AND_NBT_AND_AMOUNT(ITEM_AND_AMOUNT.predicate.and((stack, stack2) -> (stack.getComponents() == null &&  stack2.getComponents() == null) || (stack.getComponents() != null && stack2.getComponents() != null && stack.getComponents().equals(stack2.getComponents())))),
        NAME_ONLY((stack, stack2) -> stack.getHoverName().equals(stack2.getHoverName())),
        NAME_AND_AMOUNT(((BiPredicate<ItemStack, ItemStack>) (stack, stack2) -> stack.getCount() == stack2.getCount()).and(NAME_ONLY.predicate)),
        NAMESPACE((stack, stack2) -> {
            var key1 = BuiltInRegistries.ITEM.getResourceKey(stack.getItem()).orElse(null);
            var key2 = BuiltInRegistries.ITEM.getResourceKey(stack2.getItem()).orElse(null);
            return key1 != null && key2 != null && key1.identifier().getNamespace().equalsIgnoreCase(key2.identifier().getNamespace());
        });

        final BiPredicate<ItemStack, ItemStack> predicate;

        Comparators(BiPredicate<ItemStack, ItemStack> predicate) {
            this.predicate = predicate;
        }

        public String translationKey() {
            return "highlightitem.comparator." + this.name().toLowerCase();
        }

        @Override
        public int getId() {
            return this.ordinal();
        }

        @Override
        public String getKey() {
            return translationKey();
        }
    }
}
