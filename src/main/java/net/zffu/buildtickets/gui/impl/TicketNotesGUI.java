package net.zffu.buildtickets.gui.impl;

import dev.triumphteam.gui.guis.GuiItem;
import net.zffu.buildtickets.BuildTicketsPlugin;
import net.zffu.buildtickets.gui.ItemConvertible;
import net.zffu.buildtickets.gui.PaginatedGUI;
import net.zffu.buildtickets.tickets.BuildTicket;
import net.zffu.buildtickets.utils.HeadUtils;
import net.zffu.buildtickets.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class TicketNotesGUI extends PaginatedGUI<TicketNotesGUI.Notes> {
    private BuildTicket ticket;

    public TicketNotesGUI(BuildTicket ticket, int page) {
        super("Notes (Page " + page + ")", page, 35);
        this.ticket = ticket;
        this.page = page;
    }

    @Override
    public void initItems() {
        super.initItems();
        gui.setItem(53, new GuiItem(ItemBuilder.create(Material.PAPER).display("§aCreate a note").lore("§7Create or edit your note of this", "§7ticket.", "", "§eClick here to send / edit your note!").build()));

        setAction(53, (event -> {
            BuildTicketsPlugin.getInstance().doChatHandler(event.getWhoClicked(), (chat) -> {
                chat.setCancelled(true);
                ticket.sendNote(chat.getPlayer(), chat.getMessage());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        open(chat.getPlayer());
                    }
                }.runTask(BuildTicketsPlugin.getInstance());
            });
        }));

        gui.setItem(48, new GuiItem(GO_BACK));
        gui.setItem(50, new GuiItem(GO_NEXT));

        setAction(48, (event -> {
            goBack(event.getWhoClicked());
        }));

        setAction(50, (event -> {
            goNext(event.getWhoClicked());
        }));
    }

    @Override
    public List<Notes> getElements() {
        return ticket.getNotes().entrySet().stream().map(entry -> new Notes(entry)).collect(Collectors.toList());
    }

    @Override
    public boolean setDefaultClickActions() {
        return false;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {}

    public class Notes implements ItemConvertible {

        private Map.Entry<UUID, String> entry;

        public Notes(Map.Entry<UUID, String> entry) {
            this.entry = entry;
        }

        @Override
        public ItemStack toItemStack() {
            OfflinePlayer poster = Bukkit.getOfflinePlayer(entry.getKey());
            String note = entry.getValue();

            ItemStack stack = HeadUtils.getHeadStack(entry.getKey());
            return ItemBuilder.create(stack).display("§a" + poster.getName() + "'s Note").lore("§7Note: §f" + note).build();
        }
    }

}
