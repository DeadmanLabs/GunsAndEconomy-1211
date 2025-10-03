===== GCAA RECIPE FIX FOR SERVERS =====

This KubeJS script fixes missing recipes in the GCAA mod when running on servers.

INSTALLATION:
1. Install KubeJS mod on your server (required)
2. Navigate to your server's main directory
3. Create the folder structure: kubejs/server_scripts/
4. Copy gcaa_recipes_fix.js into kubejs/server_scripts/
5. Restart the server

WHAT THIS FIXES:
- Vending Machine crafting recipe
- Bullet Crafting Table recipe  
- Transaction Terminal recipe (handheld item)
- Lead Block <-> Lead Ingot conversions
- Lead Nugget <-> Lead Ingot conversions
- Thin Copper Plate recipe
- Lead Ore smelting/blasting
- Asphalt Ore to Plastic smelting/blasting

VERIFICATION:
After server restart, you should see this in console:
"[GCAA Recipe Fix] All recipes have been registered successfully!"

Players should now be able to craft:
- Vending Machine (Glass Panes + Bell + Iron Trapdoor + Hopper)
- Bullet Crafting Table (Iron Ingots + Crafting Table + Wool + Piston)
- Transaction Terminal (Glass Panes + String + Redstone + Copper Ingots)
- All other GCAA items with their intended recipes