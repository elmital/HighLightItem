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
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

public class ItemComparator {
    public static final BiPredicate<ItemStack, ItemStack> itemOnlyPredicate = (stack, stack2) -> stack.getItem().equals(stack2.getItem());
    public static final BiPredicate<ItemStack, ItemStack> itemAndAmountPredicate = ((BiPredicate<ItemStack, ItemStack>) (stack, stack2) -> stack.getCount() == stack2.getCount()).and(itemOnlyPredicate);
    public static final BiPredicate<ItemStack, ItemStack> itemAndNBTPredicate = itemOnlyPredicate.and((stack, stack2) -> (stack.getNbt() == null && stack2.getNbt() == null) || (stack.getNbt() != null && stack2.getNbt() != null && stack.getNbt().equals(stack2.getNbt())));
    public static final BiPredicate<ItemStack, ItemStack> equalsPredicate = itemAndAmountPredicate.and((stack, stack2) -> (stack.getNbt() == null && stack2.getNbt() == null) || (stack.getNbt() != null && stack2.getNbt() != null && stack.getNbt().equals(stack2.getNbt())));

    public static boolean test(Comparators comparator, ItemStack stack, ItemStack stack2) {
        return switch (comparator) {
            case ITEM_ONLY -> itemOnlyPredicate.test(stack, stack2);
            case ITEM_AND_AMOUNT -> itemAndAmountPredicate.test(stack, stack2);
            case ITEM_AND_NBT -> itemAndNBTPredicate.test(stack, stack2);
            case ITEM_AND_NBT_AND_AMOUNT -> equalsPredicate.test(stack, stack2);
        };
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
        ITEM_ONLY,
        ITEM_AND_AMOUNT,
        ITEM_AND_NBT,
        ITEM_AND_NBT_AND_AMOUNT
    }
}
