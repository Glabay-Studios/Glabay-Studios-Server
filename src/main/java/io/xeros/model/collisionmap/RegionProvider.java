package io.xeros.model.collisionmap;

import io.xeros.content.commands.owner.Clipping;
import io.xeros.content.skills.hunter.impling.Impling;
import io.xeros.content.skills.hunter.trap.impl.BirdSnare;
import io.xeros.content.skills.hunter.trap.impl.BoxTrap;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Arrays;

public class RegionProvider {

    /**
     * A flag that marks a tile as occupied by a player or an NPC
     */
    public static final int NPC_TILE_FLAG = 0x80000000;

    /**
     * A flag that marks a tile as fully occupied blocking both projectile and entity paths
     */
    public static final int FULL_NPC_TILE_FLAG = 0x100000;

    /**
     * An array of fully blocked NPCs that block movement and projectiles
     */
    public static final int[] FULL_BLOCKED_NPCS = {
            Npcs.GORILLA, Npcs.BEARDED_GORILLA, Npcs.ELDER_GUARD, Npcs.ELDER_GUARD_2,
            7710,// ROCKY SUPPORT
    };

    /**
     * An array of NPC's that neither add or remove NPC clipping
     */
    public static int[] EXCLUDED_NPCS = {
            // Misc
            Npcs.SKOTIZO, Npcs.SMOKE_DEVIL, Npcs.SMOKE_DEVIL_2,
            Npcs.SMOKE_DEVIL_3, Npcs.SMOKE_DEVIL_4, Npcs.SMOKE_DEVIL_5,
            Npcs.JAL_NIB,

            // Mimic
            Npcs.THE_MIMIC, Npcs.ETHEREAL_MIMIC, Npcs.THE_MIMIC_2,
            // End of Mimic

            // Dagannoths
            Npcs.DAGANNOTH_REX, Npcs.DAGANNOTH_PRIME, Npcs.DAGANNOTH_SUPREME,
            // End of Dagannoths

            // Grotesque Guardians
            Npcs.DUSK_2, Npcs.DAWN_2,
            // End of Grotesque Guardians

            // Mage Arena 2 boosses
            Npcs.PORAZDIR, Npcs.PORAZDIR_2,
            Npcs.JUSTICIAR_ZACHARIAH, Npcs.JUSTICIAR_ZACHARIAH_2,
            Npcs.DERWEN, Npcs.DERWEN_2, Npcs.DERWEN_3,
            // End of Mage Arena 2 bosses

            // COX
            Npcs.ICE_DEMON, Npcs.ICE_DEMON_2,
            Npcs.LIZARDMAN_SHAMAN, Npcs.LIZARDMAN_SHAMAN_2, Npcs.LIZARDMAN_SHAMAN_3,
            Npcs.LIZARDMAN_SHAMAN_4, Npcs.LIZARDMAN_SHAMAN_5, Npcs.LIZARDMAN_SHAMAN_6, Npcs.LIZARDMAN_SHAMAN_7,
            Npcs.MUTTADILE, Npcs.MUTTADILE_2, Npcs.MUTTADILE_3,
            Npcs.SKELETAL_MYSTIC, Npcs.SKELETAL_MYSTIC_2, Npcs.SKELETAL_MYSTIC_3,
            Npcs.TEKTON, Npcs.TEKTON_2, Npcs.TEKTON_3,
            Npcs.TEKTON_4, Npcs.TEKTON_ENRAGED, Npcs.TEKTON_ENRAGED_2,
            Npcs.VANGUARD, Npcs.VANGUARD_2, Npcs.VANGUARD_3,
            Npcs.VANGUARD_4, Npcs.VANGUARD_5, Npcs.VANGUARD_6, Npcs.VANGUARD_7,
            Npcs.VASA_NISTIRIO, Npcs.VASA_NISTIRIO_2,
            Npcs.VESPULA, Npcs.VESPULA_2, Npcs.VESPULA_3,
            Npcs.GREAT_OLM, Npcs.GREAT_OLM_2, Npcs.GREAT_OLM_LEFT_CLAW,
            Npcs.GREAT_OLM_RIGHT_CLAW, Npcs.GREAT_OLM_LEFT_CLAW_2, Npcs.GREAT_OLM_RIGHT_CLAW_2,
            // END OF COX

            // GWD
            // Bandos
            Npcs.GENERAL_GRAARDOR, Npcs.GENERAL_GRAARDOR_2,
            Npcs.SERGEANT_STRONGSTACK, Npcs.SERGEANT_STEELWILL, Npcs.SERGEANT_GRIMSPIKE,
            // Zamorak
            Npcs.KRIL_TSUTSAROTH, Npcs.KRIL_TSUTSAROTH_2,
            Npcs.TSTANON_KARLAK, Npcs.ZAKLN_GRITCH, Npcs.BALFRUG_KREEYATH,
            // Saradomin
            Npcs.COMMANDER_ZILYANA, Npcs.COMMANDER_ZILYANA_2,
            Npcs.GROWLER, Npcs.STARLIGHT, Npcs.BREE,
            // Armadyl
            Npcs.KREEARRA, Npcs.KREEARRA_2,
            Npcs.WINGMAN_SKREE, Npcs.FLOCKLEADER_GEERIN, Npcs.FLIGHT_KILISA,
            // END OF GWD

            // Pest Control
            Npcs.RAVAGER, Npcs.RAVAGER_2, Npcs.RAVAGER_3,
            Npcs.RAVAGER_4, Npcs.RAVAGER_5,
            Npcs.SPINNER, Npcs.SPINNER_2, Npcs.SPINNER_3,
            Npcs.SPINNER_4, Npcs.SPINNER_5,
            Npcs.SPLATTER, Npcs.SPLATTER_2, Npcs.SPLATTER_3,
            Npcs.SPLATTER_4, Npcs.SPLATTER_5,
            // End of Pest Control

            // Warriors Guild
            Npcs.ANIMATED_IRON_ARMOUR, Npcs.ANIMATED_STEEL_ARMOUR,
            Npcs.ANIMATED_STEEL_ARMOUR_2, Npcs.ANIMATED_BLACK_ARMOUR,
            Npcs.ANIMATED_MITHRIL_ARMOUR, Npcs.ANIMATED_ADAMANT_ARMOUR,
            Npcs.ANIMATED_RUNE_ARMOUR,
            // End of Warriors Guild

            // KQueen
            Npcs.KALPHITE_QUEEN, Npcs.KALPHITE_QUEEN_2, Npcs.KALPHITE_QUEEN_3,
            Npcs.KALPHITE_QUEEN_4, Npcs.KALPHITE_QUEEN_5, Npcs.KALPHITE_QUEEN_6,
            Npcs.KALPHITE_QUEEN_7,
            // End of KQueen

            // Wilderness Bosses
            Npcs.CALLISTO, Npcs.CALLISTO_2,
            Npcs.CHAOS_ELEMENTAL, Npcs.CHAOS_ELEMENTAL_2,
            Npcs.CHAOS_FANATIC, Npcs.CRAZY_ARCHAEOLOGIST,
            Npcs.KING_BLACK_DRAGON, Npcs.KING_BLACK_DRAGON_2, Npcs.KING_BLACK_DRAGON_3,
            Npcs.SCORPIA, Npcs.VENENATIS,
            Npcs.VETION, Npcs.VETION_REBORN,
            // End oof Wilderness Bosses

            // Barrows Brothers
            Npcs.DHAROK_THE_WRETCHED, Npcs.AHRIM_THE_BLIGHTED, Npcs.GUTHAN_THE_INFESTED,
            Npcs.TORAG_THE_CORRUPTED, Npcs.KARIL_THE_TAINTED, Npcs.VERAC_THE_DEFILED
    };

    /**
     * Determines if an NPC is an excluded NPC for clipping
     *
     * @param npc The npc that is being checked
     * @return True if the NPC is excluded
     */
    public static boolean isExcludedNpc(NPC npc) {
        int npcId = npc.getNpcId();

        // Hunter checks
        if (BoxTrap.NPC_IDS.contains(npcId))
            return true;
        else if (BirdSnare.NPC_IDS.contains(npc.getNpcId()))
            return true;
        else if (Impling.isImp(npcId))
            return true;

        return Arrays.stream(EXCLUDED_NPCS).anyMatch(i -> i == npcId);
    }

    /**
     * Determines if an NPC fully blocks a tile
     *
     * @param npc The npc to check
     * @return True if the NPC fully blocks a tile
     */
    public static boolean isFullyBlockedNpc(NPC npc) {
        return Arrays.stream(FULL_BLOCKED_NPCS).anyMatch(i -> i == npc.getNpcId());
    }

    private static final RegionProvider GLOBAL = new RegionProvider();

    public static RegionProvider getGlobal() {
        return GLOBAL;
    }

    private final Int2ObjectMap<Region> regions = new Int2ObjectOpenHashMap<>();

    public RegionProvider() {
    }

    public Region get(int x, int y) {
        if (regions.get(getHash(x, y)) == null) {
            Region region = new Region(this, getHash(x, y), false);
            regions.put(getHash(x, y), region);
            return region;
        } else {
            return regions.get(getHash(x, y));
        }
    }

    public boolean contains(int x, int y) {
        return regions.get(getHash(x, y)) != null;
    }

    public int getHash(int x, int y) {
        int regionX = x >> 3;
        int regionY = y >> 3;
        return ((regionX / 8) << 8) + (regionY / 8);
    }

    public void add(Region region) {
        regions.put(region.id(), region);
    }

    public int getClipping(int x, int y, int height) {
        if (height > 3)
            height %= 4;
        Region r = get(x, y);
        if (r != null) {
            return r.getClip(x, y, height);
        }
        return 0;
    }

    public void addClipping(int clipping, int x, int y, int height) {
        Region r = get(x, y);
        if (r != null) {
            r.addClip(x, y, height, clipping);
        }
    }

    public void addNpcClipping(NPC npc) {
        if (isExcludedNpc(npc)) return;
        int flag = isFullyBlockedNpc(npc) ? FULL_NPC_TILE_FLAG : NPC_TILE_FLAG;
        for (int x = 0; x < npc.getSize(); x++) {
            for (int y = 0; y < npc.getSize(); y++) {
                addNpcClipping(flag, npc.getX() + x, npc.getY() + y, npc.getHeight());
            }
        }
    }

    public void removeNpcClipping(NPC npc) {
        if (isExcludedNpc(npc)) return;
        int flag = isFullyBlockedNpc(npc) ? FULL_NPC_TILE_FLAG : NPC_TILE_FLAG;
        for (int x = 0; x < npc.getSize(); x++) {
            for (int y = 0; y < npc.getSize(); y++) {
                removeNpcClipping(flag, npc.getX() + x, npc.getY() + y, npc.getHeight());
            }
        }
    }

    public void addNpcClipping(int flag, int x, int y, int height) {
        // Blocks clipping from being added again to already clipped NPC tiles
        if (isOccupiedByNpc(x, y, height))
            return;

        if (Boundary.isIn(new Position(x, y, height), Boundary.CATACOMBS)
                || Boundary.isIn(new Position(x, y, height), Boundary.CRYSTAL_CAVE_AREA))
            return;

        if (flag > -1) {
            addClipping(flag, x, y, height);
        }
    }

    public void removeNpcClipping(int flag, int x, int y, int height) {
        // Blocks clipping from being added again to already clipped NPC tiles
        if (!isOccupiedByNpc(x, y, height) && !isOccupiedByFullBlockNpc(x, y, height))
            return;

        if (Boundary.isIn(new Position(x, y, height), Boundary.CATACOMBS)
                || Boundary.isIn(new Position(x, y, height), Boundary.CRYSTAL_CAVE_AREA))
            return;

        Region r = get(x, y);
        if (r != null) {
            r.removeClip(x, y, height, -flag);
        }
    }

    public boolean getClipping(int x, int y, int height, int moveTypeX, int moveTypeY) {
        try {
            if (height > 3)
                height %= 4;
            int checkX = (x + moveTypeX);
            int checkY = (y + moveTypeY);
            if (moveTypeX == -1 && moveTypeY == 0)
                return (getClipping(x, y, height) & 0x1280108) == 0;
            else if (moveTypeX == 1 && moveTypeY == 0)
                return (getClipping(x, y, height) & 0x1280180) == 0;
            else if (moveTypeX == 0 && moveTypeY == -1)
                return (getClipping(x, y, height) & 0x1280102) == 0;
            else if (moveTypeX == 0 && moveTypeY == 1)
                return (getClipping(x, y, height) & 0x1280120) == 0;
            else if (moveTypeX == -1 && moveTypeY == -1)
                return ((getClipping(x, y, height) & 0x128010e) == 0 && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0
                        && (getClipping(checkX - 1, checkY, height) & 0x1280102) == 0);
            else if (moveTypeX == 1 && moveTypeY == -1)
                return ((getClipping(x, y, height) & 0x1280183) == 0 && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0
                        && (getClipping(checkX, checkY - 1, height) & 0x1280102) == 0);
            else if (moveTypeX == -1 && moveTypeY == 1)
                return ((getClipping(x, y, height) & 0x1280138) == 0 && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0
                        && (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
            else if (moveTypeX == 1 && moveTypeY == 1)
                return ((getClipping(x, y, height) & 0x12801e0) == 0 && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0
                        && (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
            else {
                // System.out.println("[FATAL ERROR]: At getClipping: " + x + ", "
                // + y + ", " + height + ", " + moveTypeX + ", "
                // + moveTypeY);
                return false;
            }
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Determines if the specific position is blocked by clipping
     *
     * @param position The position to check
     * @return True if clipping is blocked
     */
    public boolean isBlocked(Position position) {
        return isBlocked(position.getX(), position.getY(), position.getHeight());
    }

    /**
     * Determines if this tile is occupied by an npc
     *
     * @param x X coord
     * @param y Y coord
     * @param z Z coord
     * @return True if the tile is blocked by an npc
     */
    public boolean isOccupiedByNpc(int x, int y, int z) {
        return (getClipping(x, y, z) & NPC_TILE_FLAG) != 0;
    }

    /**
     * Determines if this tile is occupied by an npc
     *
     * @param x X coord
     * @param y Y coord
     * @param z Z coord
     * @return True if the tile is blocked by an npc
     */
    public boolean isOccupiedByNpc(int size, int x, int y, int z) {
        for (int xOffset = 0; xOffset < size; xOffset++) {
            for (int yOffset = 0; yOffset < size; yOffset++) {
                if (isOccupiedByNpc(x + xOffset, y + yOffset, z))
                    return true;
            }
        }
        return false;
    }

    /**
     * Determines if this tile is completely blocked by an npc
     *
     * @param x The xCoordinate
     * @param y The yCoordinate
     * @param z The zCoordinate
     * @return True if this tile is completely blocked by an npc
     */
    public boolean isOccupiedByFullBlockNpc(int x, int y, int z) {
        return (getClipping(x, y, z) & FULL_NPC_TILE_FLAG) != 0;
    }

    /**
     * Determines if a specific coordinate is blocked by clipping
     *
     * @param x The x coordinate to check
     * @param y The y coordinate to check
     * @param z The height coordinate to check
     * @return True if clipping is blocked
     */
    public boolean isBlocked(int x, int y, int z) {
        return (getClipping(x, y, z) & 0x1280120) != 0;
    }

    public boolean hasClipping(int x, int y, int z) {
        return getClipping(x, y, z) != 0;
    }

    public boolean blockedNorth(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x, y + 1, z)
                || isOccupiedByFullBlockNpc(x, y + 1, z)
                || ((getClipping(x, y + 1, z) & 0x1280120) != 0);
    }

    public boolean blockedEast(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x + 1, y, z)
                || isOccupiedByFullBlockNpc(x + 1, y, z)
                || ((getClipping(x + 1, y, z) & 0x1280180) != 0);
    }

    public boolean blockedSouth(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x, y - 1, z)
                || isOccupiedByFullBlockNpc(x, y - 1, z)
                || ((getClipping(x, y - 1, z) & 0x1280102) != 0);
    }

    public boolean blockedWest(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x - 1, y, z)
                || isOccupiedByFullBlockNpc(x - 1, y, z)
                || ((getClipping(x - 1, y, z) & 0x1280108) != 0);
    }

    public boolean blockedNorthEast(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x + 1, y + 1, z)
                || isOccupiedByFullBlockNpc(x + 1, y + 1, z)
                || ((getClipping(x + 1, y + 1, z) & 0x12801e0) != 0);
    }

    public boolean blockedNorthWest(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x - 1, y + 1, z)
                || isOccupiedByFullBlockNpc(x - 1, y + 1, z)
                || ((getClipping(x - 1, y + 1, z) & 0x1280138) != 0);
    }

    public boolean blockedSouthEast(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x + 1, y - 1, z)
                || isOccupiedByFullBlockNpc(x + 1, y - 1, z)
                || ((getClipping(x + 1, y - 1, z) & 0x1280183) != 0);
    }

    public boolean blockedSouthWest(int x, int y, int z, boolean isNpc) {
        return isNpc && isOccupiedByNpc(x - 1, y - 1, z)
                || isOccupiedByFullBlockNpc(x - 1, y - 1, z)
                || ((getClipping(x - 1, y - 1, z) & 0x128010e) != 0);
    }

    public boolean canMove(int x, int y, int z, int direction) {
        return canMove(x, y, z, direction, false);
    }

    public boolean canMove(int x, int y, int z, int direction, boolean isNpc) {
        if (direction == 0) {
            return !blockedNorthWest(x, y, z, isNpc) && !blockedNorth(x, y, z, isNpc)
                    && !blockedWest(x, y, z, isNpc);
        } else if (direction == 1) {
            return !blockedNorth(x, y, z, isNpc);
        } else if (direction == 2) {
            return !blockedNorthEast(x, y, z, isNpc) && !blockedNorth(x, y, z, isNpc)
                    && !blockedEast(x, y, z, isNpc);
        } else if (direction == 3) {
            return !blockedWest(x, y, z, isNpc);
        } else if (direction == 4) {
            return !blockedEast(x, y, z, isNpc);
        } else if (direction == 5) {
            return !blockedSouthWest(x, y, z, isNpc) && !blockedSouth(x, y, z, isNpc)
                    && !blockedWest(x, y, z, isNpc);
        } else if (direction == 6) {
            return !blockedSouth(x, y, z, isNpc);
        } else if (direction == 7) {
            return !blockedSouthEast(x, y, z, isNpc) && !blockedSouth(x, y, z, isNpc)
                    && !blockedEast(x, y, z, isNpc);
        }
        return false;
    }

    final int[] directional_flag = new int[]{0x20000 | 0x400, 0x20000 | 0x1000, 0x20000 | 0x4000, 0x20000 | 0x10000};

    public boolean canShoot(int x, int y, int z, int direction) {
		if (direction == 0) {
			return !projectileBlockedNorthWest(x, y, z) && !projectileBlockedNorth(x, y, z)
					&& !projectileBlockedWest(x, y, z);
		} else if (direction == 1) {
			return !projectileBlockedNorth(x, y, z);
		} else if (direction == 2) {
			return !projectileBlockedNorthEast(x, y, z) && !projectileBlockedNorth(x, y, z)
					&& !projectileBlockedEast(x, y, z);
		} else if (direction == 3) {
			return !projectileBlockedWest(x, y, z);
		} else if (direction == 4) {
			return !projectileBlockedEast(x, y, z);
		} else if (direction == 5) {
			return !projectileBlockedSouthWest(x, y, z) && !projectileBlockedSouth(x, y, z)
					&& !projectileBlockedWest(x, y, z);
		} else if (direction == 6) {
			return !projectileBlockedSouth(x, y, z);
		} else if (direction == 7) {
			return !projectileBlockedSouthEast(x, y, z) && !projectileBlockedSouth(x, y, z)
					&& !projectileBlockedEast(x, y, z);
		}
        return false;
    }

    public int getProjectileClipping(int x, int y, int height) {
        if (height > 3)
            height %= 4;
        Region r = get(x, y);
        if (r != null) {
            return r.getProjectileClip(x, y, height);
        }
        return 0;
    }

    public boolean projectileBlockedNorth(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x, y + 1, z)
                || (getProjectileClipping(x, y + 1, z) & 19398944) != 0;
    }

    public boolean projectileBlockedEast(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x + 1, y, z)
                || (getProjectileClipping(x + 1, y, z) & 0x1280180) != 0;
    }

    public boolean projectileBlockedSouth(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x, y - 1, z)
                || (getProjectileClipping(x, y - 1, z) & 0x1280102) != 0;
    }

    public boolean projectileBlockedWest(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x - 1, y, z)
                || (getProjectileClipping(x - 1, y, z) & 0x1280108) != 0;
    }

    public boolean projectileBlockedNorthEast(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x + 1, y + 1, z)
                || (getProjectileClipping(x + 1, y + 1, z) & 0x12801e0) != 0;
    }

    public boolean projectileBlockedNorthWest(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x - 1, y + 1, z)
                || (getProjectileClipping(x - 1, y + 1, z) & 0x1280138) != 0;
    }

    public boolean projectileBlockedSouthEast(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x + 1, y - 1, z)
                || (getProjectileClipping(x + 1, y - 1, z) & 0x1280183) != 0;
    }

    public boolean projectileBlockedSouthWest(int x, int y, int z) {
        return isOccupiedByFullBlockNpc(x - 1, y - 1, z)
                || (getProjectileClipping(x - 1, y - 1, z) & 0x128010e) != 0;
    }
}
