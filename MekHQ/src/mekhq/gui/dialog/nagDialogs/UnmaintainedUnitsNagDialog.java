/*
 * Copyright (C) 2021-2025 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * A copy of the GPL should have been included with this project;
 * if not, see <https://www.gnu.org/licenses/>.
 *
 * NOTICE: The MegaMek organization is a non-profit group of volunteers
 * creating free software for the BattleTech community.
 *
 * MechWarrior, BattleMech, `Mech and AeroTech are registered trademarks
 * of The Topps Company, Inc. All Rights Reserved.
 *
 * Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of
 * InMediaRes Productions, LLC.
 */
package mekhq.gui.dialog.nagDialogs;

import mekhq.MHQConstants;
import mekhq.MekHQ;
import mekhq.campaign.Campaign;
import mekhq.gui.baseComponents.AbstractMHQNagDialog;

import static mekhq.gui.dialog.nagDialogs.nagLogic.UnmaintainedUnitsNagLogic.campaignHasUnmaintainedUnits;

/**
 * A nag dialog that alerts the user about unmaintained units in the campaign's hangar.
 *
 * <p>
 * This dialog identifies units that require maintenance but have not received it yet,
 * excluding units marked as salvage. It provides a reminder to the player to keep
 * active units in proper working order.
 * </p>
 */
public class UnmaintainedUnitsNagDialog extends AbstractMHQNagDialog {
    /**
     * Constructs the nag dialog for unmaintained units.
     *
     * <p>
     * This constructor initializes the dialog with relevant campaign details and
     * formats the displayed message to include context for the commander.
     * </p>
     *
     * @param campaign The {@link Campaign} object representing the current campaign.
     */
    public UnmaintainedUnitsNagDialog(final Campaign campaign) {
        super(campaign, MHQConstants.NAG_UNMAINTAINED_UNITS);

        final String DIALOG_BODY = "UnmaintainedUnitsNagDialog.text";
        setRightDescriptionMessage(String.format(resources.getString(DIALOG_BODY),
            campaign.getCommanderAddress(false)));
        showDialog();
    }

    /**
     * Checks if a nag dialog should be displayed for the inability to afford loan payments in the given campaign.
     *
     * <p>The method evaluates the following conditions to determine if the nag dialog should appear:</p>
     * <ul>
     *     <li>If the nag dialog for the inability to afford loan payments has not been ignored in the user options.</li>
     *     <li>If the campaign is unable to afford its loan payments.</li>
     * </ul>
     *
     * @param campaign the {@link Campaign} to check for nagging conditions
     * @return {@code true} if the nag dialog should be displayed, {@code false} otherwise
     */
    public static boolean checkNag(Campaign campaign) {
        final String NAG_KEY = MHQConstants.NAG_UNMAINTAINED_UNITS;

        return campaign.getCampaignOptions().isCheckMaintenance()
            && !MekHQ.getMHQOptions().getNagDialogIgnore(NAG_KEY)
            && campaignHasUnmaintainedUnits(campaign);
    }
}
