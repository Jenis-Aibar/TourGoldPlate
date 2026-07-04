package com.tourist.tourGoldPlate.plate;

import org.bukkit.block.Block;

import java.util.UUID;

public class PlateData {
    private static Block plateBlock;
    private static UUID currentPlayer;

    // GETTER`s
    public Block getPlateBlock() {
        return plateBlock;
    }
    public UUID getCurrentPlayer () {
        return currentPlayer;
    }

    // SETTER`s
    public void setPlateBlock (Block block) {
        plateBlock = block;
    }
    public void setCurrentPlayer (UUID id) {
        currentPlayer = id;
    }

}
