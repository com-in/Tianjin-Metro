package ziyue.tjmetro.mapping;

import net.neoforged.fml.ModList;

/**
 * @author ZiYueCommentary
 * @since 1.0.0-beta-5
 */

public interface ModLoaderHelper
{
    static boolean hasClothConfig() {
        return ModList.get().isLoaded("cloth_config") || ModList.get().isLoaded("cloth-config");
    }

    /**
     * The filters mod is an optional dependency. Anything referencing {@code ziyue.filters.*} must be
     * guarded by this, otherwise the missing classes raise a NoClassDefFoundError (an Error, so it
     * cannot be caught as an Exception) while the mod is being constructed.
     */
    static boolean hasFilters() {
        return ModList.get().isLoaded("filters");
    }
}
