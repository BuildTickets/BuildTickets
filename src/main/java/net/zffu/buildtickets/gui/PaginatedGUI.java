package net.zffu.buildtickets.gui;

import dev.triumphteam.gui.guis.GuiItem;
import net.zffu.buildtickets.locale.LocaleManager;
import net.zffu.buildtickets.locale.LocaleString;
import net.zffu.buildtickets.utils.Bundle;
import net.zffu.buildtickets.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PaginatedGUI<T extends ItemConvertible> extends AbstractGUI {

    public static ItemStack BACK = null;
    public static ItemStack GO_BACK = null;
    public static ItemStack GO_NEXT = null;
    protected int page;
    protected int elementsPerPage;
    protected int startingIndex;
    protected int startingSlotIndex = 0;
    protected int elementsPerLine;

    protected Bundle<String, Comparator<T>>[] sortingOptions = null;
    protected int sortingSlot = 47;
    private int selectedFilter = -1;

    public PaginatedGUI(String inventoryName, int page, int elementsPerPage) {
        super(inventoryName);
        this.page = page;
        this.elementsPerPage = elementsPerPage;
        this.startingIndex = elementsPerPage * page;
    }

    @Override
    public void initItems() {
        if(this.sortingOptions != null) {
            ItemBuilder builder = ItemBuilder.create(Material.ANVIL);
            builder = builder.display("§aSorting");
            for (int i = 0; i < this.sortingOptions.length; i++) {
                builder = builder.lore((this.selectedFilter == i) ? "§2► " + this.sortingOptions[i].getFirst() : "  §7" + this.sortingOptions[i].getFirst());
            }

            builder = builder.lore("§7", "§eLeft-Click to go forward", "§eRight-Click to go backwards", "§eShift-Click to remove filter");

            this.gui.setItem(this.sortingSlot, new GuiItem(builder.build()));

            setAction(this.sortingSlot, (event -> {
                event.setCancelled(true);

                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    this.selectedFilter = -1;
                }

                if (event.getClick() == ClickType.LEFT) {
                    if (this.selectedFilter == -1 || (this.selectedFilter + 1) >= (this.sortingOptions.length)) {
                        this.selectedFilter = 0;
                    } else {
                        this.selectedFilter++;
                    }
                }

                if (event.getClick() == ClickType.RIGHT) {
                    this.selectedFilter--;
                    if (this.selectedFilter == -1) {
                        this.selectedFilter = this.sortingOptions.length - 1;
                    }
                }

                this.initItems();
                this.gui.update();
            }));
        }

        List<ItemStack> stacks = getItems();
        int rowIndex = 0;
        for(int i = startingIndex; i < startingIndex + elementsPerPage; i++) {
            if(stacks.size() <= i) return;
            rowIndex++;
            if(rowIndex >= elementsPerLine && elementsPerLine > 0) {
                rowIndex = 0;
                i += (9 - elementsPerLine);
            }
            this.gui.setItem(i - startingIndex, new GuiItem(stacks.get(i)));
        }
    }

    /**
     * Returns the elements to display in the Paginated GUIS. T must be a {@link ItemConvertible}
     * @return
     */
    public abstract List<T> getElements();

    /**
     * Parses the elements into items.
     * @return
     */
    private List<ItemStack> getItems() {
        List<T> elems = this.getElements();
        if(this.selectedFilter != -1) {
            elems.sort(this.sortingOptions[this.selectedFilter].getSecond());
        }
        return elems.stream().map(ItemConvertible::toItemStack).collect(Collectors.toList());
    }

    static {
        BACK = ItemBuilder.create(Material.ARROW).display("§aGo Back").lore("§7Click here to go back!").build();
        GO_BACK = ItemBuilder.create(Material.ARROW).display("§aPrevious Page").lore("§7Click here to go back to the previous page!").build();
        GO_NEXT = ItemBuilder.create(Material.ARROW).display("§aNext Page").lore("§7Click here to go to the next page!").build();
    }

    public void goBack(HumanEntity player) {
        if(this.page <= 0) {
            player.sendMessage(LocaleManager.getMessage(LocaleString.PAGE_ALREADY_FIRST, player));
            return;
        }
        this.page--;
        this.startingIndex = elementsPerPage * page;
        this.open(player);
    }

    public void goNext(HumanEntity player) {
        if(this.getElements().size() <= (this.page + 1) * elementsPerPage) {
            player.sendMessage(LocaleManager.getMessage(LocaleString.PAGE_ALREADY_LAST, player));
            return;
        }
        this.page++;
        this.startingIndex = elementsPerPage * page;
        this.open(player);
    }

}
