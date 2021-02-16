package com.chaos.eki_lib.gui.widget

import com.chaos.eki_lib.gui.screen.BaseScreen
import com.chaos.eki_lib.gui.screen.StationOverviewScreen
import com.chaos.eki_lib.station.data.Station
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.EntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

@Environment(EnvType.CLIENT)
class StationSelectionWidget(client: MinecraftClient, val parent: BaseScreen, stations: MutableList<Station>) :
    EntryListWidget<StationSelectionWidget.StationEntry>(
        client,
        client.window.scaledWidth,
        client.window.scaledHeight,
        32,
        client.window.scaledHeight - 61,
        18
    ) {
    private val textRenderer: TextRenderer = client.textRenderer

    init {
        for (station in stations)
            addEntry(StationEntry(station, textRenderer))

        if (selected != null)
            centerScrollOn(selected)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val k = this.rowLeft
        val l = top + 4 - scrollAmount.toInt()

        renderList(matrices, k, l, mouseX, mouseY, delta)
    }

    override fun getScrollbarPositionX(): Int = super.getScrollbarPositionX() + 20

    override fun getRowWidth(): Int = super.getRowWidth() + 50

    override fun isFocused(): Boolean = parent.focused == this

    inner class StationEntry(val station: Station, private val textRenderer: TextRenderer) :
        EntryListWidget.Entry<StationSelectionWidget.StationEntry>() {
        override fun render(
            matrices: MatrixStack?,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            val text = station.format()
            textRenderer.drawWithShadow(
                matrices,
                text,
                (width / 2 - textRenderer.getWidth(text) / 2).toFloat(),
                (y + 1).toFloat(),
                Formatting.WHITE.colorValue!!
            )
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (parent is StationOverviewScreen) {
                if (button == 0) {
                    parent.bindButton.active = true
                    parent.listWidget.selected = this
                    return true
                }
                return false
            }
            return false
        }
    }
}