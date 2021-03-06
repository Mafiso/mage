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
package mage.sets.fatereforged;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterAttackingOrBlockingCreature;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author fireshoes
 */
public class WhiskAway extends CardImpl {
    
    private static final FilterAttackingOrBlockingCreature filter = new FilterAttackingOrBlockingCreature();

    public WhiskAway(UUID ownerId) {
        super(ownerId, 57, "Whisk Away", Rarity.COMMON, new CardType[]{CardType.INSTANT}, "{2}{U}");
        this.expansionSetCode = "FRF";

        // Put target attacking or blocking creature on top of its owner's library.
        this.getSpellAbility().addEffect(new WhiskAwayEffect());
        this.getSpellAbility().addTarget(new TargetCreaturePermanent(filter));
    }

    public WhiskAway(final WhiskAway card) {
        super(card);
    }

    @Override
    public WhiskAway copy() {
        return new WhiskAway(this);
    }
}

class WhiskAwayEffect extends OneShotEffect {

    WhiskAwayEffect() {
        super(Outcome.Removal);
        staticText = "Put target attacking or blocking creature on top of its owner's library";
    }

    WhiskAwayEffect(final WhiskAwayEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent targetCreature = game.getPermanent(targetPointer.getFirst(game, source));
        if (targetCreature != null) {
            targetCreature.moveToZone(Zone.LIBRARY, source.getSourceId(), game, true);
            return true;
        }
        return false;
    }

    @Override
    public WhiskAwayEffect copy() {
        return new WhiskAwayEffect(this);
    }
}