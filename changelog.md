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

## Mod configuration menu enhancement

### Feature

The menu have been fully rearranged :
- All previous options buttons are now split between sections :
  - Color section : contain cursor for RGBA values, the button to use the vanilla highlighting, the button to color the hovered item
  - Application logic section : contain the Comparator mode button and the new Screen limitation feature button
  - Other : contain the notification preferences button
- Add a new button to reset the colors only (in the Color section) 

### Fix

- The button to reset the values said it reset only the colors while it reset all the values
