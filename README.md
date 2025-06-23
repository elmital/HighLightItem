# What is the mod doing?

Minecraft mod that highlights inventory items similar to the one hovered over.

# Features

**You can use different comparaison modes :**
 - ITEM_ONLY : This mode will only compare if the materials are identical for example if it's a oak door or not. It's how the mod has compared ItemStack until this feature was added and it's the default mode
 - ITEM_AND_AMOUNT : Like the previous mode but it will compare if stacks has the same amount for example only all the 2 oak doors stacks will be highlighted
 - ITEM_AND_NBT : It will compare like ITEM_ONLY mode plus compare the NBT for example does a name tag uses same names or shulker boxes contains the same exact contents
 - ITEM_AND_NBT_AND_AMOUNT : It's a mix of all previous modes

**For version 1.1.0+ three new comparaison modes exists :**
- NAME_ONLY : It will check if items share the same name 
- NAME_AND_AMOUNT : like previous one but check if stacks has the same amount too
- NAMESPACE : It will highlight the items who share the same namespace usefull to highlight all items that are from the same mod

**Custom colors :**
You can choose between 16.777.216 colors to tune the Highlighting either using the command or via the config menu where you can use sliders and a preview to make it easier. The format used is the [RGBA](https://en.wikipedia.org/wiki/RGBA_color_model)

**Opacity :**
With the custom colors using the [RGBA format](https://en.wikipedia.org/wiki/RGBA_color_model) it means you can tweak the transparency which is the A(for alpha) value you choose. 

**Keybinds :**
You can add keybinds to toggle the highlighting, changing mode comparaison, color the hovered element or open the config menu. 

**Language :**
The mod is available in English, French, German, Russian, Ukrainian and Chinese Simplified. And will automatically use the language(beetween these three or EN_US by default) you use for Minecraft

**Config menu (v2.0.0+):**
The old color picker is now a true configuration menu where you can change all the configs directly. To open it you can use the command `/highlightitem menu` or the keybind or if you use ModMenu directly from the Mods button.

**Notifications (v2.0.0+):**
You can choose a preferred mod notification sending mode when you have no inventory opened : 
- Default : It depends on the context. 
  - If you are using the command to make a change the notif will be sent in the chat.
  - If you pressed a key bind it will be sent in the minecraft overlay zone (above life, levels and hunger). 
- Chat : It will be sent in the chat  
- Overlay : It will be sent in the minecraft overlay zone (above life, levels and hunger).
- Toast : It will always be sent in a "minecraft toast notification"

# Commands 

If you prefer commands than keybinds and the menu there are three commands available to manage the mod :

```/highlightitem toggle``` -> activate/deactivate the mod 

```/highlightitem menu``` -> open the config menu (only for v2.0.0+)

```/highlightitem colorHover <true/false>```-> when set to true the slot of the item you are pointing with your cursor will be colored too when set to false it still will be highlight but with the vanilla highlighting

```/highlightitem color <COLOR>``` -> choose the color in a list of default colors the mod will use to highlight the items

```/highlightitem color CUSTOM <RGBA value>``` -> choose a custom color using the [RGBA](https://en.wikipedia.org/wiki/RGBA_color_model) format
