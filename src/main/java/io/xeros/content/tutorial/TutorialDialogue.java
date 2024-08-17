package io.xeros.content.tutorial;

import java.util.function.Consumer;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.items.Starter;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.PlayerMovementState;
import io.xeros.model.entity.player.PlayerMovementStateBuilder;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.entity.player.mode.group.GroupIronman;

public class TutorialDialogue extends DialogueBuilder {

    public static final int TUTORIAL_NPC = 3248;
    private static final String IN_TUTORIAL_KEY = "in_tutorial";
    private static final DialogueOption[] XP_RATES = {
            new DialogueOption("Fast Xp Rate (standard) ", p -> chosenXpRate(p, ModeType.STANDARD)),
            new DialogueOption("5x Xp Rate (rogue)", p -> chosenXpRate(p, ModeType.ROGUE))
    };

    public static boolean inTutorial(Player player) {
        return player.getAttributes().getBoolean(IN_TUTORIAL_KEY);
    }

    private static void setInTutorial(Player player, boolean inTutorial) {
        player.getAttributes().setBoolean(IN_TUTORIAL_KEY, inTutorial);
        if (inTutorial) {
            player.setMovementState(new PlayerMovementStateBuilder().setLocked(true).createPlayerMovementState());
        } else {
            player.setMovementState(PlayerMovementState.getDefault());
        }
    }

    public static void selectedMode(Player player, ModeType mode) {
        Consumer<Player> chooseExpRate = p -> {
            if (mode == ModeType.STANDARD) {
                chooseExperienceRate(player);
            } else {
                finish(player, mode);
            }
        };

        player.start(new DialogueBuilder(player)
                .setNpcId(TUTORIAL_NPC)
                .npc("You've chosen " + mode.getFormattedName() + ", sound right?")
                .option(new DialogueOption("Yes, play " +mode.getFormattedName() + " mode.", chooseExpRate),
                        new DialogueOption("No, pick another game mode.", p -> p.getModeSelection().openInterface()))
        );
    }

    private static void chosenXpRate(Player player, ModeType mode) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("You've chosen the " + mode.getFormattedName() + " experience rate.", "Sound right?")
                .option(new DialogueOption("Yes, use " + mode.getFormattedName() + " experience rate.", p -> finish(p, mode)),
                        new DialogueOption("No.", TutorialDialogue::chooseExperienceRate)));
    }

    private static void chooseExperienceRate(Player player) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Select which experience type you want to use.").option(XP_RATES));
    }

    public static void finish(Player player, ModeType modeType) {
        switch (modeType) {
            case IRON_MAN:
                player.setMode(Mode.forType(ModeType.IRON_MAN));
                player.getRights().setPrimary(Right.IRONMAN);
                break;
            case ULTIMATE_IRON_MAN:
                player.setMode(Mode.forType(ModeType.ULTIMATE_IRON_MAN));
                player.getRights().setPrimary(Right.ULTIMATE_IRONMAN);
                break;
            case HC_IRON_MAN:
                player.setMode(Mode.forType(ModeType.HC_IRON_MAN));
                player.getRights().setPrimary(Right.HC_IRONMAN);
                break;
            case OSRS:
                player.setMode(Mode.forType(ModeType.OSRS));
                player.getRights().setPrimary(Right.OSRS);
                break;
            case ROGUE:
                player.setMode(Mode.forType(ModeType.ROGUE));
                player.getRights().setPrimary(Right.ROGUE);
                break;
            case ROGUE_HARDCORE_IRONMAN:
                player.setMode(Mode.forType(ModeType.ROGUE_HARDCORE_IRONMAN));
                player.getRights().setPrimary(Right.ROGUE_HARDCORE_IRONMAN);
                break;
            case GROUP_IRONMAN:
                player.setMode(Mode.forType(ModeType.GROUP_IRONMAN));
                player.getRights().setPrimary(Right.GROUP_IRONMAN);
                break;
            default:
                player.setMode(Mode.forType(ModeType.STANDARD));
                break;
        }

        player.getPA().requestUpdates();
        setInTutorial(player, false);
        Starter.addStarter(player);
        player.setCompletedTutorial(true);

        if (player.getRights().contains(Right.GROUP_IRONMAN)) {
            GroupIronman.moveToFormingLocation(player);
            return;
        }

        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Enjoy your stay on " + Configuration.SERVER_NAME + "!"));
        PlayerHandler.executeGlobalMessage("[@blu@New Player@bla@] " + player.getDisplayNameFormatted() + " @bla@has logged in! Welcome!");
    }

    public TutorialDialogue(Player player, boolean repeat) {
        super(player);

        setNpcId(TUTORIAL_NPC);
        if (!Server.isTest()) {
            npc(new Position(3090, 3492), "Welcome to " + Configuration.SERVER_NAME + "!", "Here is our home area!", "Don't forgot to join our ::discord.");
            npc(new Position(3108, 3495), "Here you can find all the shops needed", "when you first start out! You can buy combat gear,", "foods and pots, or show off your fashion skills!");
            npc(new Position(3095, 3504), "Receive your daily login rewards here!");
            npc(new Position(3094, 3504), "Here is the vote chest.", "After voting for all sites 10 times you get a @blu@vote key@bla@!", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(3092, 3504), "Here is the corrupt chest.", "There are 2 Wildy bosses that can be killed for keys!", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(3080, 3510), "This is the Outlast Portal.", "Anybody can join, any level or game mode!", "Use the Quest Tab to see when the next", "event will happen!");
            npc(new Position(3081, 3499), "Here you can get Slayer tasks and spend Boss points!");
            npc(new Position(3075, 3497), "This is the @red@Fire of Exchange@bla@.", "Use items on it to earn exchange points", "which can be used to buy custom pets!");
            npc(new Position(3080, 3471), "This is where you can plant seeds after defeating", "the world boss Hespori, which is displayed in your quest tab.");
            npc(new Position(3105, 3480), "If you decide to be a restricted game mode", "you can use the shops here!", "Including a UIM Storage chest!");
            npc(new Position(3088, 3487), "Finally, change your character style here!", "And don't forget you can use the teleport platform", "at home to open the Teleport Menu!");
        }
        if (!repeat) {
            npc("One last thing, be sure to @blu@set an account pin with ::pin@bla@!", "@blu@You will gain one hour of bonus xp scrolls!",
                    "You only have to enter it when you login", "on a different computer.");
            npc("You've made it through!", "You have the option to play as an <col=" + Right.IRONMAN + "><img=12></img>Iron Man</col>, <col=" + Right.GROUP_IRONMAN + "><img=27></img>GIM</col>",
                    "<col=" + Right.ULTIMATE_IRONMAN + "><img=13></img>Ultimate Iron Man</col>, <col=" + Right.HC_IRONMAN
                            + "><img=9></img>Hardcore Iron Man</col>, or neither.", "Choose from the following interface.");
            exit(p -> p.getModeSelection().openInterface());
        }

        // Then "Please decide what experience rate you would like to have."
        // Then option ["Fast Xp Rates", "5x", "1x"]
        // You have chosen X xp rates, good luck!
    }

    @Override
    public void initialise() {
        setInTutorial(getPlayer(), true);
        super.initialise();
    }

    private void npc(Position teleport, String...text) {
        npc(text).action(player -> player.moveTo(teleport));
    }

    /*
    case 647:
			String modetype = c.getMode().getType().toString();
			if (c.getMode().getType().equals(ModeType.REGULAR)) {
				sendNpcChat("Please decide what experience rates you would like to have.");
				c.nextChat = 2647;
			} else if (c.getMode().getType().equals(ModeType.OSRS)) {
				sendNpcChat("You have chosen the OSRS mode, with x1 experience rates.");
			} else if (c.getMode().getType().equals(ModeType.ROGUE)) {
				sendNpcChat("You have chosen the 5x mode, with x5 experience rates.");
			} else {
				sendNpcChat("You have chosen the mode " + modetype.replace("_", " ") + ".",
						"Take this set of armor to help you on your way.");
				c.nextChat = -1;
				c.getTutorial().proceed();
			}

			break;

			case 3647:
			sendNpcChat("You have chosen fast xp rates, good luck!");
			c.getTutorial().proceed();

			break;
		case 4647:
			c.setMode(Mode.forType(ModeType.OSRS));
			c.getRights().setPrimary(Right.OSRS);
			sendNpcChat("You have chosen @red@Extreme Rates @bla@,good luck!");
			c.getTutorial().proceed();

			break;
		case 5647:
			c.setMode(Mode.forType(ModeType.ROGUE));
			c.getRights().setPrimary(Right.ROGUE);
			sendNpcChat("You have chosen @red@5x Rates @bla@,good luck!");
			c.getTutorial().proceed();
			break;
     */
}
