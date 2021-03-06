package net.eduard.api.lib.menu

import java.util.ArrayList
import java.util.HashMap

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

import net.eduard.api.lib.modules.Mine
import net.eduard.api.lib.manager.EventsManager
import net.eduard.api.lib.game.ClickEffect
import net.eduard.api.lib.kotlin.centralized
import net.eduard.api.lib.modules.Copyable
import net.eduard.api.lib.modules.Extra

/**
 * Sistema proprio de criacao de Menus Interativos automaticos para facilitar
 * sua vida
 *
 * @author Eduard
 */
open class Menu : EventsManager, PagedMenu {
    @Transient
    var superiorMenu: Menu? = null
    var title = "Menu"
    var lineAmount = 1
    var pageAmount = 1
    var pagePrefix = ""
    var pageSuffix = "(\$page/\$max_page)"

    var isTranslateIcon: Boolean = false
    var isAutoAlignItems: Boolean = false
    var isCacheInventories: Boolean = false
    var openWithItem: ItemStack? = Mine.newItem(Material.COMPASS, "§aMenu Exemplo", 1, 0, "§2Clique abrir o menu")
    var openWithCommand: String? = null
    var previousPage = Slot(
            Mine.newItem(Material.ARROW, "§aVoltar Página", 1, 0, "§2Clique para ir para a página anterior"), 5, 3)
    var backPage = Slot(
            Mine.newItem(Material.ARROW, "§aVoltar para Menu Principal", 1, 0, "§2Clique para ir para a página superior"), 1, 2)
    var nextPage = Slot(
            Mine.newItem(Material.ARROW, "§aPróxima Página", 1, 0, "§2Clique para ir para a próxima página"), 9, 2)
    var buttons = mutableListOf<MenuButton>()


    @Transient
    var effect: ClickEffect = MenuButton.NO_ACTION

    @Transient
    private var pagesCache: MutableMap<Int, Inventory> = HashMap()

    @Transient
    private val pageOpened = HashMap<Player, Int>()

    val fullTitle: String
        get() = if (isPageSystem) {
            pagePrefix + title + pageSuffix
        } else title

    val firstEmptySlotOnInventories: Int
        get() {
            var page = 1
            var id = -1

            while (id == -1 && page <= pageAmount)
                id = getFirstEmptySlot(++page)

            return id
        }

    val isFull: Boolean
        get() = isFull(1)

    val isPageSystem: Boolean
        get() = pageAmount > 1

    fun removeButton(name: String) {
        val button = getButton(name) ?: return
        buttons.remove(button)

    }

    fun removeAllButtons() {
        buttons.clear()
        clearCache()
        pageOpened.forEach { (p, a) -> p.closeInventory() }
        pageOpened.clear()
    }

    open fun copy(): Menu {

        return Copyable.copyObject(this)
    }

    fun getButton(icon: ItemStack, player: Player): MenuButton? {
        for (button in buttons) {
            if (button.getIcon(player).isSimilar(icon)) {
                return button
            }
        }

        return null
    }

    fun getButton(name: String): MenuButton? {
        for (button in buttons) {
            if (button.name.equals(name, ignoreCase = true)) {
                return button
            }
        }

        return null
    }

    fun getButton(page: Int, index: Int): MenuButton? {
        for (button in buttons) {
            if (button.index == index && page == button.page)
                return button
        }
        return null
    }

    fun addButton(button: MenuButton) {
        buttons.add(button)
    }


    fun removeButton(button: MenuButton) {
        buttons.remove(button)

    }

    fun updateCache(page: Int) {
        if (isCacheInventories) {
            pagesCache.remove(page)
        }
    }

    constructor() {}

    constructor(title: String, lineAmount: Int) {
        this.title = title
        this.lineAmount = lineAmount

    }

    protected fun getIndex(page: Int, slot: Int): Int {
        return getPageSlot(page, slot)
    }

    fun getPageSlot(page: Int, slot: Int): Int {
        return getMinPageSlot(page) + slot
    }

    fun getMinPageSlot(page: Int): Int {
        return (page - 1) * 9 * lineAmount
    }

    fun getMaxPageSlot(page: Int): Int {
        return page * 9 * lineAmount
    }

    fun clearCache() {
        pagesCache.clear()
    }

    fun getFirstEmptySlot(page: Int): Int {

        for (slot in getMinPageSlot(page) until getMaxPageSlot(page)) {
            val button = getButton(page, slot) ?: return slot
        }

        return -1
    }

    fun isFull(page: Int): Boolean {

        for (slot in getMinPageSlot(page) until getMaxPageSlot(page)) {
            val button = getButton(page, slot) ?: return false

        }
        return true
    }

    override fun open(player: Player, page: Int): Inventory {
        var page = page
        if (page < 1) {
            page = 1
        }
        if (page > pageAmount) {
            page = pageAmount
        }

        if (isCacheInventories && pagesCache.containsKey(page)) {
            player.openInventory(pagesCache[page])
        } else {
            if ((lineAmount < 1) or (lineAmount > 6)) {
                lineAmount = 1
            }
            val minSlot = getMinPageSlot(page)
            val maxSlot = getMaxPageSlot(page)

            val prefix = pagePrefix.replace("\$max_page", "" + pageAmount).replace("\$page", "" + page)
            val suffix = pageSuffix.replace("\$max_page", "" + pageAmount).replace("\$page", "" + page)
            var menuTitle = Extra.cutText(prefix + title + suffix, 32)
            if (!isPageSystem) {
                menuTitle = title
            }
            val menu = Bukkit.createInventory(null, 9 * lineAmount, menuTitle)
            if (isPageSystem) {
                if (page > 1)
                    previousPage.give(menu)
                if (page < pageAmount)
                    nextPage.give(menu)
            }
            if (this.superiorMenu != null) {
                backPage.give(menu)
            }

            if (isAutoAlignItems) {
                var slot = 10
                for (button in buttons) {
                    if ((slot < minSlot) || (slot > maxSlot)) {
                        slot++
                        continue
                    }
                    slot.centralized()
                    var icon = button.getIcon(player)

                    if (isTranslateIcon) {
                        icon = Mine.getReplacers(icon, player)
                    }
                    menu.setItem(slot, icon)

                    slot++
                }
            } else {

                for (button in buttons) {
                    if (button.page != page) continue
                    var slot = button.index
                    if (slot > maxSlot) {
                        slot = maxSlot
                    }
                    var icon = button.getIcon(player)
                    if (isTranslateIcon) {
                        icon = Mine.getReplacers(icon, player)
                    }
                    menu.setItem(slot, icon)
                }
            }
            player.closeInventory()
            pageOpened[player] = page
            player.openInventory(menu)
            if (isCacheInventories && !pagesCache.containsKey(page)) {
                pagesCache[page] = menu
            }


            return menu
        }
        return Mine.newInventory("Null", 9)

    }

    override fun register(plugin: Plugin) {
        if (isRegistered) return
        super.register(plugin)
        registeredMenus.add(this)
        for (button in buttons) {
            button.parentMenu = this
            if (button.isCategory) {
                button.menu?.superiorMenu = this
                button.menu?.register(plugin)

            }

        }
    }

    override fun unregisterListener() {
        if (!isRegistered) return
        super.unregisterListener()
        registeredMenus.remove(this)
        for (button in buttons) {
            if (button.isCategory) {
                button.menu?.unregisterListener()
            }

        }

    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        val p = e.player
        if (p.itemInHand == null)
            return

        if (openWithItem != null && Mine.equals(p.itemInHand, openWithItem)) {
            e.isCancelled = true
            open(p)
        }

    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message = event.message
        val cmd = Extra.getCommandName(message)
        if (openWithCommand != null)
            if (cmd.toLowerCase() == "/" + openWithCommand!!.toLowerCase()) {
                event.isCancelled = true
                open(player)
            }
    }

    @EventHandler
    fun close(e: InventoryCloseEvent) {
        if (e.player is Player) {
            val p = e.player as Player

            if (pageOpened.containsKey(p)) {
                pageOpened.remove(p)
            }

        }
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (e.whoClicked is Player) {

            val player = e.whoClicked as Player

            if (isOpen(player)) {
                debug("Nome do Menu: " + e.inventory.name)
                e.isCancelled = true
                val slot = e.rawSlot
                var page: Int = getPageOpen(player)
                val itemClicked = e.currentItem
                var button: MenuButton? = null
                if (itemClicked != null) {

                    if (previousPage.item == itemClicked) {
                        open(player, (--page))
                        return
                    } else if (nextPage.item == itemClicked) {
                        open(player, (++page))
                        return
                    } else if (backPage.item == itemClicked) {
                        if (superiorMenu != null) {
                            superiorMenu?.open(player)
                        }
                        return
                    } else {
                        button = getButton(itemClicked, player)
                        debug("Button by Item " + if (button == null) "is Null" else "is not null")
                    }
                }
                if (button == null) {
                    button = getButton(page, slot)
                    debug("Button by Slot " + if (button == null) "is Null" else "is not null")
                }

                if (button != null) {
                    debug("Button is not null")
                    button.click.accept(e)
                    if (button.effects != null) {
                        debug("Button make Editable Effects")
                        button.effects?.effect(player)
                    }
                    if (button.isCategory) {
                        button.menu?.open(player)
                        debug("Button open another menu")
                        return
                    }
                }
                if (effect != null) {
                    debug("Played menu effect")
                    effect?.accept(e)
                }
            }
        }

    }


    override fun isOpen(player: Player): Boolean {
        return pageOpened.containsKey(player)
    }

    override fun getPageOpen(player: Player): Int {
        return pageOpened.getOrDefault(player, 0)
    }

    companion object {
        var isDebug = true
        val registeredMenus = ArrayList<Menu>()


        fun debug(msg: String) {
            if (isDebug) {
                Mine.console("§b[Menu] §7$msg")
            }

        }


    }


}
