package com.amber.amberutils.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.amber.amberutils.AmberUtils;
import com.amber.amberutils.helpers.NameHelper;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityRanchBlock;

public class RanchOwnerCommand implements CommandExecutor {

    private Set<UUID> pending_ranchcheck = new HashSet<>();

    private final AmberUtils plugin;

    public RanchOwnerCommand(AmberUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("This command can only be executed by a player."));
            return CommandResult.empty();
        }

        Player player = (Player) src;
        UUID playerID = player.getUniqueId();

        if (!pending_ranchcheck.contains(playerID)) {
            pending_ranchcheck.add(playerID);
            player.sendMessage(Text.of(TextColors.GREEN, "Right-Click a ranch block to see the owner!"));
            
            Sponge.getScheduler().createTaskBuilder()
                    .delay(30, TimeUnit.SECONDS)
                    .execute(() -> {
                        if (pending_ranchcheck.contains(playerID)) {
                            pending_ranchcheck.remove(playerID);
                            Optional<Player> onlinePlayer = Sponge.getServer().getPlayer(playerID);
                            onlinePlayer.ifPresent(p -> p.sendMessage(Text.of(TextColors.RED, "Your pending ranch check has timed out.")));
                        }
                    })
                    .submit(plugin);
            return CommandResult.success();
        }

        player.sendMessage(Text.of(TextColors.RED, "You already have a pending ranch check."));
        return CommandResult.success();
    }

    public static CommandSpec buildSpec(RanchOwnerCommand cmd) {
        return CommandSpec.builder()
                .description(Text.of("Shows the UUID and name of the ranch block owner."))
                .permission("amberutils.command.ranchowner")
                .executor(cmd)
                .build();
    }

    @Listener
    public void onRanchInteract(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> playerOpt = event.getCause().first(Player.class);
        if (!playerOpt.isPresent()) {
            return;
        }
        Player player = playerOpt.get();
        UUID playerID = player.getUniqueId();

        if (!getPendingList().contains(playerID)) {
            return;
        }

        pending_ranchcheck.remove(playerID);

        Optional<Location<World>> locationOpt = event.getTargetBlock().getLocation();
        if (!locationOpt.isPresent()) {
            return;
        }
        Location<World> location = locationOpt.get();

        Optional<TileEntity> tileEntityOpt = location.getTileEntity();
        if (!tileEntityOpt.isPresent()) {
            player.sendMessage(Text.of(TextColors.RED, "This block does not have a tile entity."));
            return;
        }
        TileEntity tileEntity = tileEntityOpt.get();

        if (!(tileEntity instanceof TileEntityRanchBlock)) {
            player.sendMessage(Text.of(TextColors.RED, "This is not a ranch block."));
            return;
        }

        TileEntityRanchBlock ranchBlock = (TileEntityRanchBlock) tileEntity;
        UUID ownerID = ranchBlock.getOwnerUUID();
        String ownerName = NameHelper.getPlayerName(ownerID);
        player.sendMessage(Text.of(TextColors.YELLOW, "This ranch is owned by ", TextColors.GREEN , ownerName , ".\n(UUID:" + ownerID + ")"));
    }

    public Set<UUID> getPendingList() {
        return pending_ranchcheck;
    }
}
