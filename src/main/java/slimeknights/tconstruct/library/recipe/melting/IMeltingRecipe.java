package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;

public interface IMeltingRecipe extends IRecipe<ISingleItemInventory> {
  /**
   * Gets a safe to modify output stack for this recipe
   * @param inv    Input inventory
   * @param world  World used in crafting
   * @return  Output stack
   */
  default FluidStack getOutput(ISingleItemInventory inv, World world) {
    return getOutput().copy();
  }

  /**
   * Gets the minimum temperatue to melt this item. Doubles as the time
   * @return  Recipe temperature
   */
  int getTemperature();


  /* JEI display */

  /**
   * Gets the recipe output for display, result should not be modified or added to an inventroy
   * @return
   */
  FluidStack getOutput();


  /* Recipe data */

  @Override
  default IRecipeType<?> getType() {
    return RecipeTypes.MELTING;
  }

  @Override
  default boolean isDynamic() {
    return true;
  }


  /* Required methods */

  /** @deprecated unused */
  @Deprecated
  @Override
  default boolean canFit(int width, int height) {
    return true;
  }

  /** @deprecated unsupported */
  @Deprecated
  @Override
  default ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  /** @deprecated unsupported */
  @Deprecated
  @Override
  default ItemStack getCraftingResult(ISingleItemInventory inv) {
    return ItemStack.EMPTY;
  }


  /* Utils */

  double LOG9_2 = 0.31546487678;

  /**
   * Calculates the temperature for a recipe based on the fluid result
   * @param fluid  Fluid result
   * @return  Temperature for the recipe
   */
  static int calcTemperature(FluidStack fluid) {
    int temp = fluid.getFluid().getAttributes().getTemperature(fluid);

    int base = MaterialValues.VALUE_Block;
    int maxTemp = Math.max(0, temp - 300); // we use 0 as baseline, not 300
    double f = (double) fluid.getAmount() / (double) base;

    // we calculate 2^log9(f), which effectively gives us 2^(1 for each multiple of 9)
    // so 1 = 1, 9 = 2, 81 = 4, 1/9 = 1/2, 1/81 = 1/4 etc
    // we simplify it to f^log9(2) to make calculation simpler
    f = Math.pow(f, LOG9_2);

    return 300 + (int) (f * (double) maxTemp);
  }
}
