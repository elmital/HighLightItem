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

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class Colors {
    public static float[] customFromJson(JsonObject json) {
        if (json.has("custom")) {
            try {
                return new float[]{json.get("red").getAsFloat(), json.get("green").getAsFloat(), json.get("blue").getAsFloat(), json.get("alpha").getAsFloat()};
            } catch (IllegalStateException | UnsupportedOperationException e) {
                HighlightItem.LOGGER.error("Can't convert json to a rgba color don't try to change manually the config file! Actual json is " + json);
                return HighLightColor.DEFAULT.getShaderColor();
            }
        } else {
            HighlightItem.LOGGER.error("Can't convert json to color don't try to change manually the config file! Actual json is " + json);
        }

        return HighLightColor.DEFAULT.getShaderColor();
    }

    public static JsonObject customToJson(float[] rgba) {
        var json = new JsonObject();
        json.addProperty("custom", "");
        json.addProperty("red", rgba[0]);
        json.addProperty("green", rgba[1]);
        json.addProperty("blue", rgba[2]);
        json.addProperty("alpha", rgba[3]);
        return json;
    }

    public enum HighLightColor {
        DEFAULT(new float[]{1.0f, 1.0f, 1.0f, 0.45f}),
        BLUE(new float[]{0.5f, 1.0f, 1.0f, 0.45f}),
        YELLOW(new float[]{1.0f, 1.0f, 0.0f, 0.45f}),
        RED(new float[]{1.0f, 0.0f, 0.0f, 0.45f}),
        GREEN(new float[]{0.0f, 1.0f, 0.0f, 0.45f});

        private final float[] shaderColor;

        HighLightColor(float[] shaderColor) {
            this.shaderColor = shaderColor;
        }

        public float[] getShaderColor() {
            return shaderColor;
        }

        JsonObject json() {
            JsonObject json = new JsonObject();
            json.addProperty("default", name());
            return json;
        }

        public static HighLightColor fromJson(JsonObject json) {
            try {
                var name = json.get("default").getAsString();
                return HighLightColor.valueOf(name);
            } catch (IllegalStateException | UnsupportedOperationException e) {
                HighlightItem.LOGGER.error("Can't convert json to HighLightColor don't try to change manually the config file! Actual json is " + json);
                return DEFAULT;
            }
        }
    }
    public static class HighLightColorArgumentType implements ArgumentType<HighLightColor> {
        private static final Collection<String> EXAMPLES = generateExamples();

        public static HighLightColorArgumentType color() {
            return new HighLightColorArgumentType();
        }

        public static <S> HighLightColor getColor(String name, CommandContext<S> context) {
            return context.getArgument(name, HighLightColor.class);
        }

        @Override
        public HighLightColor parse(StringReader reader) throws CommandSyntaxException {
            int areBeginning = reader.getCursor();
            if(!reader.canRead())
                reader.skip();

            while (reader.canRead() && reader.peek() != ' ')
                reader.skip();

            String colorString =  reader.getString().substring(areBeginning, reader.getCursor());
            try {
                return HighLightColor.valueOf(colorString.toUpperCase());
            } catch (IllegalArgumentException iae) {
                throw new SimpleCommandExceptionType(Text.of(iae.getMessage())).createWithContext(reader);
            }
        }

        @Override
        public Collection<String> getExamples() {
            return EXAMPLES;
        }

        private static Collection<String> generateExamples() {
            return Arrays.stream(HighLightColor.values()).map(highLightColor -> highLightColor.name().toLowerCase()).toList();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(EXAMPLES, builder);
        }
    }
}
