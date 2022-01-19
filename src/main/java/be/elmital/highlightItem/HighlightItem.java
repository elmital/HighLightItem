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

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class HighlightItem implements ModInitializer {
	public static final String MOD_ID = "highlight_item";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static Configurator configurator;

	public enum HighLightColor {
		DEFAULT(new float[]{1.0f, 1.0f, 1.0f, 1.0f}),
		BLUE(new float[]{0.5f, 1.0f, 1.0f, 1.0f}),
		YELLOW(new float[]{255.0f, 244.0f, 0.0f, 1.0f}),
		RED(new float[]{255.0f, 0.0f, 0.0f, 1.0f}),
		GREEN(new float[]{0.0f, 255.0f, 0.0f, 1.0f});

		private final float[] shaderColor;

		HighLightColor(float[] shaderColor) {
			this.shaderColor = shaderColor;
		}

		public float[] getShaderColor() {
			return shaderColor;
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("""
    
				-------------
				 HighLightItem
				 Copyright (C) 2022  elmital
				 This program comes with ABSOLUTELY NO WARRANTY.
				 This is free software, and you are welcome to redistribute it
				 under certain conditions.
				 See the GNU General Public License for more details. <https://www.gnu.org/licenses/>
				-------------
				""");
		try {
			LOGGER.info("Checking for configuration file");
			configurator = Configurator.getInstance();
			LOGGER.info("Config file loaded!");

			var com = HighLightCommands.inst();
			LOGGER.info("Registering commands...");
			com.register();
			LOGGER.info("Commands registered!");
			LOGGER.info("Registering command arguments...");
			com.registerArgumentTypes();
			LOGGER.info("Command arguments registered!");
			LOGGER.info("Mod init!");
		} catch (IOException | URISyntaxException e) {
			LOGGER.error("Can't setup mod properly !");
			LOGGER.throwing(e);
		} finally {
			LOGGER.info("""
         
     				-------------
     				
					""");
		}
	}
}
