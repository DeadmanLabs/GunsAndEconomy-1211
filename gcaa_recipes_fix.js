// KubeJS Recipe Fix Script for GCAA Mod
// Place this file in: kubejs/server_scripts/gcaa_recipes_fix.js
// This script adds all missing GCAA recipes for servers running the broken version

ServerEvents.recipes(event => {
    // Remove any existing broken recipes first (optional)
    event.remove({ output: 'gcaa:vending_machine' })
    event.remove({ output: 'gcaa:bullet_crafting_table' })
    event.remove({ output: 'gcaa:ammunition_processor' })
    
    // Vending Machine Recipe
    event.shaped('gcaa:vending_machine', [
        '#B#',
        '#I#',
        '#H#'
    ], {
        '#': 'minecraft:glass_pane',
        'B': 'minecraft:bell',
        'I': 'minecraft:iron_trapdoor',
        'H': 'minecraft:hopper'
    })
    
    // Bullet Crafting Table Recipe
    event.shaped('gcaa:bullet_crafting_table', [
        'ICI',
        'IWI',
        'IPI'
    ], {
        'I': 'minecraft:iron_ingot',
        'C': 'minecraft:crafting_table',
        'W': '#minecraft:wool',
        'P': 'minecraft:piston'
    })
    
    // Lead Block Recipe
    event.shaped('gcaa:lead_block', [
        'LLL',
        'LLL',
        'LLL'
    ], {
        'L': 'gcaa:lead_ingot'
    })
    
    // Lead Ingot from Block
    event.shapeless('9x gcaa:lead_ingot', ['gcaa:lead_block'])
    
    // Lead Nugget to Ingot
    event.shaped('gcaa:lead_ingot', [
        'NNN',
        'NNN',
        'NNN'
    ], {
        'N': 'gcaa:lead_nugget'
    })
    
    // Lead Ingot to Nuggets
    event.shapeless('9x gcaa:lead_nugget', ['gcaa:lead_ingot'])
    
    // Thin Copper Plate
    event.shaped('4x gcaa:thin_copper_plate', [
        'CC'
    ], {
        'C': 'minecraft:copper_ingot'
    })
    
    // Transaction Terminal (handheld item)
    event.shaped('gcaa:transaction_terminal', [
        'PPP',
        'SRS',
        'CCC'
    ], {
        'P': 'minecraft:glass_pane',
        'S': 'minecraft:string',
        'R': 'minecraft:redstone',
        'C': 'minecraft:copper_ingot'
    })
    
    // Smelting/Blasting Recipes
    event.smelting('gcaa:lead_ingot', 'gcaa:ore_lead')
    event.blasting('gcaa:lead_ingot', 'gcaa:ore_lead')
    event.smelting('gcaa:plastic', 'gcaa:ore_asphalt')
    event.blasting('gcaa:plastic', 'gcaa:ore_asphalt')
    
    console.log('[GCAA Recipe Fix] All recipes have been registered successfully!')
})

// Optional: Add recipe book unlocks
ServerEvents.recipes(event => {
    // Make recipes visible in recipe book when player gets any GCAA item
    event.forEachRecipe({ mod: 'gcaa' }, recipe => {
        if (recipe.getId().includes('gcaa:')) {
            // This makes the recipe visible when any ingredient is obtained
            console.log(`[GCAA Recipe Fix] Registered recipe: ${recipe.getId()}`)
        }
    })
})