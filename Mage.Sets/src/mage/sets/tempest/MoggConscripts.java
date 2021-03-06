/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.tempest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.RestrictionEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Rarity;
import mage.constants.WatcherScope;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.stack.Spell;
import mage.watchers.Watcher;

/**
 *
 * @author fireshoes
 */
public class MoggConscripts extends CardImpl {

    public MoggConscripts(UUID ownerId) {
        super(ownerId, 189, "Mogg Conscripts", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{R}");
        this.expansionSetCode = "TMP";
        this.subtype.add("Goblin");

        this.color.setRed(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Mogg Conscripts can't attack unless you've cast a creature spell this turn.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new MoggConscriptsEffect()), new PlayerCastCreatureWatcher());
    }

    public MoggConscripts(final MoggConscripts card) {
        super(card);
    }

    @Override
    public MoggConscripts copy() {
        return new MoggConscripts(this);
    }
}

class MoggConscriptsEffect extends RestrictionEffect {

    public MoggConscriptsEffect() {
        super(Duration.WhileOnBattlefield);
        staticText = "{this} can't attack unless you've cast a creature spell this turn";
    }

    public MoggConscriptsEffect(final MoggConscriptsEffect effect) {
        super(effect);
    }

    @Override
    public MoggConscriptsEffect copy() {
        return new MoggConscriptsEffect(this);
    }

    @Override
    public boolean canAttack(Game game) {
        return false;
    }

    @Override
    public boolean applies(Permanent permanent, Ability source, Game game) {
        if (permanent.getId().equals(source.getSourceId())) {
            PlayerCastCreatureWatcher watcher = (PlayerCastCreatureWatcher) game.getState().getWatchers().get("PlayerCastCreature");
            if (watcher != null && !watcher.playerDidCastCreatureThisTurn(source.getControllerId())) {
                return true;
            }
        }
        return false;
    }
}

class PlayerCastCreatureWatcher extends Watcher {

    Set<UUID> playerIds = new HashSet<>();

    public PlayerCastCreatureWatcher() {
        super("PlayerCastCreature", WatcherScope.GAME);
    }

    public PlayerCastCreatureWatcher(final PlayerCastCreatureWatcher watcher) {
        super(watcher);
        this.playerIds.addAll(watcher.playerIds);
    }

    @Override
    public void watch(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.SPELL_CAST) {
            Spell spell = (Spell) game.getObject(event.getTargetId());
            if (spell.getCardType().contains(CardType.CREATURE)) {
                playerIds.add(spell.getControllerId());
            }
        }
    }

    @Override
    public PlayerCastCreatureWatcher copy() {
        return new PlayerCastCreatureWatcher(this);
    }

    @Override
    public void reset() {
        super.reset();
        playerIds.clear();
    }

    public boolean playerDidCastCreatureThisTurn(UUID playerId) {
        return playerIds.contains(playerId);
    }
}