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
package mage.sets.limitedalpha;

import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.DamageEvent;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetSource;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author Quercitron
 */
public class JadeMonolith extends CardImpl {

    public JadeMonolith(UUID ownerId) {
        super(ownerId, 252, "Jade Monolith", Rarity.RARE, new CardType[]{CardType.ARTIFACT}, "{4}");
        this.expansionSetCode = "LEA";

        // {1}: The next time a source of your choice would deal damage to target creature this turn, that source deals that damage to you instead.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new JadeMonolithRedirectionEffect(), new GenericManaCost(1));
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);
    }

    public JadeMonolith(final JadeMonolith card) {
        super(card);
    }

    @Override
    public JadeMonolith copy() {
        return new JadeMonolith(this);
    }
}

class JadeMonolithRedirectionEffect extends ReplacementEffectImpl {

    private final TargetSource targetSource;
    
    public JadeMonolithRedirectionEffect() {
        super(Duration.EndOfTurn, Outcome.RedirectDamage);
        this.staticText = "The next time a source of your choice would deal damage to target creature this turn, that source deals that damage to you instead";
        this.targetSource = new TargetSource();
    }
    
    public JadeMonolithRedirectionEffect(final JadeMonolithRedirectionEffect effect) {
        super(effect);
        this.targetSource = effect.targetSource.copy();
    }

    @Override
    public JadeMonolithRedirectionEffect copy() {
        return new JadeMonolithRedirectionEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        this.targetSource.choose(Outcome.PreventDamage, source.getControllerId(), source.getSourceId(), game);
    }
    
    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Player you = game.getPlayer(source.getControllerId());
        Permanent targetCreature = game.getPermanent(source.getFirstTarget());
        MageObject sourceObject = game.getObject(source.getSourceId());
        DamageEvent damageEvent = (DamageEvent) event;
        if (you != null && targetCreature != null) {
            this.used = true;
            you.damage(damageEvent.getAmount(), damageEvent.getSourceId(), game, damageEvent.isCombatDamage(), damageEvent.isPreventable(), damageEvent.getAppliedEffects());
            
            StringBuilder sb = new StringBuilder(sourceObject != null ? sourceObject.getName() : "");
            sb.append(": ").append(damageEvent.getAmount()).append(" damage redirected from ").append(targetCreature.getName());
            sb.append(" to ").append(you.getName());
            game.informPlayers(sb.toString());
            
            return true;
        }
        return false;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (!this.used) {
            if (event.getType().equals(EventType.DAMAGE_CREATURE)) {
                if (event.getSourceId().equals(targetSource.getFirstTarget()) && event.getTargetId().equals(source.getFirstTarget())) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
