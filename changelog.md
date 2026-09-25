## BugFixes 

- Update color notif is always sent in chat
- Update notification preference notif is always sent with Toast

## Features

- Add a way to limit the Highlighting in some area of the screens there are 5 types :
    - Everywhere : It's like the mod worked before, it highlight all the slots when they match
    - Exclude Creative : no highlighting will work in the Creative Screen, it's the new default behavior
    - Player inventory : only highlight the slots that are in the player inventory
    - Player inventory and exclude Creative : if it's in the Creative screen it only highlights the slots in the hotbar when in creative screen else it only highlights the slots that are in the player inventory
    - Container only : only highlight the slots that are in the containers, in a chest or a shulker for example
- You can now choose to color the hovered slot with the minecraft vanilla highlighting or even highlight it even if it is empty. You can choose between 5 modes to color or not the hovered slot :
  - Not colored : the hovered slot will not be highlighted at all
  - Colored : the hovered slot will be colored with the color you chose in the menu
  - Colored when not empty : the hovered slot will be colored with the color you chose in the menu if there is an item in it
  - Vanilla highlighting : the hovered item will be with the vanilla highlighting, the others items matching will be colored with the color you chose in the menu
  - Vanilla highlighting when not empty: the hovered item will be with the vanilla highlighting if there is an item in it, the others items matching will be colored with the color you chose in the menu

## Mod configuration menu enhancement

### Feature

The menu have been fully rearranged :
- All previous options buttons are now split between sections :
  - Color section : contain cursor for RGBA values, the button to use the vanilla highlighting, the button to color the hovered item
  - Application logic section : contain the Comparator mode button and the new Screen limitation feature button
  - Other : contain the notification preferences button
- Add a new button to reset the colors only (in the Color section) 
- The color viewer have been totally replaced by a fake little inventory containing 4 slots fill with wool itemstack that will be highlighted with the color you pick

### Fix

- The button to reset the values said it reset only the colors while it reset all the values
