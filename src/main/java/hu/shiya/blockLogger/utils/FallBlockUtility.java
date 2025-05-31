package hu.shiya.blockLogger.utils;

import org.bukkit.Material;

import java.util.Set;

public class FallBlockUtility {

    private static final Set<Material> FALLABLE_BLOCKS = Set.of(
            Material.SAND,
            Material.GRAVEL,
            Material.RED_SAND,
            Material.ANVIL,
            Material.CHIPPED_ANVIL,
            Material.DAMAGED_ANVIL,
            Material.DRAGON_EGG,
            Material.WHITE_CONCRETE_POWDER,
            Material.ORANGE_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.YELLOW_CONCRETE_POWDER,
            Material.LIME_CONCRETE_POWDER,
            Material.PINK_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.CYAN_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER,
            Material.BROWN_CONCRETE_POWDER,
            Material.GREEN_CONCRETE_POWDER,
            Material.RED_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER
    );
    public static boolean isFallableBlock(final Material material) {
        return FALLABLE_BLOCKS.contains(material);
    }
}
