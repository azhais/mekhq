/*
 * Copyright (C) 2020-2025 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MekHQ is distributed in the hope that it will be useful,
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
 *
 * MechWarrior Copyright Microsoft Corporation. MekHQ was created under
 * Microsoft's "Game Content Usage Rules"
 * <https://www.xbox.com/en-US/developers/rules> and it is not endorsed by or
 * affiliated with Microsoft.
 */
package mekhq.gui.stratcon;

import mekhq.MekHQ;
import mekhq.campaign.Campaign;
import mekhq.campaign.force.CombatTeam;
import mekhq.campaign.force.Force;
import mekhq.campaign.icons.enums.OperationalStatus;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.unit.Unit;
import mekhq.utilities.ReportingUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

import static mekhq.campaign.icons.enums.OperationalStatus.NOT_OPERATIONAL;
import static mekhq.campaign.personnel.turnoverAndRetention.Fatigue.getEffectiveFatigue;
import static mekhq.utilities.MHQInternationalization.getFormattedTextAt;
import static mekhq.utilities.ReportingUtilities.CLOSING_SPAN_TAG;
import static mekhq.utilities.ReportingUtilities.spanOpeningWithCustomColor;

/**
 * Handles rendering of individual lances in the StratCon scenario wizard.
 * @author NickAragua
 */
public class ScenarioWizardLanceRenderer extends JLabel implements ListCellRenderer<Force> {
    final private String RESOURCE_BUNDLE = "mekhq.resources." + getClass().getSimpleName();

    private final Campaign campaign;

    public ScenarioWizardLanceRenderer(Campaign campaign) {
        this.campaign = campaign;
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends Force> list, final Force force,
                                                  final int index, final boolean isSelected,
                                                  final boolean cellHasFocus) {
        // JTextArea::setForeground and JTextArea::setBackground don't work properly with the
        // default return on all themes, but by recreating the colour it works properly
        final Color foreground = new Color((isSelected
                ? list.getSelectionForeground() : list.getForeground()).getRGB());
        final Color background = new Color((isSelected
                ? list.getSelectionBackground() : list.getBackground()).getRGB());
        setForeground(foreground);
        setBackground(background);

        // Determine name color
        OperationalStatus operationalStatus = force.updateForceIconOperationalStatus(campaign).get(0);

        String statusOpenFormat = switch (operationalStatus) {
            case NOT_OPERATIONAL -> "<s>";
            case MARGINALLY_OPERATIONAL -> spanOpeningWithCustomColor(
                ReportingUtilities.getNegativeColor());
            case SUBSTANTIALLY_OPERATIONAL -> spanOpeningWithCustomColor(
                ReportingUtilities.getWarningColor());
            case FULLY_OPERATIONAL, FACTORY_FRESH -> spanOpeningWithCustomColor(
                ReportingUtilities.getPositiveColor());
        };

        String statusCloseFormat = operationalStatus == NOT_OPERATIONAL ? "</s>" : CLOSING_SPAN_TAG;

        // Get combat role
        CombatTeam combatTeam = campaign.getCombatTeamsTable().get(force.getId());
        String roleString = "";
        if (combatTeam != null) {
            roleString = combatTeam.getRole().toString() + ", ";
        }

        // Adjust force name to remove unnecessary information
        String forceName = force.getFullName();
        String originNodeName = ", " + campaign.getForce(0).getName();
        forceName = forceName.replaceAll(originNodeName, "");

        String fatigueReport = "";
        if (campaign.getCampaignOptions().isUseFatigue()) {
            int highestFatigue = 0;
            for (UUID unitId : force.getAllUnits(false)) {
                Unit unit = campaign.getUnit(unitId);

                if (unit == null) {
                    continue;
                }

                for (Person person : unit.getCrew()) {
                    if (person.getFatigue() > highestFatigue) {
                        highestFatigue = getEffectiveFatigue(person.getFatigue(), person.isClanPersonnel(),
                              person.getSkillLevel(campaign, false), campaign.getFieldKitchenWithinCapacity());
                    }
                }
            }
            fatigueReport = getFormattedTextAt(RESOURCE_BUNDLE, "fatigueReport.string", highestFatigue);
        }

        // Format string
        setText(getFormattedTextAt(RESOURCE_BUNDLE, "report.string", statusOpenFormat, force.getName(),
              statusCloseFormat, roleString, force.getTotalBV(campaign, true),
              fatigueReport, forceName));

        return this;
    }
}
