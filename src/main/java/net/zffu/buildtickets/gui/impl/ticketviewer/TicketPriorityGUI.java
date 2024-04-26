package net.zffu.buildtickets.gui.impl.ticketviewer;

import dev.triumphteam.gui.guis.GuiItem;
import net.zffu.buildtickets.gui.AbstractGUI;
import net.zffu.buildtickets.locale.LocaleManager;
import net.zffu.buildtickets.locale.LocaleString;
import net.zffu.buildtickets.tickets.BuildTicket;
import net.zffu.buildtickets.tickets.TicketPriority;
import net.zffu.buildtickets.utils.ItemBuilder;
import net.zffu.buildtickets.wrappers.WrappedMaterials;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TicketPriorityGUI extends AbstractGUI {

    private static ItemStack[] stacks = new ItemStack[] {WrappedMaterials.DYE_RED, WrappedMaterials.DYE_ORANGE, WrappedMaterials.DYE_YELLOW, WrappedMaterials.DYE_GREEN, WrappedMaterials.DYE_LIME};

    private BuildTicket ticket;

    public TicketPriorityGUI(BuildTicket ticket) {
        super("Change Ticket Priority",3);
        this.ticket = ticket;
        this.backItemSlot = 22;
    }

    @Override
    public void initItems() {
        int index = 11;
        for(TicketPriority priority : TicketPriority.values()) {

            gui.setItem(index, new GuiItem(ItemBuilder.create(stacks[index - 11]).display("§a" + priority.getDisplay()).lore((this.ticket.getPriority() != priority ? "§eClick here to change the priority to the " + priority.getDisplay() : "§cThis is already the priority of the ticket!")).build()));

            index++;
        }
    }

    @Override
    public boolean setDefaultClickActions() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        event.setCancelled(true);

        if(event.getSlot() >= 11 && (event.getSlot() - 11) <= TicketPriority.values().length) {
            int priorityId = event.getSlot() - 11;
            TicketPriority priority = TicketPriority.values()[priorityId];

            if(ticket.getPriority() == priority) {
                event.getWhoClicked().sendMessage(LocaleManager.getMessage(LocaleString.TICKET_PRIORITY_ALREADY, event.getWhoClicked()));
                return;
            }

            ticket.setPriority(priority);
            event.getWhoClicked().sendMessage(LocaleManager.getMessage(LocaleString.TICKET_PRIORITY_CHANGE, event.getWhoClicked()));
            this.initItems();
        }

    }
}
