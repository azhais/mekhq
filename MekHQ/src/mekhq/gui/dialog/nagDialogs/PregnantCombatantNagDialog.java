/*
 * Copyright (c) 2021 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ. If not, see <http://www.gnu.org/licenses/>.
 */
package mekhq.gui.dialog.nagDialogs;

import mekhq.MHQConstants;
import mekhq.MekHQ;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.Person;
import mekhq.gui.baseComponents.AbstractMHQNagDialog;

import javax.swing.*;

public class PregnantCombatantNagDialog extends AbstractMHQNagDialog {
    private static boolean isPregnantCombatant(Campaign campaign) {
        if (campaign.getActiveMissions(false).isEmpty()) {
            return false;
        }

        return campaign.getActivePersonnel().stream()
                .filter(Person::isPregnant)
                .anyMatch(p -> ((p.getUnit() != null) && (p.getUnit().getForceId() != -1)));
    }

    //region Constructors
    public PregnantCombatantNagDialog(final JFrame frame, final Campaign campaign) {
        super(frame, "PregnantCombatantNagDialog", "PregnantCombatantNagDialog.title",
                "PregnantCombatantNagDialog.text", campaign, MHQConstants.NAG_PREGNANT_COMBATANT);
    }
    //endregion Constructors

    @Override
    protected boolean checkNag() {
        return !MekHQ.getMHQOptions().getNagDialogIgnore(getKey())
                && isPregnantCombatant(getCampaign());
    }
}
