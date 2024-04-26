package com.amber.amberutils.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RecipeModifier {

    public static void overrideRecipeOutput() {
        // Get the piston crafting recipe
        IRecipe pistonRecipe = ForgeRegistries.RECIPES.getValue(new ResourceLocation("minecraft:piston"));

        // Check if it's a shaped recipe (crafting table recipe)
        if (pistonRecipe instanceof ShapedRecipes) {
            ShapedRecipes shapedRecipe = (ShapedRecipes) pistonRecipe;

            // Define the output item (e.g., dirt block)
            ItemStack output = new ItemStack(Blocks.AIR);

            // Create a new ShapedRecipes instance with the modified output
            ShapedRecipes newRecipe = new ShapedRecipes("custom_piston", shapedRecipe.recipeWidth, shapedRecipe.recipeHeight, shapedRecipe.recipeItems, output);

            // Replace the original recipe with the new one
            ForgeRegistries.RECIPES.register(newRecipe.setRegistryName("minecraft:piston"));
        }
    }
}
