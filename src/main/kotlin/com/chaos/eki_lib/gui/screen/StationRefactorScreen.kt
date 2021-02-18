package com.chaos.eki_lib.gui.screen

import com.chaos.eki_lib.EkiLibClient
import com.chaos.eki_lib.station.data.OpCode
import com.chaos.eki_lib.station.data.Station
import com.chaos.eki_lib.station.data.StationLevel
import com.chaos.eki_lib.utils.extensions.asArray
import com.chaos.eki_lib.utils.extensions.format
import com.chaos.eki_lib.utils.handlers.StationManager
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

@Environment(EnvType.CLIENT)
class StationRefactorScreen(
    previous: Screen?,
    dimension: Identifier,
    player: PlayerEntity,
    private val targetStation: Station? = null
) : BaseScreen(TranslatableText("eki_lib.screen.modify_station"), previous, dimension, player) {
    private lateinit var stationNameTextField: TextFieldWidget
    private lateinit var createButton: ButtonWidget
    private val position: BlockPos = targetStation?.pos ?: player.blockPos
    private var level: StationLevel = targetStation?.level ?: StationLevel.THRID

    override fun init() {
        client?.keyboard?.setRepeatEvents(true)
        addButton(ButtonWidget(
            width / 2 - 110,
            height / 2 + 50,
            100,
            20,
            TranslatableText("eki_lib.screen.back")
        ) {
            client?.openScreen(previous)
        })
        createButton = addButton(ButtonWidget(
            width / 2 + 10,
            height / 2 + 50,
            100,
            20,
            TranslatableText("eki_lib.screen.${if (targetStation == null) "create" else "modify"}")
        ) {
            val station = Station(stationNameTextField.text, position, level, dimension)

            val operationSuccess = if (targetStation != null)
                StationManager.replaceStation(station)
            else
                StationManager.addStation(station)

            if (operationSuccess) {
                val data = PacketByteBuf(Unpooled.buffer())

                data.writeEnumConstant(if (targetStation != null) OpCode.REPLACE else OpCode.ADD)
                station.toByteBuf(data)

                ClientSidePacketRegistry.INSTANCE.sendToServer(
                    EkiLibClient.C2S_CLIENT_UPDATE_STATIONS,
                    data
                )
            }

            val stationName = LiteralText(stationNameTextField.text).formatted(Formatting.BOLD)
            val message = if (operationSuccess)
                TranslatableText("eki_lib.screen.${if (targetStation == null) "create" else "modify"}_success")
                    .append(stationName)
            else
                TranslatableText("eki_lib.screen.${if (targetStation == null) "create" else "modify"}_failed")
                    .append(stationName)
                    .formatted(Formatting.RED)

            player.sendMessage(message, true)
            onClose()
        })
        addButton(ButtonWidget(
            width / 2 + 110,
            height / 2 - 80,
            20,
            20,
            Text.of("<")
        ) {
            level = level.previous()
        })
        addButton(ButtonWidget(
            width / 2 + 150,
            height / 2 - 80,
            20,
            20,
            Text.of(">")
        ) {
            level = level.next()
        })
        super.init()
        stationNameTextField = TextFieldWidget(
            textRenderer,
            width / 2 - 100,
            height / 2 - 100,
            200,
            20,
            Text.of("")
        )
        stationNameTextField.setChangedListener {
            createButton.active = stationNameTextField.text.isNotEmpty()
        }
        addChild(stationNameTextField)

        if (targetStation != null)
            stationNameTextField.text = targetStation.name

        createButton.active = stationNameTextField.text.isNotEmpty()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
        stationNameTextField.render(matrices, mouseX, mouseY, delta)

        val white = Formatting.WHITE.colorValue!!

        drawCenteredText(
            matrices,
            textRenderer,
            title,
            width / 2,
            height / 2 - 130,
            white
        )
        drawCenteredText(
            matrices,
            textRenderer,
            TranslatableText("eki_lib.station.name"),
            width / 2,
            height / 2 - 110,
            white
        )
        drawTextWithShadow(
            matrices,
            textRenderer,
            TranslatableText("eki_lib.station.pos").append(": ${position.format()}"),
            width / 2 + 110,
            height / 2 - 110,
            white
        )
        drawTextWithShadow(
            matrices,
            textRenderer,
            TranslatableText("eki_lib.station.level").append(": ${level.toTranslated().string}"),
            width / 2 + 110,
            height / 2 - 90,
            white
        )
    }

    override fun onClose() {
        client?.keyboard?.setRepeatEvents(false)
        super.onClose()
    }
}