package com.chaos.eki_lib.gui.screen

import com.chaos.eki_lib.EkiLibClient
import com.chaos.eki_lib.gui.widget.StationSelectionWidget
import com.chaos.eki_lib.objects.items.StationTunerItem
import com.chaos.eki_lib.station.data.OpCode
import com.chaos.eki_lib.utils.handlers.StationManager
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
class StationOverviewScreen(previous: Screen?, dimension: Identifier, player: PlayerEntity) :
    BaseScreen(Text.of(""), previous, dimension, player) {
    lateinit var refactorButton: ButtonWidget
    lateinit var removeButton: ButtonWidget
    lateinit var bindButton: ButtonWidget
    lateinit var listWidget: StationSelectionWidget

    override fun init() {
        listWidget = addChild(
            StationSelectionWidget(
                client ?: MinecraftClient.getInstance(),
                this,
                StationManager.getStationList()
            )
        )
        refactorButton = addButton(ButtonWidget(
            width / 2 - 200,
            height - 50,
            100,
            20,
            TranslatableText("eki_lib.screen.create_modify")
        ) {
            client?.openScreen(
                StationRefactorScreen(
                    this,
                    dimension,
                    player,
                    listWidget.selected?.station
                )
            )
        })
        removeButton = addButton(ButtonWidget(
            width / 2 - 50,
            height - 50,
            100,
            20,
            TranslatableText("eki_lib.screen.remove")
        ) {
            val station = listWidget.focused?.station!!

            val operationSuccess = StationManager.removeStation(station.pos)

            if (operationSuccess) {
                val data = PacketByteBuf(Unpooled.buffer())
                data.writeEnumConstant(OpCode.DELETE)
                listWidget.focused?.station?.toByteBuf(data)

                ClientSidePacketRegistry.INSTANCE.sendToServer(
                    EkiLibClient.C2S_CLIENT_UPDATE_STATIONS,
                    data
                )
            }

            client?.openScreen(this)
        })
        bindButton = addButton(ButtonWidget(
            width / 2 + 100,
            height - 50,
            100,
            20,
            TranslatableText("eki_lib.screen.bind"),
            {

            }
        ) { _, p2, p3, p4 ->
            val texts = mutableListOf<Text>()
            texts += TranslatableText("eki_lib.screen.bind.description")

            if (player.mainHandStack.item !is StationTunerItem)
                texts += TranslatableText("eki_lib.screen.bind.warning").formatted(Formatting.RED)

            renderTooltip(
                p2,
                texts,
                p3,
                p4
            )
        })
        addButton(ButtonWidget(
            width - 60,
            5,
            20,
            20,
            Text.of("X"),
            {
                client?.openScreen(this)
            }
        ) { _, p2, p3, p4 ->
            renderTooltip(
                p2,
                TranslatableText("eki_lib.screen.clear_selection"),
                p3,
                p4
            )
        })
        super.init()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        listWidget.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, title, width / 2, 16, 16777215)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun tick() {
        if (listWidget.focused == null) {
            removeButton.active = false
            bindButton.active = false
        } else {
            removeButton.active = true
            bindButton.active = true
        }
    }

    override fun isPauseScreen(): Boolean = false
}