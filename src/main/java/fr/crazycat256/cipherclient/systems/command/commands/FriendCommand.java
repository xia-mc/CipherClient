/*
 * This file is part of CipherClient (https://github.com/crazycat256/CipherClient).
 * Copyright (c) crazycat256.
 */

package fr.crazycat256.cipherclient.systems.command.commands;

import fr.crazycat256.cipherclient.systems.command.Command;
import fr.crazycat256.cipherclient.systems.friend.Friend;
import fr.crazycat256.cipherclient.systems.friend.Friends;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiPlayerInfo;

/**
 * Copied from <a href="https://github.com/radioegor146/ehacks-pro/blob/master/src/main/java/ehacks/mod/commands/classes/FriendsCommand.java">ehacks-pro</a>
 * @author radioegor146
 */
public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "Edit friend list", "<add|list|remove|clear> [nickname]");
    }

    @Override
    public void process(String[] args) {
        ArrayList<String> friends = Friends.get().getFriends();
        if (args.length > 0) {
            if (args[0].equals("add") && args.length > 1) {
                if (!friends.contains(args[1])) {
                    Friends.get().add(new Friend(args[1]));
                    info("Successfully added " + args[1]);
                } else {
                    error("Friend already exists");
                }
                Friends.get().save();
                return;
            }
            if (args[0].equals("list")) {
                info("Friendlist:");
                int i = 1;
                for (String name : friends) {
                    info(i + ". &f" + name);
                    i++;
                }
                if (i == 1) {
                    info("You have no friends");
                }
                info("Tip: you can use MCF to add friends");
                return;
            }
            if (args[0].equals("clear")) {
                Friends.get().clear();
                info("Successfully cleared");
                Friends.get().save();
                return;
            }
            if (args[0].equals("remove") && args.length > 1) {
                Friend friend = Friends.get().getFriend(args[1]);
                if (friend != null) {
                    Friends.get().remove(friend);
                    info("Successfully removed " + args[1]);
                } else {
                   error("No such nick in friends");
                }
                Friends.get().save();
                return;
            }
        }
        sendHelp();
    }

    private boolean contains(Object[] array, Object object) {
        for (Object o : array) {
            if (o.equals(object)) {
                return true;
            }
        }
        return false;
    }

    private String[] getTabList() {
        @SuppressWarnings("unchecked")
        List<GuiPlayerInfo> players = mc.thePlayer.sendQueue.playerInfoList;
        ArrayList<String> playerNicks = new ArrayList<>();
        for(GuiPlayerInfo playerInfo : players) {
            playerNicks.add(playerInfo.name);
        }
        return playerNicks.toArray(new String[0]);
    }

    @Override
    public String[] autoComplete(String[] args) {
        ArrayList<String> friends = Friends.get().getFriends();
        if (args.length == 0) {
            return new String[]{"add", "list", "remove", "clear"};
        }
        if (args.length == 1) {
            if (contains(new String[]{"add", "list", "remove", "clear"}, args[0])) {
                if ("add".equals(args[0])) {
                    return getTabList();
                }
                if ("clear".equals(args[0])) {
                    return new String[0];
                }
                if ("remove".equals(args[0])) {
                    return Friends.get().getFriends().toArray(new String[0]);
                }
            } else {
                ArrayList<String> availableNames = new ArrayList<>();
                for (String name : new String[]{"add", "list", "remove", "clear"}) {
                    if (name.startsWith(args[0])) {
                        availableNames.add(name);
                    }
                }
                return availableNames.toArray(new String[0]);
            }
        }
        if (args.length == 2) {
            if (args[0].equals("add")) {
                ArrayList<String> availableNames = new ArrayList<>();
                for (String name : getTabList()) {
                    if (!friends.contains(name) && name.startsWith(args[0])) {
                        availableNames.add(name);
                    }
                }
                return availableNames.toArray(new String[0]);
            }
            if (args[0].equals("remove")) {
                ArrayList<String> availableNames = new ArrayList<>();
                for (String name : friends) {
                    if (name.startsWith(args[0])) {
                        availableNames.add(name);
                    }
                }
                return availableNames.toArray(new String[0]);
            }
        }
        return new String[0];
    }

}
