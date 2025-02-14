package com.github.lukesky19.skylib.gui.abstracts;

import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.gui.GUIButton;
import com.github.lukesky19.skylib.gui.GUIType;
import com.github.lukesky19.skylib.gui.interfaces.ButtonGUI;
import com.github.lukesky19.skylib.version.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class can be extended to create a GUI based on the Chest UI.
 * Make sure to run {@link #createInventory(Player, GUIType, String, Location)} and to override
 * methods like {@link #update()}, {@link #handleClose(InventoryCloseEvent)}, and {@link #closeInventory(Plugin, Player)}.
 * Your plugins need to track open GUIs and handle passing all the events to the GUIs.
 * See any of my plugins like SkyShop or SkyMarket for example implementations.
 */
public abstract class ChestGUI implements ButtonGUI {
    /**
     * This hashmap maps slots (as an Integer) to a GUIButton.
     */
    HashMap<Integer, GUIButton> slotButtons = new HashMap<>();
    private InventoryView view; // >= 1.21.4 only
    private Inventory inventory; // All versions

    /**
     * Creates a GUI for the given inventory type.
     * On versions newer or equal to 1.21.4, the MenuType API will be used.
     * On version older than or equal to 1.21.3, legacy methods will be used.
     * @param player The player to create this Gui for.
     * @param type The type of Gui.
     * @param name The name of the Gui.
     * @param location Optional. The location of the Container to create an InventoryView for.
     *                 NOTE: Only available on version >= 1.21.4 and for MenuTypes that use
     *                 {@link LocationInventoryViewBuilder}
     * @throws RuntimeException if the GUIType is not a valid type.
     */
    @Override
    public void createInventory(@NotNull Player player, @NotNull GUIType type, @NotNull String name, @Nullable Location location) {
        if(VersionUtil.getMajorVersion() > 21 || (VersionUtil.getMajorVersion() == 21 && VersionUtil.getMinorVersion() >= 4)) {
            switch(type) {
                case CHEST_9, CHEST_18, CHEST_27, CHEST_36, CHEST_45, CHEST_54 -> {
                    if(type.getMenuType().typed().builder() instanceof LocationInventoryViewBuilder<@NotNull InventoryView> builder) {
                        builder.title(FormatUtil.format(name));

                        builder.checkReachable(false);

                        // There is a weird quirk with the MenuType API where if a location is not provided, it will use the block at the player's feet.
                        // This can cause the inventory to be copied to the block's inventory in the world.
                        // To fix this, we just set the location to a location outside the world's buildable bounds. (i.e., a high y level such as y: 500)
                        /* There is some performance costs doing this, see the */ /**{@link LocationInventoryViewBuilder#location(Location)}*/ /* for more information */
                        // Relevant PRs for fixes:
                        // https://github.com/PaperMC/Paper/pull/12013
                        // https://github.com/PaperMC/Paper/pull/12055
                        builder.location(Objects.requireNonNullElseGet(location, () -> new Location(player.getWorld(), player.getX(), player.getY() + 500, player.getZ())));

                        view = builder.build(player);
                    } else {
                        InventoryViewBuilder<@NotNull InventoryView> builder = type.getMenuType().typed().builder();

                        builder.title(FormatUtil.format(name));

                        view = builder.build(player);
                    }
                }

                case null, default -> throw new RuntimeException("Unsupported GUIType.");
            }
        } else {
            switch(type) {
                case CHEST_9 -> inventory = Bukkit.createInventory(null, 9, FormatUtil.format(name));

                case CHEST_18 -> inventory = Bukkit.createInventory(null, 18, FormatUtil.format(name));

                case CHEST_27 -> inventory = Bukkit.createInventory(null, 27, FormatUtil.format(name));

                case CHEST_36 -> inventory = Bukkit.createInventory(null, 36, FormatUtil.format(name));

                case CHEST_45 -> inventory = Bukkit.createInventory(null, 45, FormatUtil.format(name));

                case CHEST_54 -> inventory = Bukkit.createInventory(null, 54, FormatUtil.format(name));

                case null, default -> throw new RuntimeException("Unsupported GUIType.");
            }
        }
    }

    /**
     * Get the inventory for this GUI.
     * @return An Inventory.
     * @throws RuntimeException if Inventory or InventoryView is null.
     */
    @NotNull
    public Inventory getInventory() {
        if(inventory != null) {
            return inventory;
        } else if(view != null) {
            return view.getTopInventory();
        } else {
            throw new RuntimeException("Unable to get Inventory due to a null Inventory or InventoryView. Did you run createInventory?");
        }
    }

    /**
     * This will be null if used on a server on version 1.21.3 or older.
     * Use {@link #getInventory()} instead.
     * @return An InventoryView.
     */
    @CheckForNull
    public InventoryView getInventoryView() {
        return view;
    }

    /**
     * Clears the GUI of all buttons and then clears the slot-button mapping.
     */
    public void clearButtons() {
        ItemStack clearStack = new ItemStack(Material.AIR);

        if(getInventoryView() != null) {
            InventoryView view = getInventoryView();

            int guiSize = getInventory().getSize() - 1;
            for(int slot = 0; slot <= guiSize; slot++) {
                if(getButtonMapping().containsKey(slot)) {
                    view.setItem(slot, clearStack);
                }
            }
        } else {
            Inventory inventory = getInventory();
            int guiSize = getInventory().getSize() - 1;
            for(int slot = 0; slot <= guiSize; slot++) {
                if(getButtonMapping().containsKey(slot)) {
                    inventory.setItem(slot, clearStack);
                }
            }
        }

        slotButtons.clear();
    }

    /**
     * Clears the inventory of all items, regardless if it is a button or not.
     */
    public void clearInventory()  {
        ItemStack clearStack = new ItemStack(Material.AIR);
        int guiSize = getInventory().getSize() - 1;

        if(getInventoryView() != null) {
            InventoryView view = getInventoryView();

            for(int slot = 0; slot <= guiSize; slot++) {
                view.setItem(slot, clearStack);
            }
        } else {
            Inventory inventory = getInventory();

            for(int slot = 0; slot <= guiSize; slot++) {
                inventory.setItem(slot, clearStack);
            }
        }

        slotButtons.clear();
    }

    /**
     * Get a HashMap of all slots mapped to buttons.
     * @return A HashMap of all slots mapped to buttons.
     */
    @NotNull
    public Map<Integer, GUIButton> getButtonMapping() {
        return slotButtons;
    }

    /**
     * Maps a GUIButton to a slot.
     * To actually add the buttons to the Inventory, you must call {@link #update()}
     * @param slot The slot to map the GUIButton to.
     * @param button The GUIButton for the given slot.
     * @throws RuntimeException if you try to map a button to a slot outside the Inventory bounds.
     */
    public void setButton(int slot, @NotNull GUIButton button) {
        Inventory inventory = getInventory();

        if(slot < 0 || slot >= inventory.getSize()) {
            throw new RuntimeException("Provided slot is outside of inventory bounds. Slot must be greater than 0 and less than " + inventory.getSize());
        }

        slotButtons.put(slot, button);
    }

    /**
     * Takes a HashMap of representing a collection of slots and GUIButtons and replaces the existing mapping.
     * @param buttonMap A HashMap containing a mapping of slots to GUIButtons.
     */
    public void setButtons(HashMap<Integer, GUIButton> buttonMap) {
        clearButtons();

        slotButtons.putAll(buttonMap);
    }
}
