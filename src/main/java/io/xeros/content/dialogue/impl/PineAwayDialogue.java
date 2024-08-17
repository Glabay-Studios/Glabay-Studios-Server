package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class PineAwayDialogue extends DialogueBuilder {

    public PineAwayDialogue(Player player, NPC npc) {
        super(player);
        setNpcId(npc.getNpcId());


        sad("I have come curiously close to the end, though");
        sad("Beneath my self-indulgent pitiful hole");
        sad("Defeated");
        sad("I concede and move closer");
        sad("I may find comfort here");
        sad("I may find peace within the emptiness");
        sad("How pitiful");
        sad("It's calling me");
        sad("It's calling me");
        sad("It's calling me");
        sad("It's calling me");

        sad("And in my darkest moment, feeble and weeping");
        sad("The moon tells me a secret, a confidant");
        sad("As full and bright as I am");
        sad("This light is not my own and");
        sad("A million light reflections pass over me");
        sad("The source is bright and endless");
        sad("She resuscitates the hopeless");
        sad("Without her, we are lifeless satellites drifting");
        optimistic("And as I pull my head out, I am without one doubt");
        optimistic("Don't want to be down here soothing my narcissism, I");
        optimistic("Must crucify the ego before it's far too late");
        optimistic("I pray the light lifts me out");

        sad("Before I pine away");
        sad("Before I pine away");
        sad("Before I pine away");
        sad("Before I pine away");

        sad("So crucify the ego, before it's far too late");
        sad("And leave behind this place so negative and blind and cynical");
        sad("And you will come to find that we are all one mind");
        sad("Capable of all that's imagined and all conceivable");
        sad("So let the light touch you so that the words spill through");
        sad("let the past break through, bringing out our hope and reason");

        exit(player1 -> fadeOut(player1, npc));
    }

    private void fadeOut(Player player, NPC npc) {
        player.start(new DialogueBuilder(player).setNpcId(npc.getNpcId()).npc("Before we pine away").exit(player1 -> fadeOut(player, npc)));
    }

    private void optimistic(String...text) {
        npc(DialogueExpression.SPEAKING_CALMLY, text);
    }

    private void sad(String...text) {
        npc(DialogueExpression.NEARLY_CRYING, text);
    }
}
